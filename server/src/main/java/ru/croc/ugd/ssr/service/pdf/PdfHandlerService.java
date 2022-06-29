package ru.croc.ugd.ssr.service.pdf;

import ru.croc.ugd.ssr.service.pdf.model.MergeToPdfInput;

import java.util.List;

public interface PdfHandlerService<XML_DATA_OBJECT> {
    byte[] transformObjectToPdf(XML_DATA_OBJECT object, String transformationName);

    byte[] mergeToSinglePdf(List<MergeToPdfInput> mergeToPdfInputs);

    String extractPdfTextContent(byte[] pdfSource);

    byte[] retrievePdf(final byte[] unionFileSource, final List<Integer> pageNumbers);
}
