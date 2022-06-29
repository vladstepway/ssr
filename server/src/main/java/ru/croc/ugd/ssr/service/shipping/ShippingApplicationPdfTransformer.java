package ru.croc.ugd.ssr.service.shipping;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.service.pdf.DefaultPdfHandlerService;
import ru.croc.ugd.ssr.service.pdf.PdfCreatorHelper;
import ru.croc.ugd.ssr.shipping.ShippingApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Service
public class ShippingApplicationPdfTransformer extends DefaultPdfHandlerService<ShippingApplication> {
    private static final String TEMPLATE_NAME = "shippingApplication.xsl";

    public ShippingApplicationPdfTransformer(
        XmlUtils xmlUtils,
        PdfCreatorHelper pdfCreatorHelper
    ) {
        super(xmlUtils, pdfCreatorHelper);
    }

    public byte[] transformToPdf(final ShippingApplication shippingApplication) {
        return super.transformObjectToPdf(shippingApplication, TEMPLATE_NAME);
    }

    @Override
    protected String postProcessXmlInput(
        final ShippingApplication shippingApplication,
        final String xmlDataInput
    ) {
        final Locale locale = new Locale("ru");
        final String timeFormat = "HH:mm";
        final String dateFormat = "dd MMMM YYYY, EEEE";
        final LocalDateTime start = shippingApplication
            .getShippingApplicationData()
            .getShippingDateStart();
        final LocalDateTime end = shippingApplication
            .getShippingApplicationData()
            .getShippingDateEnd();
        final String date = Optional.ofNullable(start)
            .map(localDateTime -> localDateTime.format(DateTimeFormatter.ofPattern(dateFormat, locale)))
            .orElse("-");
        final String timeStart = Optional.ofNullable(start)
            .map(localDateTime -> localDateTime.format(DateTimeFormatter.ofPattern(timeFormat, locale)))
            .orElse("-");
        final String timeEnd = Optional.ofNullable(end)
            .map(localDateTime -> localDateTime.format(DateTimeFormatter.ofPattern(timeFormat, locale)))
            .orElse("-");
        final String resultFormattedDateTime = date + ", c " + timeStart + " по " + timeEnd;
        return xmlDataInput.replaceAll(
            "<shippingDateStart>.*</shippingDateStart>",
            "<shippingDateStart>" + resultFormattedDateTime + "</shippingDateStart>");
    }
}
