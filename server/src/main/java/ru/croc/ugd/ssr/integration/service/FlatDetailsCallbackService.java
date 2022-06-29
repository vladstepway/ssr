package ru.croc.ugd.ssr.integration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.integration.config.IntegrationProperties;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.FetchFlatDetailsSoapService;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.FlatDetails;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing.Attribute;
import ru.croc.ugd.ssr.integration.service.wsdl.flat.model.external.xmlparsing.Package;
import ru.croc.ugd.ssr.integration.util.FileStringData;
import ru.croc.ugd.ssr.integration.util.FileUtils;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Slf4j
public class FlatDetailsCallbackService {
    private static final String GLOBAL_ID_ATTRIBUTE = "global_id";

    private ObjectMapper objectMapper;
    private IntegrationProperties integrationProperties;
    private FetchFlatDetailsSoapService fetchFlatDetailsSoapService;
    private CapitalConstructionObjectService capitalConstructionObjectService;
    private RealEstateDocumentService realEstateDocumentService;

    public void storeServicePushJson(String incomeJson) {
        FileUtils.writeStringFile(
            integrationProperties.getEhdCatalogPushUpdatesImport(),
            UUID.randomUUID().toString() + ".json",
            incomeJson);
    }

    @Scheduled(cron = "${schedulers.flat-details-processing.cron:0 0 0 * * ?}")
    public void processStoredFiles() {
        log.info("FlatDetailsCallbackService: scheduler triggered.");
        FileUtils.processAllFilesInDir(
            integrationProperties.getEhdCatalogPushUpdatesImport(),
            this::processFile
        );
    }

    private void processFile(final FileStringData fileStringData) {
        if (fileStringData == null || StringUtils.isEmpty(fileStringData.getFileContent())) {
            log.warn("FlatDetailsCallbackService: nothing to process.");
            return;
        }
        try {
            final Package thePackage = objectMapper.readValue(fileStringData.getFileContent(), Package.class);
            if (!isValidPackage(thePackage)) {
                log.warn("FlatDetailsCallbackService: package is not valid.");
                moveToNokFolder(fileStringData);
                return;
            }
            log.info("FlatDetailsCallbackService: processing file " + fileStringData.getFileAbsolutePath());
            handlePackage(thePackage);
        } catch (IOException e) {
            log.warn("FlatDetailsCallbackService: could not parse package. File: "
                + fileStringData.getFileAbsolutePath(), e);
            moveToNokFolder(fileStringData);
        } catch (Exception ex) {
            log.error("FlatDetailsCallbackService: Failure processing file: "
                + fileStringData.getFileAbsolutePath(), ex);
            moveToNokFolder(fileStringData);
        }
        FileUtils.removeFile(fileStringData.getFileAbsolutePath());
    }

    private void handlePackage(final Package thePackage) {
        thePackage
            .getCatalog()
            .getData()
            .getAttribute()
            .stream()
            .filter(Objects::nonNull)
            .filter(attribute -> GLOBAL_ID_ATTRIBUTE.equals(attribute.getName()))
            .findFirst()
            .ifPresent(this::processCatalogIdAttribute);
    }

    private void moveToNokFolder(final FileStringData fileStringData) {
        FileUtils.moveFile(
            fileStringData.getFileAbsolutePath(),
            integrationProperties.getEhdCatalogPushUpdatesFailed()
                + (LocalDateTime.now() + ".json").replaceAll(":", "_"));
    }

    private void processCatalogIdAttribute(final Attribute idAttribute) {
        if (!isValidAttribute(idAttribute)) {
            return;
        }
        final String globalId = idAttribute.getValues().getValue().getValue();
        final List<FlatDetails> flatDetails = fetchFlatDetailsSoapService
            .getFlatDetailsByGlobalId(globalId);
        processFlatDetails(flatDetails);
    }


    private boolean isValidAttribute(final Attribute attribute) {
        return attribute != null
            && attribute.getValues() != null
            && attribute.getValues().getValue() != null
            && attribute.getValues().getValue().getValue() != null;
    }

    private boolean isValidPackage(final Package thePackage) {
        return thePackage != null
            && thePackage.getCatalog() != null
            && thePackage.getCatalog().getData() != null
            && !CollectionUtils.isEmpty(thePackage.getCatalog().getData().getAttribute());
    }

    private void processFlatDetails(final List<FlatDetails> flatDetails) {
        if (CollectionUtils.isEmpty(flatDetails)) {
            return;
        }
        flatDetails
            .stream()
            .collect(Collectors.groupingBy(FlatDetails::getUnom))
            .forEach(this::updateFlatsForUnom);
    }

    private void updateFlatsForUnom(final String unom, final List<FlatDetails> flatDetails) {
        log.info("FlatDetailsCallbackService: Processing UNOM: " + unom + " from catalog 28609");
        if (flatDetails != null && !StringUtils.isEmpty(unom)) {
            if (flatDetails.size() == 1) {
                capitalConstructionObjectService.updateCcoFlatWithFlatDetail(unom, flatDetails.get(0));
            } else {
                capitalConstructionObjectService.updateCcoFlatWithFlatDetails(unom, flatDetails);
            }
            realEstateDocumentService.updateRealEstateByFlatDetails(unom, flatDetails);
        }
    }
}
