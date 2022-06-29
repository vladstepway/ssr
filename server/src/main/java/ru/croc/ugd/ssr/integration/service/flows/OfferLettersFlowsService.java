package ru.croc.ugd.ssr.integration.service.flows;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.trimToEmpty;
import static ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter.Files.File;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.IntegrationLog;
import ru.croc.ugd.ssr.OfferLetterParsedFlatData;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.builder.IntegrationLogBuilder;
import ru.croc.ugd.ssr.builder.ResettlementHistoryBuilder;
import ru.croc.ugd.ssr.dto.CcoFlat;
import ru.croc.ugd.ssr.dto.OfferLetterDocData;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequest;
import ru.croc.ugd.ssr.egrn.EgrnFlatRequestData;
import ru.croc.ugd.ssr.egrn.FlatRequestCriteria;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.util.XmlUtils;
import ru.croc.ugd.ssr.mapper.OfferLetterMapper;
import ru.croc.ugd.ssr.mapper.OfferLetterParsingMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPLetterRequestType;
import ru.croc.ugd.ssr.model.integration.dto.FlowReceivedMessageDto;
import ru.croc.ugd.ssr.model.offerletterparsing.OfferLetterParsingDocument;
import ru.croc.ugd.ssr.offerletterparsing.OfferLetterParsingType;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.SystemAddressesService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.service.document.OfferLetterParsingDocumentService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService;
import ru.croc.ugd.ssr.service.flowerrorreport.FlowType;
import ru.croc.ugd.ssr.service.pdf.DefaultPdfHandlerService;
import ru.croc.ugd.ssr.utils.Comparators;
import ru.reinform.cdp.security.utils.RiAuthenticationUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Обновление данных о письмах с предложниями. 2 поток.
 */
@Service
@Slf4j
public class OfferLettersFlowsService {
    private static final Logger LOG = LoggerFactory.getLogger(OfferLettersFlowsService.class);

    private static final String PDF_BLANK = "2";
    private static final String PDF_PROPOSAL = "1";
    private static final String PROCESS_PARSE_ADDRESS_KEY = "ugdssrOfferLetter_parseAddress";
    private static final String SUCCESS_STATUS_CODE = "1004";

    @Value("${ugd.ssr.offer-letter-parsing.enabled:false}")
    private boolean offerLetterParsingEnabled;

    @Value("${ugd.ssr.offer-letter-parsing.find-unom-by-address.enabled:false}")
    private boolean offerLetterParsingFindUnomByAddressEnabled;

    @Value("${ugd.ssr.offer-letter-parsing.task-creation.enabled:false}")
    private boolean offerLetterParsingTaskCreationEnabled;

    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;
    private final FlatResettlementStatusUpdateService flatResettlementStatusUpdateService;
    private final RiAuthenticationUtils riAuthenticationUtils;
    private final ChedFileService chedFileService;
    private final BeanFactory beanFactory;
    private final XmlUtils xmlUtils;
    private final FlowErrorReportService flowErrorReportService;
    private final SsrFilestoreService ssrFilestoreService;
    private final DefaultPdfHandlerService pdfHandlerService;
    private final OfferLetterMapper offerLetterMapper;
    private final CapitalConstructionObjectService ccoService;
    private final SystemAddressesService addressService;
    private final BpmService bpmService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final OfferLetterParsingDocumentService offerLetterParsingDocumentService;
    private final OfferLetterParsingMapper offerLetterParsingMapper;
    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;

