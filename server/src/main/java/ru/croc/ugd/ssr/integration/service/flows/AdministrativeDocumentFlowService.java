package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.OfferLetterParsedFlatData;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.AdministrativeDocumentsInfoMfrFlowService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.PersonMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPAdministrativeDocumentType.NewFlats.NewFlat;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.OfferLetterParsingDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;
import ru.croc.ugd.ssr.service.guardianship.GuardianshipService;
import ru.croc.ugd.ssr.service.offerletterparsing.OfferLetterParsingService;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class AdministrativeDocumentFlowService {

    public static final String ADMINISTRATIVE_DOC_FILE_TYPE = "3";

    public static final String OK_DGI_STATUS = "1";
    public static final String NOK_DGI_STATUS = "2";

    private final PersonDocumentService personDocumentService;
    private final DgiElkDeliveryStatusSendService dgiElkDeliveryStatusSendService;
    private final XmlUtils xmlUtils;
    private final PersonMapper personMapper;
    private final BeanFactory beanFactory;
    private final ChedFileService chedFileService;
    private final GuardianshipService guardianshipService;
    private final CapitalConstructionObjectService ccoService;
    private final OfferLetterParsingDocumentService offerLetterParsingDocumentService;
    private final OfferLetterParsingService offerLetterParsingService;
    private final AdministrativeDocumentsInfoMfrFlowService administrativeDocumentsInfoMfrFlowService;

    public void receiveAdministrativeDocument(
        final String eno,
        final String xmlMessage,
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        try {
            updatePersonWithNewDate(eno, xmlMessage, administrativeDocumentType);
        } catch (Exception e) {
            log.error("Unable to process administrative document (eno = {}): {}", eno, e.getMessage(), e);
        }
    }

    private void updatePersonWithNewDate(
        final String eno,
        final String xmlMessage,
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        log.debug("AdministrativeDocumentFlowService: updating person with"
                + " received administrative document. Eno: {}, personId: {}",
            eno, administrativeDocumentType.getPersonId());
        fetchPersonDocumentAndReportFlowErrorIfRequired(administrativeDocumentType, xmlMessage)
            .ifPresent(personDocument -> handleIncomeDataForPerson(eno,
                xmlMessage,
                administrativeDocumentType,
                personDocument));
    }

    private void handleIncomeDataForPerson(
        final String eno,
        final String xmlMessage,
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType,
        final PersonDocument personDocument
    ) {
        try {
            populatePersonWithIncomeData(eno,
                xmlMessage,
                administrativeDocumentType,
                personDocument
            );
            handleGuardianshipRequest(administrativeDocumentType);
        } catch (Exception ex) {
            dgiElkDeliveryStatusSendService.sendAdStatusToDgi(personDocument,
                administrativeDocumentType.getLetterId(),
                NOK_DGI_STATUS);
            log.error(ex.getMessage(), ex);
            return;
        }
        dgiElkDeliveryStatusSendService.sendAdStatusToDgi(personDocument,
            administrativeDocumentType.getLetterId(),
            OK_DGI_STATUS);
    }

    private void handleGuardianshipRequest(
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        final String affairId = administrativeDocumentType.getAffairId();
        guardianshipService.checkGuardianshipRequestAndStartHandle(affairId);
    }

    private void populatePersonWithIncomeData(
        final String eno,
        final String xmlMessage,
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType,
        final PersonDocument personDocument
    ) {
        final PersonType personType = personDocument.getDocument().getPersonData();
        populateLegalRepresentatives(personType, administrativeDocumentType);
        populateOfferLetter(personType, administrativeDocumentType);
        populateNewFlats(personType, administrativeDocumentType);
        updateOfferLetterFiles(personDocument, administrativeDocumentType);
        personType.setAdministrativeDocumentsUploadDateTime(LocalDateTime.now());
        updateOfferLetterFlatData(personType, administrativeDocumentType);
        addLogMessageToPerson(personDocument, eno, xmlMessage);

        personDocumentService
            .updateDocument(personDocument.getId(), personDocument, true, true, null);

        final NewFlat newFlat = administrativeDocumentType.getNewFlats()
            .getNewFlat()
            .stream()
            .findFirst()
            .orElse(null);

        administrativeDocumentsInfoMfrFlowService.sendAdministrativeDocumentsInfo(
            personType, administrativeDocumentType.getLetterId(), newFlat
        );
    }

    private void updateOfferLetterFlatData(
        final PersonType personType, final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        if (personType.getOfferLetters() == null) {
            return;
        }

        final SuperServiceDGPAdministrativeDocumentType.NewFlats incomeNewFlats = administrativeDocumentType
            .getNewFlats();

        if (incomeNewFlats != null) {
            incomeNewFlats
                .getNewFlat()
                .forEach(newFlat -> updateFlatData(
                    personType.getOfferLetters(), administrativeDocumentType.getLetterId(), newFlat));

            finishOfferLetterParsingBpmProcessIfNeeded(administrativeDocumentType);
        }
    }

    private void finishOfferLetterParsingBpmProcessIfNeeded(
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        final OfferLetterParsingDocument offerLetterParsingDocument =
            offerLetterParsingDocumentService.fetchByLetterIdAndAffairId(
                administrativeDocumentType.getLetterId(), administrativeDocumentType.getAffairId()
            );
        ofNullable(offerLetterParsingDocument)
            .ifPresent(offerLetterParsingService::finishBpmProcessIfNeeded);
    }

    private void updateFlatData(
        final PersonType.OfferLetters offerLetters,
        final String letterId,
        final NewFlat newFlat
    ) {
        offerLetters.getOfferLetter()
            .stream()
            .filter(letter -> nonNull(letter.getLetterId()))
            .filter(letter -> letter.getLetterId().equals(letterId))
            .findFirst()
            .ifPresent(letter -> {
                final OfferLetterParsedFlatData flatData = ofNullable(letter.getFlatData())
                    .orElseGet(OfferLetterParsedFlatData::new);

                flatData.setUnom(newFlat.getUnom());
                flatData.setFlatNumber(newFlat.getFlatNumber());

                letter.setFlatData(flatData);
            });
    }

    private Optional<PersonDocument> fetchPersonDocumentAndReportFlowErrorIfRequired(
        final SuperServiceDGPAdministrativeDocumentType superServiceDgpAdministrativeDocumentType,
        final String xmlMessage
    ) {
        final String personId = superServiceDgpAdministrativeDocumentType.getPersonId();
        final String affairId = superServiceDgpAdministrativeDocumentType.getAffairId();
        return personDocumentService.fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(
            personId, affairId, FlowType.FLOW_THIRTEEN, xmlMessage
        );
    }

    private void populateOfferLetter(
        final PersonType personType, final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        final String letterId = administrativeDocumentType.getLetterId();

        if (letterId == null || PersonUtils.getOfferLetterStream(personType)
            .anyMatch(offerLetter -> Objects.equals(offerLetter.getLetterId(), letterId))) {
            return;
        }

        if (personType.getOfferLetters() == null) {
            personType.setOfferLetters(new PersonType.OfferLetters());
        }

        final PersonType.OfferLetters.OfferLetter offerLetter = new PersonType.OfferLetters.OfferLetter();
        offerLetter.setLetterId(letterId);
        offerLetter.setDate(LocalDate.now());
        personType.getOfferLetters().getOfferLetter().add(offerLetter);
    }

    private void populateNewFlats(
        final PersonType personType, final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        final SuperServiceDGPAdministrativeDocumentType.NewFlats incomeNewFlats = administrativeDocumentType
            .getNewFlats();

        if (incomeNewFlats == null) {
            return;
        }

        if (personType.getNewFlatInfo() == null) {
            personType.setNewFlatInfo(new PersonType.NewFlatInfo());
        }

        incomeNewFlats
            .getNewFlat()
            .forEach(newFlat -> mapNewFlatDetail(
                personType.getNewFlatInfo().getNewFlat(), administrativeDocumentType.getLetterId(), newFlat
            ));
    }

    private void mapNewFlatDetail(final List<PersonType.NewFlatInfo.NewFlat> existingFlats,
                                  final String letterId,
                                  final NewFlat newFlatDetail) {
        newFlatDetail.setFlatNumber(StringUtils.trimToEmpty(newFlatDetail.getFlatNumber()));
        newFlatDetail.setUnom(StringUtils.trimToEmpty(newFlatDetail.getUnom()));
        StreamUtils.OptionalConsumer
            .of(getExistingFlat(newFlatDetail.getFlatNumber(),
                newFlatDetail.getUnom(),
                existingFlats))
            .ifPresent(flatType -> personMapper.toNewFlat(flatType, newFlatDetail, letterId, this::retrieveCcoAddress))
            .ifNotPresent(() -> existingFlats.add(
                personMapper.toNewFlat(newFlatDetail, letterId, this::retrieveCcoAddress)
            ));
    }

    private Optional<PersonType.NewFlatInfo.NewFlat> getExistingFlat(
        final String newFlatNumber,
        final String newFlatUnom,
        final List<PersonType.NewFlatInfo.NewFlat> existingFlats) {
        return existingFlats
            .stream()
            .filter(Objects::nonNull)
            .filter(newFlat -> newFlat.getCcoUnom() != null)
            .filter(flatType -> Objects.equals(trimToEmpty(flatType.getCcoFlatNum()), newFlatNumber)
                && Objects.equals(flatType.getCcoUnom().toString(), newFlatUnom)
            )
            .findFirst();
    }

    private String retrieveCcoAddress(final NewFlat newFlat) {
        if (nonNull(newFlat) && StringUtils.isNumeric(newFlat.getUnom())) {
            return ccoService.getCcoAddressByUnom(newFlat.getUnom());
        }
        return null;
    }

    private void updateOfferLetterFiles(
        final PersonDocument personDocument,
        final SuperServiceDGPAdministrativeDocumentType administrativeDocumentType
    ) {
        try {
            final PersonType personType = personDocument.getDocument().getPersonData();
            final String newAdministrativeLink = administrativeDocumentType.getAdministrativeDocumentLink();
            if (newAdministrativeLink == null || personType.getOfferLetters() == null) {
                return;
            }
            PersonUtils.getOfferLetterStream(personType)
                .filter(offerLetter -> Objects.equals(offerLetter.getLetterId(),
                    administrativeDocumentType.getLetterId()))
                .findFirst()
                .ifPresent(offerLetter -> addNewOfferLetterFile(
                    offerLetter,
                    personDocument,
                    newAdministrativeLink
                ));
        } catch (Exception ex) {
            log.error("Couldn't populate document link", ex);
        }
    }

    private void addNewOfferLetterFile(
        final PersonType.OfferLetters.OfferLetter existingOfferLetter,
        final PersonDocument personDocument,
        final String newAdministrativeLink
    ) {
        if (existingOfferLetter.getFiles() == null) {
            existingOfferLetter.setFiles(new PersonType.OfferLetters.OfferLetter.Files());
        }
        final List<PersonType.OfferLetters.OfferLetter.Files.File> existingFiles
            = existingOfferLetter.getFiles().getFile();
        final String chedFileId = newAdministrativeLink
            .replace("{", "").replace("}", "");
        existingFiles.add(createNewFile(chedFileId, personDocument.getFolderId()));
    }

    private PersonType.OfferLetters.OfferLetter.Files.File createNewFile(
        final String chedFileId, final String folderId
    ) {
        final PersonType.OfferLetters.OfferLetter.Files.File result
            = new PersonType.OfferLetters.OfferLetter.Files.File();

        final String fileLink = chedFileService.extractFileFromChedAndGetFileLink(chedFileId, folderId);
        result.setFileType(ADMINISTRATIVE_DOC_FILE_TYPE);
        result.setFileLink(fileLink);
        result.setChedFileId(chedFileId);
        result.setCreationDateTime(LocalDateTime.now());

        return result;
    }

    private void populateLegalRepresentatives(
        final PersonType personType,
        final SuperServiceDGPAdministrativeDocumentType superServiceDgpAdministrativeDocumentType
    ) {
        final List<String> representativesSnils = superServiceDgpAdministrativeDocumentType
            .getLegalRepresentatives().getSnils();
        if (personType.getLegalRepresentatives() == null) {
            personType.setLegalRepresentatives(new PersonType.LegalRepresentatives());
        }
        final List<String> existingSnils = personType.getLegalRepresentatives().getSnils();

        final Set<String> uniqueSnilsSet = mergeWithoutDuplicates(existingSnils, representativesSnils);

        existingSnils.clear();
        existingSnils.addAll(uniqueSnilsSet);
    }

    private static Set<String> mergeWithoutDuplicates(final List<String> existingSnils, final List<String> newSnils) {
        final Set<String> uniqueSet = new HashSet<>(existingSnils);
        uniqueSet.addAll(newSnils);

        return uniqueSet;
    }

    private void addLogMessageToPerson(
        final PersonDocument personDocument, final String eno, final String xmlMessage
    ) {
        final PersonType personType = personDocument.getDocument().getPersonData();
        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(eno)
            .addEventId("SuperServiceDGPAdministrativeDocument")
            .addFileId(xmlUtils.saveXmlToAlfresco(xmlMessage, personDocument.getFolderId()));
        if (personType.getIntegrationLog() == null) {
            personType.setIntegrationLog(new IntegrationLog());
        }
        personType.getIntegrationLog().getItem().add(logBuilder.build());
    }
}
