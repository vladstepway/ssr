package ru.croc.ugd.ssr.solr.converter;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.HouseToResettle;
import ru.croc.ugd.ssr.HouseToSettle;
import ru.croc.ugd.ssr.ResettlementRequest;
import ru.croc.ugd.ssr.ResettlementRequestType;
import ru.croc.ugd.ssr.model.ResettlementRequestDocument;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.solr.UgdSsrResettlementRequest;
import ru.croc.ugd.ssr.solr.SsrDocumentConverter;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Реализация конвертера для Запроса на переселение.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResettlementRequestDocumentConverter
    extends SsrDocumentConverter<ResettlementRequestDocument, UgdSsrResettlementRequest> {

    private final DocumentConverterService service;

    @Override
    public DocumentType<ResettlementRequestDocument> getDocumentType() {
        return SsrDocumentTypes.RESETTLEMENT_REQUEST;
    }

    @Override
    public UgdSsrResettlementRequest convertDocument(@NotNull ResettlementRequestDocument document) {
        final UgdSsrResettlementRequest position = createDocument(getAnyAccessType(), document.getId());

        final ResettlementRequestType source = of(document.getDocument())
            .map(ResettlementRequest::getMain)
            .orElseThrow(() -> new SolrDocumentConversionException(document.getId()));

        String firstRow;
        if (source.isSendNotificationStep()) {
            firstRow = "Инициирование уведомлений о начале переселения по следующим отселяемым домам: ";
            Set<String> houses = new HashSet<>();
            for (HouseToSettle houseToSettle : source.getHousesToSettle()) {
                for (HouseToResettle houseToResettle : houseToSettle.getHousesToResettle()) {
                    if (StringUtils.isBlank(houseToResettle.getRealEstateUnom())) {
                        continue;
                    }
                    String unom = houseToResettle.getRealEstateUnom();
                    String address = getAddressByUnom(unom);

                    String house = StringUtils.isNotBlank(address) ? address + " " : "";
                    house += "(" + unom + ")";

                    houses.add(house);
                }
            }
            firstRow += String.join("; ", houses);
        } else {
            firstRow = "Черновик задачи инициирования переселения";
            if (source.getSaveDraftDate() != null) {
                firstRow += " от " + source.getSaveDraftDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            }
        }
        position.setResettlementRequestFirstRow(firstRow);

        return position;
    }

    private String getAddressByUnom(final String unom) {
        try {
            return service.getAddressByUnom(unom);
        } catch (Exception e) {
            log.warn("Unable to getAddressByUnom: {}", e.getMessage(), e);
            return null;
        }
    }

}