    public OfferLettersFlowsService(
        ElkUserNotificationService elkUserNotificationService,
        PersonDocumentService personDocumentService,
        FlatResettlementStatusUpdateService flatResettlementStatusUpdateService,
        RiAuthenticationUtils riAuthenticationUtils,
        ChedFileService chedFileService,
        BeanFactory beanFactory,
        XmlUtils xmlUtils,
        FlowErrorReportService flowErrorReportService,
        SsrFilestoreService ssrFilestoreService,
        @Qualifier("defaultPdfHandlerService") DefaultPdfHandlerService pdfHandlerService,
        OfferLetterMapper offerLetterMapper,
        CapitalConstructionObjectService ccoService,
        SystemAddressesService addressService,
        BpmService bpmService,
        RealEstateDocumentService realEstateDocumentService,
        OfferLetterParsingDocumentService offerLetterParsingDocumentService,
        OfferLetterParsingMapper offerLetterParsingMapper,
        EgrnFlatRequestDocumentService egrnFlatRequestDocumentService
    ) {
        this.elkUserNotificationService = elkUserNotificationService;
        this.personDocumentService = personDocumentService;
        this.flatResettlementStatusUpdateService = flatResettlementStatusUpdateService;
        this.riAuthenticationUtils = riAuthenticationUtils;
        this.chedFileService = chedFileService;
        this.beanFactory = beanFactory;
        this.xmlUtils = xmlUtils;
        this.flowErrorReportService = flowErrorReportService;
        this.ssrFilestoreService = ssrFilestoreService;
        this.pdfHandlerService = pdfHandlerService;
        this.offerLetterMapper = offerLetterMapper;
        this.ccoService = ccoService;
        this.addressService = addressService;
        this.bpmService = bpmService;
        this.realEstateDocumentService = realEstateDocumentService;
        this.offerLetterParsingDocumentService = offerLetterParsingDocumentService;
        this.offerLetterParsingMapper = offerLetterParsingMapper;
        this.egrnFlatRequestDocumentService = egrnFlatRequestDocumentService;
    }

    /**
     * Получение письма из ДГИ.
     *
     * @param flowReceivedMessageDto etpmv message
     * @param xml                 xml
     */
    public void receiveOfferLetterRequest(
        final FlowReceivedMessageDto<SuperServiceDGPLetterRequestType> flowReceivedMessageDto,
        final String xml
    ) {
        try {
            processOfferLetterRequest(flowReceivedMessageDto, xml);
        } catch (Exception e) {
            log.error(
                "Unable to process offer letters request (eno = {}): {}",
                flowReceivedMessageDto.getEno(),
                e.getMessage(),
                e
            );
        }
    }

