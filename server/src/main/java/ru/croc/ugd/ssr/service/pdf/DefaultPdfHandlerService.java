package ru.croc.ugd.ssr.service.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.service.pdf.model.MergeToPdfInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

@Service
@AllArgsConstructor
@Slf4j
public class DefaultPdfHandlerService<XML_DATA_OBJECT> implements PdfHandlerService<XML_DATA_OBJECT> {
    private XmlUtils xmlUtils;
    private PdfCreatorHelper pdfCreatorHelper;

    @Override
    public byte[] transformObjectToPdf(
        final XML_DATA_OBJECT object,
        final String transformationName
    ) {
        try {
            Assert.notNull(object, "object shouldn't be null");
            final FopFactory fopFactory = FopFactory.newInstance(getFopConfigFile());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            final TransformerFactory factory = TransformerFactory.newInstance();

            final Resource templateResource = new ClassPathResource("pdf/templates/" + transformationName);

            final Transformer transformer = factory
                .newTransformer(new StreamSource(templateResource.getInputStream()));
            final Result res = new SAXResult(fop.getDefaultHandler());

            final String xmlData = xmlUtils
                .transformObjectToXmlString(object, object.getClass())
                .replaceAll("xmlns=\".+\"", "")
                .replaceAll("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>", "");

            final String postProcessedXmlData = postProcessXmlInput(object, xmlData);

            if (StringUtils.isEmpty(postProcessedXmlData)) {
                throw new SsrException("Данные для создания pdf отсутствуют");
            }
            final InputStream targetStream = new ByteArrayInputStream(postProcessedXmlData.getBytes());
            final Source src = new StreamSource(targetStream);

            transformer.transform(src, res);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка создания pdf файла", e.getMessage(), e);
            throw new SsrException("Ошибка создания pdf файла");
        }
    }

    @Override
    public byte[] mergeToSinglePdf(final List<MergeToPdfInput> mergeToPdfInputs) {
        final PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();

        final List<InputStream> inputStreams = new ArrayList<>();
        for (MergeToPdfInput mergeToPdfInput : mergeToPdfInputs) {
            try {
                inputStreams.addAll(pdfCreatorHelper.convertInputToPdfInputStream(mergeToPdfInput));
            } catch (DocumentException | IOException e) {
                log.error("DefaultPdfCreatorService.mergeToSinglePdf: cant convert file of type "
                    + mergeToPdfInput.getFileFormat().name(), e.getMessage(), e);
            }
        }
        inputStreams.forEach(pdfMergerUtility::addSource);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        pdfMergerUtility.setDestinationStream(byteArrayOutputStream);
        try {
            pdfMergerUtility.mergeDocuments();
        } catch (IOException e) {
            log.error("Ошибка создания pdf файла", e.getMessage(), e);
            throw new SsrException("Ошибка создания pdf файла");
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String extractPdfTextContent(final byte[] pdfSource) {
        final InputStream inputStream = new ByteArrayInputStream(pdfSource);
        try (final PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
                stringBuilder.append(getPageContent(pdfDoc.getPage(i)));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            log.error("Ошибка парсинга pdf файла", e.getMessage(), e);
            throw new SsrException("Ошибка парсинга pdf файла");
        }
    }

    @Override
    public byte[] retrievePdf(final byte[] unionFileContent, final List<Integer> pageNumbers) {
        try (final PDDocument unionDocument = PDDocument.load(unionFileContent)) {
            try (final PDDocument document = new PDDocument()) {
                pageNumbers.forEach(pageNumber -> {
                    final int pageIndex = pageNumber - 1;
                    final PDPage page = unionDocument.getPage(pageIndex);
                    document.addPage(page);
                });
                try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    document.save(byteArrayOutputStream);
                    return byteArrayOutputStream.toByteArray();
                }
            } catch (IOException e) {
                log.error("Unable to save pdf file: {}", e.getMessage(), e);
                throw new SsrException("Ошибка сохранения pdf файла");
            }
        } catch (IOException e) {
            log.error("Unable to load pdf file: {}", e.getMessage(), e);
            throw new SsrException("Ошибка загрузки pdf файла");
        }
    }

    private String getPageContent(PdfPage page) {
        final LocationTextExtractionStrategy strategy = new LocationTextExtractionStrategy();
        final PdfCanvasProcessor parser = new PdfCanvasProcessor(strategy);
        parser.processPageContent(page);
        return strategy.getResultantText();
    }

    protected String postProcessXmlInput(final XML_DATA_OBJECT object, final String xmlDataInput) {
        return xmlDataInput;
    }

    private File getFopConfigFile() throws IOException {
        final InputStream configInputStream = DefaultPdfHandlerService.class
            .getClassLoader().getResourceAsStream("pdf/config.xml");
        final File tempConfigFile = File.createTempFile("TEMP", ".tmp");
        FileUtils.copyInputStreamToFile(configInputStream, tempConfigFile);
        return tempConfigFile;
    }
}
