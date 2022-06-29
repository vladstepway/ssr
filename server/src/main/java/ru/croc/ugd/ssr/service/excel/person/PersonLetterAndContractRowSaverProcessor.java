package ru.croc.ugd.ssr.service.excel.person;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.croc.ugd.ssr.integration.service.flows.AdministrativeDocumentFlowService.ADMINISTRATIVE_DOC_FILE_TYPE;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeErrorLog;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeInfoLog;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeWarnLog;
import static ru.croc.ugd.ssr.utils.DateTimeUtils.getDateFromString;
import static ru.croc.ugd.ssr.utils.PersonUtils.OFFER_FILE_TYPE;
import static ru.croc.ugd.ssr.utils.PersonUtils.PDF_CONTRACT_FILE_TYPE;
import static ru.croc.ugd.ssr.utils.PersonUtils.RTF_CONTRACT_TO_SIGN_FILE_TYPE;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Contracts;
import ru.croc.ugd.ssr.PersonType.Contracts.Contract;
import ru.croc.ugd.ssr.PersonType.OfferLetters;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractDto;
import ru.croc.ugd.ssr.service.excel.person.model.PersonLetterAndContractSheetConstants;
import ru.croc.ugd.ssr.utils.Comparators;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PersonLetterAndContractRowSaverProcessor extends PersonLetterAndContractSheetRowProcessor {

    private final PersonDocumentService personDocumentService;
    private final ChedFileService chedFileService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;

    public PersonLetterAndContractRowSaverProcessor(
        final PersonLetterAndContractSheetCellProcessor personLetterAndContractSheetCellProcessor,
        final PersonDocumentService personDocumentService,
        final ChedFileService chedFileService,
        final ResettlementRequestDocumentService resettlementRequestDocumentService
    ) {
        super(personLetterAndContractSheetCellProcessor);
        this.personDocumentService = personDocumentService;
        this.chedFileService = chedFileService;
        this.resettlementRequestDocumentService = resettlementRequestDocumentService;
    }

    @Override
    protected void saveRowAsOfferLetterOrContract(
        final XSSFRow rowToProcess, final XssfRowProcessResult xssfRowProcessResult, final FileOutputStream logFos
    ) {
        try {
            saveOfferLetterOrContract(xssfRowProcessResult, rowToProcess.getRowNum(), logFos);
        } catch (Exception e) {
            writeErrorLog(
                logFos,
                String.format("Ошибка при обновлении данных, номер строки = %d", rowToProcess.getRowNum()),
                String.format(
                    "PersonLetterAndContract. Failed to save offer letter data, rowNum = %d: ", rowToProcess.getRowNum()
                ),
                e
            );
        }
    }

    private void saveOfferLetterOrContract(
        final XssfRowProcessResult xssfRowProcessResult, final int rowNumber, final FileOutputStream logFos
    ) {
        final PersonLetterAndContractDto personLetterAndContractDto = new PersonLetterAndContractDto();
        final List<XssfCellProcessResult> xssfCellProcessResults = xssfRowProcessResult
            .getXssfCellProcessResults();
        if (!CollectionUtils.isEmpty(xssfCellProcessResults)) {
            xssfCellProcessResults.forEach(xssfCellProcessResult -> mapProcessRowResult(
                xssfCellProcessResult, personLetterAndContractDto
            ));
        }
        writeInfoLog(
            logFos,
            String.format("Выполнен разбор строки, номер строки = %d: %s", rowNumber, personLetterAndContractDto),
            String.format(
                "PersonLetterAndContract. Prepared data for saving, rowNum = %d: %s",
                rowNumber,
                personLetterAndContractDto
            )
        );

        saveOfferLetterOrContract(personLetterAndContractDto, logFos);
    }

    private void saveOfferLetterOrContract(
        final PersonLetterAndContractDto personLetterAndContractDto, final FileOutputStream logFos
    ) {
        final List<PersonDocument> personDocuments = personDocumentService.fetchByAffairId(
            personLetterAndContractDto.getAffairId()
        );
        if (personDocuments.isEmpty()) {
            writeWarnLog(
                logFos,
                "Не найдены жители, affairId = " + personLetterAndContractDto.getAffairId(),
                "PersonLetterAndContract. Unable to find person, affairId = " + personLetterAndContractDto.getAffairId()

            );
        } else {
            personDocuments.forEach(personDocument -> saveOfferLetterOrContract(
                personDocument, personLetterAndContractDto, logFos
            ));
        }
    }

    private void saveOfferLetterOrContract(
        final PersonDocument personDocument,
        final PersonLetterAndContractDto personLetterAndContractDto,
        final FileOutputStream logFos
    ) {
        if (nonNull(personLetterAndContractDto.getLetterId())) {
            createOrUpdateOfferLetter(personDocument, personLetterAndContractDto, logFos);
            writeInfoLog(
                logFos,
                String.format(
                    "Актуализированы данные о письме, personDocumentId = %s, letterId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getLetterId()
                ),
                String.format(
                    "PersonLetterAndContract. Actualized letter data, personDocumentId = %s, letterId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getLetterId()
                )
            );
        }

        if (nonNull(personLetterAndContractDto.getOrderId())) {
            createOrUpdateContract(personDocument, personLetterAndContractDto, logFos);
            writeInfoLog(
                logFos,
                String.format(
                    "Актуализированы данные о договоре, personDocumentId = %s, orderId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getOrderId()
                ),
                String.format(
                    "PersonLetterAndContract. Actualized contract data, personDocumentId = %s, orderId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getOrderId()
                )
            );
        }
    }

    private void mapProcessRowResult(
        final XssfCellProcessResult xssfCellProcessResult, PersonLetterAndContractDto personLetterAndContractDto
    ) {
        final PersonLetterAndContractSheetConstants personLetterAndContractSheetConstants =
            PersonLetterAndContractSheetConstants.findByIndex(xssfCellProcessResult.getCollIndex());
        if (personLetterAndContractSheetConstants == null) {
            return;
        }
        final String cellValue = StringUtils.trimToNull(xssfCellProcessResult.getCellRawValue());
        switch (personLetterAndContractSheetConstants) {
            case AFFAIR_ID_COLUMN_INDEX:
                personLetterAndContractDto.setAffairId(cellValue);
                break;
            case LETTER_ID_COLUMN_INDEX:
                personLetterAndContractDto.setLetterId(cellValue);
                break;
            case LETTER_DATE_COLUMN_INDEX:
                personLetterAndContractDto.setLetterDate(getDateFromString(cellValue));
                break;
            case LETTER_CHED_FILE_ID_COLUMN_INDEX:
                personLetterAndContractDto.setLetterChedFileId(retrieveChedFileId(cellValue));
                break;
            case ADMINISTRATIVE_DOCUMENT_DATE_COLUMN_INDEX:
                personLetterAndContractDto.setAdministrativeDocumentDate(getDateFromString(cellValue));
                break;
            case ADMINISTRATIVE_DOCUMENT_CHED_FILE_ID_COLUMN_INDEX:
                personLetterAndContractDto.setAdministrativeDocumentChedFileId(retrieveChedFileId(cellValue));
                break;
            case ORDER_ID_COLUMN_INDEX:
                personLetterAndContractDto.setOrderId(cellValue);
                break;
            case RTF_CONTRACT_TO_SIGN_CHED_FILE_ID_COLUMN_INDEX:
                personLetterAndContractDto.setRtfContractToSignChedFileId(retrieveChedFileId(cellValue));
                break;
            case PDF_CONTRACT_CHED_FILE_ID_COLUMN_INDEX:
                personLetterAndContractDto.setPdfContractChedFileId(retrieveChedFileId(cellValue));
                break;
            default:
                break;
        }
    }

    private String retrieveChedFileId(final String cellValue) {
        if (nonNull(cellValue)) {
            return cellValue.replace("{", "").replace("}", "");
        } else {
            return null;
        }
    }

    private void createOrUpdateOfferLetter(
        final PersonDocument personDocument,
        final PersonLetterAndContractDto personLetterAndContractDto,
        final FileOutputStream logFos
    ) {
        final String folderId = personDocument.getFolderId();
        final PersonType personData = personDocument.getDocument().getPersonData();
        if (isNull(personData.getOfferLetters())) {
            personData.setOfferLetters(new OfferLetters());
        }
        final Optional<OfferLetter> optionalOfferLetter = PersonUtils.getOfferLetter(
            personData, personLetterAndContractDto.getLetterId()
        );
        if (optionalOfferLetter.isPresent()) {
            boolean hasChanges = false;
            final OfferLetter offerLetter = optionalOfferLetter.get();
            if (isNull(offerLetter.getDate()) && nonNull(personLetterAndContractDto.getLetterDate())) {
                offerLetter.setDate(personLetterAndContractDto.getLetterDate());
                hasChanges = true;
            }
            if (isNull(offerLetter.getIdCIP())) {
                resettlementRequestDocumentService.getCipIdForPerson(personData)
                    .ifPresent(offerLetter::setIdCIP);
                if (nonNull(offerLetter.getIdCIP())) {
                    hasChanges = true;
                }
            }
            if (isNull(offerLetter.getFiles())
                && (StringUtils.isNotEmpty(personLetterAndContractDto.getLetterChedFileId())
                || StringUtils.isNotEmpty(personLetterAndContractDto.getAdministrativeDocumentChedFileId()))) {
                offerLetter.setFiles(new OfferLetter.Files());
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getLetterChedFileId())
                && !PersonUtils.getOfferLetterFile(offerLetter).isPresent()) {
                addLetterFile(offerLetter, personLetterAndContractDto.getLetterChedFileId(), folderId);
                hasChanges = true;
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getAdministrativeDocumentChedFileId())
                && !PersonUtils.getAdministrativeDocument(offerLetter).isPresent()) {
                addAdministrativeDocumentFile(offerLetter, personLetterAndContractDto, folderId);
                hasChanges = true;
            }
            if (hasChanges) {
                offerLetter.setIsDgiData(true);
                writeInfoLog(
                    logFos,
                    String.format(
                        "Изменены данные о письме, personDocumentId = %s, letterId = %s",
                        personDocument.getId(),
                        personLetterAndContractDto.getLetterId()
                    ),
                    String.format(
                        "PersonLetterAndContract. Updated letter data, personDocumentId = %s, letterId = %s",
                        personDocument.getId(),
                        personLetterAndContractDto.getLetterId()
                    )
                );
            }
        } else {
            final OfferLetter offerLetter = new OfferLetter();
            offerLetter.setLetterId(personLetterAndContractDto.getLetterId());
            offerLetter.setDate(personLetterAndContractDto.getLetterDate());
            resettlementRequestDocumentService.getCipIdForPerson(personData)
                .ifPresent(offerLetter::setIdCIP);
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getLetterChedFileId())
                || StringUtils.isNotEmpty(personLetterAndContractDto.getAdministrativeDocumentChedFileId())) {
                offerLetter.setFiles(new OfferLetter.Files());
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getLetterChedFileId())) {
                addLetterFile(offerLetter, personLetterAndContractDto.getLetterChedFileId(), folderId);
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getAdministrativeDocumentChedFileId())) {
                addAdministrativeDocumentFile(offerLetter, personLetterAndContractDto, folderId);
            }
            offerLetter.setIsDgiData(true);
            personData.getOfferLetters().getOfferLetter().add(offerLetter);
            writeInfoLog(
                logFos,
                String.format(
                    "Добавлено новое письмо, personDocumentId = %s, letterId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getLetterId()
                ),
                String.format(
                    "PersonLetterAndContract. Added a new letter, personDocumentId = %s, letterId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getLetterId()
                )
            );
            personData.getOfferLetters().getOfferLetter()
                .sort(Comparator.comparing(
                    PersonType.OfferLetters.OfferLetter::getLetterId, Comparators.createNaturalOrderRegexComparator()
                ));
        }
        actualizeRelocationStatus(personData, personDocument.getId(), 2, logFos);
        personDocumentService.updateDocument(personDocument, "createOrUpdateOfferLetter");
    }

    private void actualizeRelocationStatus(
        final PersonType personData,
        final String personDocumentId,
        final Integer newRelocationStatus,
        final FileOutputStream logFos
    ) {
        final Integer currentRelocationStatus = retrieveCurrentStatus(personData.getRelocationStatus());
        if ((isNull(currentRelocationStatus) || currentRelocationStatus < newRelocationStatus)) {
            personData.setRelocationStatus(newRelocationStatus.toString());
            writeInfoLog(
                logFos,
                String.format(
                    "Актуализирован статус переезда, personDocumentId = %s, oldValue = %s, newValue = %s",
                    personDocumentId,
                    currentRelocationStatus,
                    newRelocationStatus
                ),
                String.format(
                    "PersonLetterAndContract. Actualized relocation status, personDocumentId = %s, "
                        + "oldValue = %s, newValue = %s",
                    personDocumentId,
                    currentRelocationStatus,
                    newRelocationStatus
                )
            );
        }
    }

    private Integer retrieveCurrentStatus(final String relocationStatus) {
        try {
            return Integer.parseInt(relocationStatus);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void createOrUpdateContract(
        final PersonDocument personDocument,
        final PersonLetterAndContractDto personLetterAndContractDto,
        final FileOutputStream logFos
    ) {
        final String folderId = personDocument.getFolderId();
        final PersonType personData = personDocument.getDocument().getPersonData();
        if (isNull(personData.getContracts())) {
            personData.setContracts(new Contracts());
        }
        final Optional<Contract> optionalContract = PersonUtils.getContractByOrderId(
            personDocument, personLetterAndContractDto.getOrderId()
        );
        if (optionalContract.isPresent()) {
            boolean hasChanges = false;
            final Contract contract = optionalContract.get();
            if (isNull(contract.getFiles())
                && (StringUtils.isNotEmpty(personLetterAndContractDto.getRtfContractToSignChedFileId())
                || StringUtils.isNotEmpty(personLetterAndContractDto.getPdfContractChedFileId()))) {
                contract.setFiles(new Contract.Files());
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getRtfContractToSignChedFileId())
                && !PersonUtils.getRtfContractToSignFile(contract).isPresent()) {
                addContractToSignFile(
                    contract,
                    personLetterAndContractDto.getRtfContractToSignChedFileId(),
                    RTF_CONTRACT_TO_SIGN_FILE_TYPE,
                    folderId
                );
                hasChanges = true;
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getPdfContractChedFileId())
                && !PersonUtils.getPdfContractFile(contract).isPresent()) {
                addContractToSignFile(
                    contract,
                    personLetterAndContractDto.getPdfContractChedFileId(),
                    PDF_CONTRACT_FILE_TYPE,
                    folderId
                );
                hasChanges = true;
            }
            if (hasChanges) {
                contract.setIsDgiData(true);
                writeInfoLog(
                    logFos,
                    String.format(
                        "Изменены данные о договоре, personDocumentId = %s, orderId = %s",
                        personDocument.getId(),
                        personLetterAndContractDto.getOrderId()
                    ),
                    String.format(
                        "PersonLetterAndContract. Updated contract data, personDocumentId = %s, orderId = %s",
                        personDocument.getId(),
                        personLetterAndContractDto.getOrderId()
                    )
                );
            }
        } else {
            final Contract contract = new Contract();
            contract.setOrderId(personLetterAndContractDto.getOrderId());
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getRtfContractToSignChedFileId())
                || StringUtils.isNotEmpty(personLetterAndContractDto.getPdfContractChedFileId())) {
                contract.setFiles(new Contract.Files());
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getRtfContractToSignChedFileId())) {
                addContractToSignFile(
                    contract,
                    personLetterAndContractDto.getRtfContractToSignChedFileId(),
                    RTF_CONTRACT_TO_SIGN_FILE_TYPE,
                    folderId
                );
            }
            if (StringUtils.isNotEmpty(personLetterAndContractDto.getPdfContractChedFileId())) {
                addContractToSignFile(
                    contract,
                    personLetterAndContractDto.getPdfContractChedFileId(),
                    PDF_CONTRACT_FILE_TYPE,
                    folderId
                );
            }
            contract.setIsDgiData(true);
            personData.getContracts().getContract().add(contract);
            writeInfoLog(
                logFos,
                String.format(
                    "Добавлен новый договор, personDocumentId = %s, orderId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getOrderId()
                ),
                String.format(
                    "PersonLetterAndContract. Added a new contract, personDocumentId = %s, orderId = %s",
                    personDocument.getId(),
                    personLetterAndContractDto.getOrderId()
                )
            );
        }
        actualizeRelocationStatus(personData, personDocument.getId(), 6, logFos);
        personDocumentService.updateDocument(personDocument, "createOrUpdateContract");
    }

    private void addLetterFile(final OfferLetter offerLetter, final String chedFileId, final String folderId) {
        final String fileStoreId = chedFileService.extractFileFromChedAndGetFileLink(chedFileId, folderId);
        final OfferLetter.Files.File file = createOfferLetterFile(fileStoreId, OFFER_FILE_TYPE, chedFileId);
        offerLetter.getFiles().getFile().add(file);
    }

    private void addAdministrativeDocumentFile(
        final OfferLetter offerLetter,
        final PersonLetterAndContractDto personLetterAndContractDto,
        final String folderId
    ) {
        final String fileStoreId = chedFileService.extractFileFromChedAndGetFileLink(
            personLetterAndContractDto.getAdministrativeDocumentChedFileId(), folderId
        );
        final OfferLetter.Files.File administrativeDocumentFile = createOfferLetterFile(
            fileStoreId,
            ADMINISTRATIVE_DOC_FILE_TYPE,
            personLetterAndContractDto.getAdministrativeDocumentChedFileId()
        );
        final LocalDate administrativeDocumentDate = personLetterAndContractDto.getAdministrativeDocumentDate();
        if (nonNull(administrativeDocumentDate)) {
            administrativeDocumentFile.setCreationDateTime(administrativeDocumentDate.atStartOfDay());
        }
        offerLetter.getFiles().getFile().add(administrativeDocumentFile);
    }

    private OfferLetter.Files.File createOfferLetterFile(
        final String fileStoreId, final String type, final String chedFileId
    ) {
        final OfferLetter.Files.File file = new OfferLetter.Files.File();
        file.setFileType(type);
        file.setFileLink(fileStoreId);
        file.setChedFileId(chedFileId);
        return file;
    }

    private void addContractToSignFile(
        final Contract contract, final String chedFileId, final String type, final String folderId
    ) {
        final String fileStoreId = chedFileService.extractFileFromChedAndGetFileLink(chedFileId, folderId);
        final Contract.Files.File file = createContractFile(fileStoreId, type, chedFileId);
        contract.getFiles().getFile().add(file);
    }

    private Contract.Files.File createContractFile(
        final String fileStoreId, final String type, final String chedFileId
    ) {
        final Contract.Files.File file = new Contract.Files.File();
        file.setFileType(type);
        file.setFileLink(fileStoreId);
        file.setChedFileId(chedFileId);
        return file;
    }
}