    private void processOfferLetterRequest(
        final FlowReceivedMessageDto<SuperServiceDGPLetterRequestType> flowReceivedMessageDto,
        final String xml
    ) {
        riAuthenticationUtils.setSecurityContextByServiceuser();

        final String affairId = flowReceivedMessageDto.getParsedMessage().getAffairId();
        final String personId = flowReceivedMessageDto.getParsedMessage().getPersonId();
        Optional<PersonDocument> documentOpt = personDocumentService
            .fetchOneByPersonIdAndAffairIdAndReportFlowErrorIfRequired(personId, affairId, FlowType.FLOW_TWO, xml);
        if (!documentOpt.isPresent()) {
            return;
        }
        final PersonDocument document = documentOpt.get();

        final Pair<Boolean, Boolean> isValidCommunalLiverIsFlatCommunal = personDocumentService
            .isValidCommunalFlatLiver(document);
        if (!isValidCommunalLiverIsFlatCommunal.getLeft()) {
            flowErrorReportService.reportPersonNotValidCommunalLiverError(
                FlowType.FLOW_TWO,
                document,
                isValidCommunalLiverIsFlatCommunal.getRight(),
                xml
            );
        }

        PersonType personData = document.getDocument().getPersonData();
        if (personData.getOfferLetters() == null) {
            personData.setOfferLetters(new PersonType.OfferLetters());
        }
        List<OfferLetter> offerLetters = personData.getOfferLetters().getOfferLetter();
        Optional<OfferLetter> optionalOfferLetter = offerLetters.stream()
            .filter(letter -> letter.getLetterId() != null
                && flowReceivedMessageDto.getParsedMessage().getLetterId() != null
                && letter.getLetterId().equals(flowReceivedMessageDto.getParsedMessage().getLetterId()))
            .findFirst();

        final String pdfProposal = flowReceivedMessageDto.getParsedMessage().getPdfProposal()
            .replace("{", "").replace("}", "");
        final String pdfBlank = flowReceivedMessageDto.getParsedMessage().getPdfBlank()
            .replace("{", "").replace("}", "");

        if (optionalOfferLetter.isPresent()) {
            OfferLetter offerLetter = optionalOfferLetter.get();
            offerLetter.setDate(LocalDate.now());
            offerLetter.setIdCIP(flowReceivedMessageDto.getParsedMessage().getIdCIP());

            if (isNull(offerLetter.getFiles())) {
                offerLetter.setFiles(new OfferLetter.Files());
            }
            final List<File> fileList = offerLetter.getFiles().getFile();

            addOrUpdateFilesWithFileLink(fileList, pdfProposal, PDF_PROPOSAL, document.getFolderId());
            addOrUpdateFilesWithFileLink(fileList, pdfBlank, PDF_BLANK, document.getFolderId());

            if (offerLetterParsingEnabled) {
                populateOfferLetterPdfFlatData(offerLetter, personData);
            }
            LOG.info("Письмо обновлено personID:{}, affairId:{}, letterId:{}",
                personId,
                affairId,
                offerLetter.getLetterId());

        } else {
            OfferLetter offerLetter = new OfferLetter();
            offerLetter.setDate(LocalDate.now());
            offerLetter.setIdCIP(flowReceivedMessageDto.getParsedMessage().getIdCIP());
            offerLetter.setLetterId(flowReceivedMessageDto.getParsedMessage().getLetterId());
            offerLetter.setFiles(new OfferLetter.Files());

            if (StringUtils.isNotEmpty(pdfBlank)) {
                offerLetter.getFiles()
                    .getFile()
                    .add(createFile(chedFileService.extractFileFromChedAndGetFileLink(pdfBlank,
                        document.getFolderId()), PDF_BLANK, pdfBlank));
            }
            if (StringUtils.isNotEmpty(pdfProposal)) {
                offerLetter.getFiles()
                    .getFile()
                    .add(createFile(chedFileService.extractFileFromChedAndGetFileLink(pdfProposal,
                        document.getFolderId()), PDF_PROPOSAL, pdfProposal));
            }
            if (offerLetterParsingEnabled) {
                populateOfferLetterPdfFlatData(offerLetter, personData);
            }
            personData.getOfferLetters().getOfferLetter().add(offerLetter);

            LOG.info("Добавлено новое письмо personID:{}, affairId:{}", personId, affairId);

        }

        final ResettlementHistoryBuilder historyBuilder = beanFactory.getBean(ResettlementHistoryBuilder.class);
        historyBuilder
            .addEventId("2")
            .addDataId(flowReceivedMessageDto.getParsedMessage().getLetterId())
            .addAnnotation("Получено письмо с предложением letterId: "
                + flowReceivedMessageDto.getParsedMessage().getLetterId());
        personData.getResettlementHistory().add(historyBuilder.build());

        final IntegrationLogBuilder logBuilder = beanFactory.getBean(IntegrationLogBuilder.class);
        logBuilder
            .addEno(flowReceivedMessageDto.getEno())
            .addEventId("SuperServiceDGPLetterRequest")
            .addFileId(xmlUtils.saveXmlToAlfresco(xml, document.getFolderId()));
        if (personData.getIntegrationLog() == null) {
            personData.setIntegrationLog(new IntegrationLog());
        }
        personData.getIntegrationLog().getItem().add(logBuilder.build());

        personData.setRelocationStatus("2");
        personData.getOfferLetters().getOfferLetter().sort(Comparator
            .comparing(PersonType.OfferLetters.OfferLetter::getLetterId,
                Comparators.createNaturalOrderRegexComparator()));

        personDocumentService.updateDocument(document.getId(), document, true, true, "");

        flatResettlementStatusUpdateService.updateFlatResettlementStatus(personData, "2");

        // Отправим уведомление жителю в ЕЛК
        if (flowReceivedMessageDto.isShouldSendNotifications()) {
            try {
                elkUserNotificationService.sendNotificationOfferLetter(
                    document,
                    true,
                    flowReceivedMessageDto.getParsedMessage().getIdCIP(),
                    flowReceivedMessageDto.getParsedMessage().getLetterId(),
                    getFileId(
                        flowReceivedMessageDto.getParsedMessage().getPdfBlank(),
                        flowReceivedMessageDto.getParsedMessage().getPdfProposal()
                    )
                );
            } catch (Exception e) {
                log.debug(
                    "Couldn't run sendNotificationOfferLetter (eno = {}): {}",
                    flowReceivedMessageDto.getEno(),
                    e.getMessage(),
                    e
                );
            }
        } else {
            log.debug(
                "Skip sendOfferLetterNotification (eno = {}): message received as part of affairCollation",
                flowReceivedMessageDto.getEno()
            );
        }
    }

