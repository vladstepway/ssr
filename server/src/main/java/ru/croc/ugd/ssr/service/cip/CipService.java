package ru.croc.ugd.ssr.service.cip;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.db.dao.CipDocumentDao;
import ru.croc.ugd.ssr.db.projection.CipEmployeeProjection;
import ru.croc.ugd.ssr.dto.RestAddCipEmployeesDto;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.ResettlementRequestDocumentService;
import ru.croc.ugd.ssr.service.UserCipsCrudService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;
import ru.reinform.cdp.seqs.service.SequencesRemoteApi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Сервис для работы с ЦИП.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CipService extends AbstractDocumentService<CipDocument> {

    public static final String ACTIVE_CIP_STATUS = "01";
    private static final String NUMBER_SEQUENCE_CODE = "UGD_SSR_CIP_CODE_SEQ";

    private final SequencesRemoteApi sequencesRemoteApi;
    private final CipDocumentDao cipDao;
    private final UserCipsCrudService userCipsCrudService;
    private final ResettlementRequestDocumentService resettlementRequestDocumentService;

    private CipDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private CipDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, CipDocument.class);
    }

    /**
     * Перед созданием устанавливаем код CIP.
     *
     * @param document
     *            цип.
     */
    @Override
    public void beforeCreate(@Nonnull CipDocument document) {
        final String value = String.format("%05d", sequencesRemoteApi.nextValue(NUMBER_SEQUENCE_CODE, "0").getValue());
        document.getDocument().getCipData().setCipCode(value);
    }

    @Nonnull
    @Override
    public DocumentType<CipDocument> getDocumentType() {
        return SsrDocumentTypes.CIP;
    }

    /**
     * Получить все ЦИПы по округу.
     *
     * @param area код округа
     * @return список ЦИПов
     */
    public List<CipDocument> fetchByArea(String area) {
        return cipDao.fetchByArea(area)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получить ЦИП по идентификатору.
     *
     * @param id id
     * @return ЦИП
     */
    public Optional<CipDocument> fetchById(final String id) {
        try {
            return Optional.of(this.fetchDocument(id));
        } catch (Exception e) {
            log.warn("Не найден ЦИП с id: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Получить ЦИП type по идентификатору.
     *
     * @param cipId id
     * @return ЦИП
     */
    public Optional<CipType> fetchCipTypeById(final String cipId) {
        if (StringUtils.isEmpty(cipId)) {
            return Optional.empty();
        }
        return this.fetchById(cipId)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData);
    }

    /**
     * Получить все ЦИПы, связанные с ОКСом.
     *
     * @param ccoId
     *            id ОКС (с которым связан ЦИП)
     * @return список ЦИПов
     */
    public List<CipDocument> fetchByCcoId(String ccoId) {
        return cipDao.fetchByCcoId(ccoId).stream().map(this::parseDocumentData).collect(Collectors.toList());
    }

    /**
     * Получить все UNOMs, связанные с personId.
     *
     * @param personId personId
     * @return список UNOMs.
     */
    public List<String> fetchUnomsFromCipByPersonId(String personId) {
        return cipDao.fetchUnomsFromCipByPersonId(personId);
    }

    /**
     * Получить один ЦИП, связанный с ОКСом.
     *
     * @param ccoId
     *            id ОКС (с которым связан ЦИП)
     * @return список ЦИПов
     */
    public CipDocument fetchOneByCcoId(String ccoId) {
        List<CipDocument> cipDocuments = cipDao.fetchByCcoId(ccoId)
            .stream()
            .map(this::parseDocumentData)
            .filter(cip -> cip.getDocument().getCipData().getCipStatus().equals(ACTIVE_CIP_STATUS))
            .collect(Collectors.toList());
        if (cipDocuments.isEmpty()) {
            throw new RuntimeException(String.format("Не найден активный ЦИП связанный с ОКС: %s", ccoId));
        }
        return cipDocuments.get(0);
    }


    /**
     * Получить работников ЦИП по ИД ЦИП.
     *
     * @param cipId ИД ЦИПd
     * @return список работников.
     */
    public List<CipEmployeeProjection> fetchCipEmployees(String cipId) {
        return cipDao.fetchCipEmployees(cipId);
    }

    public Optional<CipType> getPersonCip(final PersonDocument personDocument) {
        return StreamUtils
            .or(PersonUtils.getCipId(personDocument),
                () -> resettlementRequestDocumentService.getCipIdForPerson(personDocument))
            .flatMap(this::fetchCipById);
    }

    public Optional<CipType> getPersonCip(final PersonType personType) {
        return StreamUtils
            .or(PersonUtils.getCipId(personType),
                () -> resettlementRequestDocumentService.getCipIdForPerson(personType))
            .flatMap(this::fetchCipById);
    }

    public Optional<CipType> fetchCipById(final String cipId) {
        try {
            return ofNullable(cipId)
                .map(this::fetchDocument)
                .map(CipDocument::getDocument)
                .map(Cip::getCipData);
        } catch (Exception e) {
            log.warn("Не найден ЦИП с id: {}", cipId);
            return Optional.empty();
        }
    }

    /**
     * Получить все ЦИПы.
     *
     * @return список ЦИПов
     */
    public List<CipDocument> fetchAll() {
        return cipDao.fetchAll()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public CipDocument updateCipEmployees(final String cipId, final List<String> newEmployeeLogins) {
        final CipDocument cipDocument = fetchDocument(cipId);
        final List<String> currentEmployeeLogins = cipDocument.getEmployeeLogins();

        currentEmployeeLogins.stream()
            .filter(StreamUtils.not(newEmployeeLogins::contains))
            .forEach(login -> userCipsCrudService.deleteUserCipsByLogin(login, cipId));

        newEmployeeLogins.stream()
            .filter(StreamUtils.not(currentEmployeeLogins::contains))
            .forEach(login -> userCipsCrudService.addUserCipsByLogin(login, cipId));

        cipDocument.updateEmployees(newEmployeeLogins);

        updateDocument(cipId, cipDocument, true, true, null);

        return cipDocument;
    }

    public void addCipEmployees(final RestAddCipEmployeesDto addCipEmployeesDto) {
        final List<String> cipIds = ofNullable(addCipEmployeesDto.getCipIds())
            .orElseGet(Collections::emptyList);
        final List<String> employeeLogins = ofNullable(addCipEmployeesDto.getEmployeeLogins())
            .orElseGet(Collections::emptyList);

        cipIds.stream()
            .map(this::fetchById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(cipDocument -> addCipEmployees(cipDocument, employeeLogins));
    }

    private void addCipEmployees(final CipDocument cipDocument, final List<String> newEmployeeLogins) {
        final String cipId = cipDocument.getId();
        final List<String> currentEmployeeLogins = cipDocument.getEmployeeLogins();

        final List<String> employeeLoginsToAdd = newEmployeeLogins.stream()
            .filter(StreamUtils.not(currentEmployeeLogins::contains))
            .map(login -> {
                userCipsCrudService.addUserCipsByLogin(login, cipId);
                return login;
            })
            .collect(Collectors.toList());

        cipDocument.addEmployees(employeeLoginsToAdd);

        updateDocument(cipId, cipDocument, true, true, null);
    }
}
