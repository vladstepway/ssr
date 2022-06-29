package ru.croc.ugd.ssr.service.personaldocument;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.projection.TenantProjection;
import ru.croc.ugd.ssr.dto.personaldocument.PdfFileDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestAddApplicationDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestCreatePersonalDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestMergeDocumentFileDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestMergeFilesDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestParsedDocumentDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonDataDocumentsDto;
import ru.croc.ugd.ssr.dto.personaldocument.RestPersonalDocumentDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.PersonalDocumentMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentApplicationDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentDocument;
import ru.croc.ugd.ssr.personaldocument.DocumentType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocument;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplication;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentApplicationType;
import ru.croc.ugd.ssr.personaldocument.PersonalDocumentType;
import ru.croc.ugd.ssr.personaldocument.TenantType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.bpm.BpmService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentDocumentService;
import ru.croc.ugd.ssr.service.personaldocument.type.SsrPersonalDocumentTypeService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultRestPersonalDocumentService implements RestPersonalDocumentService {

    private static final String APPLICATION_DOCUMENT_TYPE_CODE = "101";
    private static final String PROCESS_PARSE_DOCUMENTS_KEY = "ugdssrPersonalDocument_parseDocuments";

    private final PersonDocumentService personDocumentService;
    private final PersonalDocumentDocumentService personalDocumentDocumentService;
    private final PersonalDocumentApplicationDocumentService personalDocumentApplicationDocumentService;
    private final PersonalDocumentMapper personalDocumentMapper;
    private final SsrFilestoreService ssrFilestoreService;
    private final SsrPersonalDocumentTypeService ssrPersonalDocumentTypeService;
    private final BpmService bpmService;

    @Override
    public RestPersonalDocumentDto fetchById(final String id) {
        final PersonalDocumentDocument personalDocumentDocument = personalDocumentDocumentService.fetchDocument(id);
        return personalDocumentMapper.toRestPersonalDocumentDto(personalDocumentDocument);
    }

    @Override
    public RestPersonDataDocumentsDto fetchAll(final String personDocumentId) {
        final String affairId = retrieveAffairId(personDocumentId);

        final List<PersonalDocumentType> personalDocuments = personalDocumentDocumentService
            .findAllByAffairId(affairId)
            .stream()
            .map(PersonalDocumentDocument::getDocument)
            .map(PersonalDocument::getPersonalDocumentData)
            .collect(Collectors.toList());

        return personalDocumentMapper.toRestPersonalDocumentsDto(
            personalDocuments, this::retrievePersonalDocumentApplication
        );
    }

    @Override
    public RestPersonalDocumentDto create(final RestCreatePersonalDocumentDto body) {
        final String affairId = retrieveAffairId(body.getPersonDocumentId());
        final PersonalDocumentDocument personalDocumentDocument = personalDocumentMapper.toPersonalDocumentDocument(
            affairId, body.getUnionFileStoreId(), body.getAddressFrom(), body.getLetterId()
        );

        personalDocumentDocumentService.createDocument(personalDocumentDocument, true, null);

        final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument().getPersonalDocumentData();

        createParseDocumentsProcess(personalDocumentDocument.getId())
            .ifPresent(personalDocument::setProcessInstanceId);

        personalDocumentDocumentService
            .updateDocument(personalDocumentDocument.getId(), personalDocumentDocument, true, true, null);

        return personalDocumentMapper.toRestPersonalDocumentDto(personalDocumentDocument);
    }

    private Optional<String> createParseDocumentsProcess(final String personalDocumentId) {
        final Map<String, String> variablesMap = Collections.singletonMap(
            BpmService.PROCESS_DOCUMENT_KEY_ENTITY_ID_VAR, personalDocumentId
        );

        return ofNullable(bpmService.startNewProcess(PROCESS_PARSE_DOCUMENTS_KEY, variablesMap));
    }

    @Override
    public void parse(final String id, final List<RestParsedDocumentDto> restParsedDocumentDtos) {
        final PersonalDocumentDocument personalDocumentDocument = personalDocumentDocumentService.fetchDocument(id);
        final PersonalDocumentType personalDocument = of(personalDocumentDocument)
            .map(PersonalDocumentDocument::getDocument)
            .map(PersonalDocument::getPersonalDocumentData)
            .orElseThrow(() -> new SsrException("Не найдены сведения о предоставленных документах " + id));

        personalDocument.setAcceptanceDateTime(LocalDateTime.now());

        final List<TenantType> tenants = retrieveTenants(
            restParsedDocumentDtos, personalDocument.getUnionFileStoreId(), personalDocumentDocument.getFolderId()
        );
        updateTenants(personalDocument, tenants);

        final List<DocumentType> documents = retrieveDocuments(
            restParsedDocumentDtos, personalDocument.getUnionFileStoreId(), personalDocumentDocument.getFolderId()
        );
        updateDocuments(personalDocument, documents);

        personalDocumentDocumentService.updateDocument(
            personalDocumentDocument.getId(), personalDocumentDocument, true, true, null
        );

        finishBpmProcess(personalDocument.getProcessInstanceId());
    }

    private void finishBpmProcess(final String processInstanceId) {
        if (org.springframework.util.StringUtils.hasText(processInstanceId)) {
            try {
                bpmService.deleteProcessInstance(processInstanceId);
            } catch (Exception e) {
                log.warn(
                    "Unable to finish bpm process for personal document due to: {}", e.getMessage(), e
                );
            }
        }
    }

    @Override
    public byte[] merge(final RestMergeFilesDto body) {
        final List<String> fileIds = new ArrayList<>();

        addFileStoreId(fileIds, body.getApplicationFileStoreId());
        addFileStoreId(fileIds, body.getUnionFileStoreId());

        addDocumentFiles(fileIds, body.getTitleDocumentFiles());
        addTenantDocumentFiles(fileIds, body.getTenantDocumentFilesList());

        return ssrFilestoreService.loadAndMergeFilesToSinglePdf(fileIds);
    }

    @Override
    public RestPersonalDocumentDto addApplicationDocument(final RestAddApplicationDocumentDto body) {
        final PersonalDocumentDocument personalDocumentDocument = personalDocumentDocumentService
            .findByUnionFileStoreId(body.getUnionFileStoreId())
            .orElseThrow(() -> new SsrException(
                "Не найдены сведения о документах для единого файла документов " + body.getUnionFileStoreId()
            ));

        addApplicationDocument(personalDocumentDocument, body.getApplicationFileStoreId());

        personalDocumentDocumentService.updateDocument(
            personalDocumentDocument.getId(), personalDocumentDocument, true, true, null
        );

        return personalDocumentMapper.toRestPersonalDocumentDto(personalDocumentDocument);
    }

    private void addApplicationDocument(
        final PersonalDocumentDocument personalDocumentDocument, final String applicationFileStoreId
    ) {
        final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument().getPersonalDocumentData();

        if (isNull(personalDocument.getDocuments())) {
            personalDocument.setDocuments(new PersonalDocumentType.Documents());
        }

        final DocumentType applicationDocumentType = personalDocumentMapper.toApplicationDocumentType(
            applicationFileStoreId, APPLICATION_DOCUMENT_TYPE_CODE
        );
        personalDocument.getDocuments().getDocument().add(applicationDocumentType);
    }

    private void addFileStoreId(final List<String> fileIds, final String fileStoreId) {
        if (StringUtils.isNotEmpty(fileStoreId)) {
            fileIds.add(fileStoreId);
        }
    }

    private void addDocumentFiles(final List<String> fileIds, final List<RestMergeDocumentFileDto> documentFiles) {
        if (nonNull(documentFiles)) {
            documentFiles.stream()
                .sorted(Comparator.comparing(
                    documentFile -> ssrPersonalDocumentTypeService.getSortOrderByCode(
                        documentFile.getDocumentTypeCode()
                    ),
                    Comparator.nullsFirst(Integer::compareTo)
                ))
                .forEach(documentFile -> fileIds.addAll(documentFile.getFileStoreIds()));
        }
    }

    private void addTenantDocumentFiles(
        final List<String> fileIds, final List<List<RestMergeDocumentFileDto>> tenantDocumentFilesList
    ) {
        if (nonNull(tenantDocumentFilesList)) {
            tenantDocumentFilesList.forEach(tenantDocumentFiles -> addDocumentFiles(fileIds, tenantDocumentFiles));
        }
    }

    private PersonalDocumentApplicationType retrievePersonalDocumentApplication(final String applicationDocumentId) {
        return ofNullable(applicationDocumentId)
            .map(personalDocumentApplicationDocumentService::fetchDocument)
            .map(PersonalDocumentApplicationDocument::getDocument)
            .map(PersonalDocumentApplication::getPersonalDocumentApplicationData)
            .orElse(null);
    }

    private String retrieveAffairId(final String personDocumentId) {
        return personDocumentService.fetchById(personDocumentId)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getAffairId)
            .orElseThrow(() -> new SsrException("Не найдена информация о семье жителя " + personDocumentId));
    }

    private void updateTenants(final PersonalDocumentType personalDocument, final List<TenantType> tenants) {
        personalDocument.setTenants(new PersonalDocumentType.Tenants());
        personalDocument.getTenants().getTenant().addAll(tenants);
    }

    private void updateDocuments(final PersonalDocumentType personalDocument, final List<DocumentType> documents) {
        personalDocument.setDocuments(new PersonalDocumentType.Documents());
        personalDocument.getDocuments().getDocument().addAll(documents);
    }

    private List<TenantType> retrieveTenants(
        final List<RestParsedDocumentDto> restParsedDocumentDtos, final String unionFileStoreId, final String folderId
    ) {
        final Map<String, List<RestParsedDocumentDto>> tenants = restParsedDocumentDtos.stream()
            .filter(restParsedDocumentDto -> StringUtils.isNotEmpty(restParsedDocumentDto.getPersonDocumentId()))
            .collect(Collectors.toMap(
                RestParsedDocumentDto::getPersonDocumentId,
                Arrays::asList,
                (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList())
            ));
        return tenants.entrySet()
            .stream()
            .map(entry -> personalDocumentMapper
                .toTenantType(
                    retrieveTenant(entry.getKey()),
                    entry.getValue(),
                    unionFileStoreId,
                    folderId,
                    this::retrieveFileStoreId,
                    this::retrieveFileName
                )
            )
            .collect(Collectors.toList());
    }

    private List<DocumentType> retrieveDocuments(
        final List<RestParsedDocumentDto> restParsedDocumentDtos, final String unionFileStoreId, final String folderId
    ) {
        return restParsedDocumentDtos.stream()
            .filter(restParsedDocumentDto -> StringUtils.isEmpty(restParsedDocumentDto.getPersonDocumentId()))
            .map(restParsedDocumentDto -> personalDocumentMapper
                .toDocumentType(
                    restParsedDocumentDto,
                    unionFileStoreId,
                    folderId,
                    null,
                    this::retrieveFileStoreId,
                    this::retrieveFileName
                )
            )
            .collect(Collectors.toList());
    }

    private TenantProjection retrieveTenant(final String personDocumentId) {
        return personDocumentService.fetchTenantById(personDocumentId)
            .orElse(null);
    }

    private String retrieveFileStoreId(final PdfFileDto pdfFileDto) {
        return ssrFilestoreService.createPdfFile(
            pdfFileDto.getUnionFileStoreId(),
            pdfFileDto.getPageNumbers(),
            pdfFileDto.getFileName(),
            pdfFileDto.getFolderId()
        );
    }

    private String retrieveFileName(final String typeCode, final String personId) {
        final String typeName = ssrPersonalDocumentTypeService.getNameByCode(typeCode);

        return StringUtils.isNotEmpty(personId)
            ? personId + "_" + typeName + "_" + LocalDateTime.now() + ".pdf"
            : typeName + "_" + LocalDateTime.now() + ".pdf";
    }

    @Override
    public RestPersonalDocumentDto getLastPersonalDocument(final String affairId, final boolean isParsed) {
        if (isParsed) {
            return personalDocumentDocumentService.findAllByAffairId(affairId)
                .stream()
                .filter(this::isParsed)
                .max(Comparator.comparing(
                    personalDocumentDocument -> personalDocumentDocument.getDocument()
                        .getPersonalDocumentData()
                        .getAcceptanceDateTime(),
                    Comparator.nullsFirst(LocalDateTime::compareTo)
                ))
                .map(personalDocumentMapper::toRestPersonalDocumentDto)
                .orElse(null);
        } else {
            return personalDocumentDocumentService.findLastByAffairId(affairId)
                .map(personalDocumentMapper::toRestPersonalDocumentDto)
                .orElse(null);
        }
    }

    private boolean isParsed(final PersonalDocumentDocument personalDocumentDocument) {
        final PersonalDocumentType personalDocument = personalDocumentDocument.getDocument().getPersonalDocumentData();
        return nonNull(personalDocument.getAcceptanceDateTime());
    }
}