    private void addOrUpdateFilesWithFileLink(
        final List<File> fileList, final String pdfChedFileId, final String fileType, final String folderId
    ) {
        if (StringUtils.isNotBlank(pdfChedFileId)) {
            final String pdfFileStoreId = chedFileService.extractFileFromChedAndGetFileLink(pdfChedFileId, folderId);

            final List<File> existingPdfFileListByType = fileList.stream()
                .filter(file -> nonNull(file.getFileType()))
                .filter(file -> Objects.equals(fileType, file.getFileType()))
                .collect(Collectors.toList());

            if (!existingPdfFileListByType.isEmpty()) {
                existingPdfFileListByType.forEach(file -> file.setFileLink(pdfFileStoreId));
            } else {
                final File newPdfFile = new File();
                newPdfFile.setFileType(fileType);
                newPdfFile.setFileLink(pdfFileStoreId);
                newPdfFile.setChedFileId(pdfChedFileId);

                fileList.add(newPdfFile);
            }
        }
    }

    @Async
    public void extractFlatDataFromOfferLetterForAll() {
        log.info("extractFlatDataFromOfferLetterForAll: Started offer letter flat data extraction.");
        final List<String> personIdsToProcess = personDocumentService
            .fetchPersonIdsForOfferLetterDataExtraction();
        log.info("extractFlatDataFromOfferLetterForAll: total persons to process: " + personIdsToProcess.size());
        final AtomicInteger currentAmountOfProcessedAtom = new AtomicInteger(0);
        personIdsToProcess
            .forEach(personId -> {
                int currentAmountOfProcessed = currentAmountOfProcessedAtom.getAndIncrement();
                populatePersonDocumentWithOfferFlatData(personId);
                if (currentAmountOfProcessed % 10 == 0) {
                    log.info("extractFlatDataFromOfferLetterForAll: persons processed: "
                        + currentAmountOfProcessed);
                }
            });
        log.info("extractFlatDataFromOfferLetterForAll: Finished offer letter flat data extraction.");
    }

    public void populateOfferLetterPdfFlatData(final OfferLetter offerLetter, final PersonType personData) {
        if (offerLetter.getFiles() == null) {
            return;
        }
        try {
            offerLetter.setFlatData(retrieveOfferLetterParsedFlatData(offerLetter));
            addNewFlatOrCreateTask(offerLetter, personData);
        } catch (Exception e) {
            log.warn("Unable to populate offer letter flat data: {}", e.getMessage(), e);
        }
    }

    private void addNewFlatOrCreateTask(final OfferLetter offerLetter, final PersonType personData) {
        final OfferLetterParsedFlatData flatData = offerLetter.getFlatData();
        if (flatData == null) {
            return;
        }
        final String cadNumber = flatData.getCadNumber();
        final String address = flatData.getAddress();
        final String flatNumber = flatData.getFlatNumber();

        log.info(
            "addNewFlatOrCreateTask: cadNumber = {}, address = {}, flatNumber = {}", cadNumber, address, flatNumber
        );
        final Optional<EgrnFlatRequest> flatRequestOptional = egrnFlatRequestDocumentService
            .fetchByCadNumAndStatusAndCcoDocumentNotNull(cadNumber, SUCCESS_STATUS_CODE)
            .map(EgrnFlatRequestDocument::getDocument);

        final FlatRequestCriteria flatRequestCriteria = flatRequestOptional
            .map(EgrnFlatRequest::getEgrnFlatRequestData)
            .map(EgrnFlatRequestData::getRequestCriteria)
            .orElse(null);

        if (flatRequestCriteria != null) {
            fillParsedLetterOrCreateTask(
                offerLetter,
                personData,
                flatRequestCriteria.getCadastralNumber(),
                flatRequestCriteria.getFlatNumber(),
                ccoService.getCcoAddressByUnom(flatRequestCriteria.getUnom()),
                flatRequestCriteria.getUnom()
            );
        } else {
            final CcoFlat ccoFlat = ofNullable(cadNumber)
                .map(ccoService::getCcoFlatByFlatCadNumber)
                .orElseGet(() -> {
                    if (offerLetterParsingFindUnomByAddressEnabled) {
                        final BigInteger ccoUnom = ofNullable(address)
                            .map(addressService::getSysUnomByAddress)
                            .orElse(null);
                        final String ccoAddress = ofNullable(ccoUnom)
                            .map(BigInteger::toString)
                            .map(ccoService::getCcoAddressByUnom)
                            .orElse("-");
                        return CcoFlat.builder()
                            .unom(ccoUnom)
                            .address(ccoAddress)
                            .flatNumber(flatNumber)
                            .build();
                    } else {
                        return CcoFlat.builder().build();
                    }
                });

            log.info("addNewFlatOrCreateTask: ccoFlat = {}", ccoFlat);

            fillParsedLetterOrCreateTask(
                offerLetter,
                personData,
                cadNumber,
                ccoFlat.getFlatNumber(),
                ccoFlat.getAddress(),
                nonNull(ccoFlat.getUnom()) ? ccoFlat.getUnom().toString() : null
            );
        }
    }

    private void fillParsedLetterOrCreateTask(
        final OfferLetter offerLetter,
        final PersonType personData,
        final String cadNumber,
        final String flatNumber,
        final String address,
        final String unom
    ) {
        final OfferLetterParsedFlatData flatData = offerLetter.getFlatData();
        final String flatNumberFromLetter = flatData.getFlatNumber();

        if (unom != null && flatNumber != null
            && (isNull(flatNumberFromLetter) || flatNumber.equals(flatNumberFromLetter))) {
            addNewFlatAndUpdateFlatData(offerLetter, personData, cadNumber, flatNumber, address, unom);
        } else {
            ofNullable(unom).ifPresent(flatData::setUnom);
            ofNullable(flatNumber).ifPresent(flatData::setFlatNumber);

            if (offerLetterParsingTaskCreationEnabled) {
                createTaskForOfferLetterIfNeeded(offerLetter.getLetterId(), personData);
            }
        }
    }

    private void addNewFlatAndUpdateFlatData(
        final OfferLetter offerLetter,
        final PersonType personData,
        final String cadNumber,
        final String flatNumber,
        final String address,
        final String unom
    ) {
        offerLetter.getFlatData().setUnom(unom);
        offerLetter.getFlatData().setFlatNumber(flatNumber);

        if (personData.getNewFlatInfo() == null) {
            personData.setNewFlatInfo(new PersonType.NewFlatInfo());
        }
        final BigInteger integerUnom = new BigInteger(unom);
        final List<PersonType.NewFlatInfo.NewFlat> newFlatList = personData.getNewFlatInfo().getNewFlat();
        final Optional<PersonType.NewFlatInfo.NewFlat> newFlatOptional = getNewFlatIfExists(
            newFlatList, integerUnom, flatNumber
        );
        final String letterId = offerLetter.getLetterId();
        if (newFlatOptional.isPresent()) {
            final PersonType.NewFlatInfo.NewFlat newFlat = newFlatOptional.get();
            newFlat.setLetterId(letterId);
        } else {
            final PersonType.NewFlatInfo.NewFlat newFlat = new PersonType.NewFlatInfo.NewFlat();
            newFlat.setCcoCadNum(cadNumber);
            newFlat.setCcoUnom(integerUnom);
            newFlat.setCcoAddress(address);
            newFlat.setCcoFlatNum(flatNumber);
            newFlat.setLetterId(letterId);

            newFlatList.add(newFlat);
        }
    }

    private void createTaskForOfferLetterIfNeeded(final String letterId, final PersonType personData) {
        final String affairId = ofNullable(personData)
            .map(PersonType::getAffairId)
            .orElse(null);

        if (!offerLetterParsingDocumentService.existsByLetterIdAndAffairId(letterId, affairId)) {
            final OfferLetterParsingDocument offerLetterParsingDocument = offerLetterParsingMapper
                .toOfferLetterParsingDocument(letterId, personData, this::retrieveRealEstate);

            offerLetterParsingDocumentService.createDocument(offerLetterParsingDocument, true, null);

            final OfferLetterParsingType offerLetterParsing = offerLetterParsingDocument.getDocument()
                .getOfferLetterParsingData();

            createParseAddressProcess(offerLetterParsingDocument.getId())
                .ifPresent(offerLetterParsing::setProcessInstanceId);

            offerLetterParsingDocumentService.updateDocument(
                offerLetterParsingDocument.getId(), offerLetterParsingDocument, true, true, null
            );
        }
    }

    private RealEstateDataType retrieveRealEstate(final PersonType personData) {
        return ofNullable(personData.getUNOM())
            .map(BigInteger::toString)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .orElse(null);
    }

    private Optional<String> createParseAddressProcess(final String offerLetterParsingId) {
        final Map<String, String> variablesMap = Collections.singletonMap(
            BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, offerLetterParsingId
        );

        return ofNullable(bpmService.startNewProcess(PROCESS_PARSE_ADDRESS_KEY, variablesMap));
    }

    private static Optional<PersonType.NewFlatInfo.NewFlat> getNewFlatIfExists(
        final List<PersonType.NewFlatInfo.NewFlat> newFlats, final BigInteger ccoUnom, final String ccoFlatNum
    ) {
        return newFlats.stream()
            .filter(newFlat -> Objects.equals(ccoUnom, newFlat.getCcoUnom())
                && Objects.equals(trimToEmpty(ccoFlatNum), trimToEmpty(newFlat.getCcoFlatNum())))
            .findFirst();
    }

    private static String getFlatNumber(final String address) {
        return ofNullable(address)
            .map(String::toLowerCase)
            .map(a -> a.replaceFirst("((.+ кв\\.)|(.+ квартира ))", ""))
            .map(a -> a.replaceAll("[^а-яё0-9 ]", " "))
            .map(String::trim)
            .orElse(null);
    }

    private OfferLetterParsedFlatData retrieveOfferLetterParsedFlatData(final OfferLetter offerLetter) {
        return offerLetter
            .getFiles()
            .getFile()
            .stream()
            .filter(Objects::nonNull)
            .filter(file -> PDF_PROPOSAL.equals(file.getFileType()))
            .findFirst()
            .map(File::getFileLink)
            .map(this::extractOfferLetterDocInfo)
            .map(offerLetterMapper::toOfferLetterParsedFlatData)
            .orElse(null);
    }

    public OfferLetterDocData extractOfferLetterDocInfo(final String fileStoreId) {
        final byte[] fileContent = ssrFilestoreService.getFile(fileStoreId);
        final String pdfContent = pdfHandlerService.extractPdfTextContent(fileContent);
        return OfferLetterDocData
            .builder()
            .address(OfferLetterExtractorHelper.extractAddressWithoutFlat(pdfContent))
            .flatNumber(OfferLetterExtractorHelper.extractFlatNumber(pdfContent))
            .cadNumber(OfferLetterExtractorHelper.extractCadNumber(pdfContent))
            .floor(OfferLetterExtractorHelper.extractFloor(pdfContent))
            .roomsAmount(OfferLetterExtractorHelper.extractRoomsAmount(pdfContent))
            .fullSquareMeters(OfferLetterExtractorHelper.extractFullSquareMeters(pdfContent))
            .livingSquareMeters(OfferLetterExtractorHelper.extractLivingSquareMeters(pdfContent))
            .fullSquareNoTerraceMeters(OfferLetterExtractorHelper.extractFullSquareNoTerraceMeters(pdfContent))
            .build();
    }

    private void populatePersonDocumentWithOfferFlatData(final String personId) {
        final Optional<PersonDocument> personDocumentToUpdate = personDocumentService.fetchById(personId);
        personDocumentToUpdate.ifPresent(this::populatePersonDocumentWithOfferFlatData);
    }

    private void populatePersonDocumentWithOfferFlatData(final PersonDocument personDocument) {
        final PersonType personData = personDocument.getDocument().getPersonData();
        Optional.ofNullable(personData.getOfferLetters())
            .map(PersonType.OfferLetters::getOfferLetter)
            .orElse(Collections.emptyList())
            .stream()
            .filter(offerLetter -> isNull(offerLetter.getFlatData()))
            .forEach(offerLetter -> populateOfferLetterPdfFlatData(offerLetter, personData));

        personDocumentService.updateDocument(personDocument);
    }

    private File createFile(String link, String pdfType, String chedFileId) {
        File result = new File();
        result.setFileType(pdfType);
        result.setFileLink(link);
        result.setChedFileId(chedFileId);
        return result;
    }

    private String getFileId(String pdfBlanc, String pdfProposal) {
        if (StringUtils.isNotEmpty(pdfProposal)) {
            return pdfProposal.replace("{", "").replace("}", "");
        } else if (StringUtils.isNotEmpty(pdfBlanc)) {
            return pdfBlanc.replace("{", "").replace("}", "");
        } else {
            return "";
        }
    }

}
