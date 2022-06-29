package ru.croc.ugd.ssr.service.document;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.utils.PersonUtils.getOfferLetter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.commission.CommissionInspection;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.commission.LetterType;
import ru.croc.ugd.ssr.db.dao.CommissionInspectionDao;
import ru.croc.ugd.ssr.db.projection.CommissionInspectionProjection;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionFlowStatus;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.service.bpm.BpmEntityService;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * CommissionInspectionDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommissionInspectionDocumentService extends AbstractDocumentService<CommissionInspectionDocument>
    implements BpmEntityService {

    private final CommissionInspectionDao commissionInspectionDao;

    @NotNull
    @Override
    public DocumentType<CommissionInspectionDocument> getDocumentType() {
        return SsrDocumentTypes.COMMISSION_INSPECTION;
    }

    @Override
    public String getProcessInstanceId(String documentId) {
        return fetchDocument(documentId).getDocument().getCommissionInspectionData().getProcessInstanceId();
    }

    /**
     * Ищем заявления по статусу.
     * @return список заявлений.
     */
    public List<CommissionInspectionDocument> findByStatusIdIn(final List<CommissionInspectionFlowStatus> statusList) {
        final List<String> statusIdList = statusList
            .stream()
            .map(CommissionInspectionFlowStatus::getId)
            .collect(Collectors.toList());
        return commissionInspectionDao.findByStatusIdIn(statusIdList)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Получение комиссионного осмотра по ЕНО.
     * @param eno ЕНО.
     * @return комиссионный осмотр.
     */
    public Optional<CommissionInspectionDocument> findByEno(final String eno) {
        return commissionInspectionDao.findByEno(eno)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    /**
     * Существуют активные заявки на КО по адресу.
     *
     * @param unom УНОМ заселяемого дома
     * @param flatNumber Номер заселяемой квартиры
     * @return существуют ли активные заявки
     */
    public boolean existsActiveByUnomAndFlatNumber(final String unom, final String flatNumber) {
        return commissionInspectionDao.existsActiveByUnomAndFlatNumber(unom, flatNumber);
    }

    /**
     * Существуют активные заявки на КО по letterId.
     *
     * @param letterId letterId
     * @return существуют ли активные заявки
     */
    public boolean existsActiveByLetterId(final String letterId) {
        return commissionInspectionDao.existsActiveByLetterId(letterId);
    }

    private CommissionInspectionDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private CommissionInspectionDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, CommissionInspectionDocument.class);
    }

    /**
     * Получить ЦИП ИД.
     * @param commissionInspectionDocument данные КО
     * @param person данные персоны
     * @return ЦИП ИД
     */
    public String getCipId(
        final CommissionInspectionDocument commissionInspectionDocument, final PersonDocument person
    ) {
        return Optional.of(commissionInspectionDocument.getDocument())
            .map(CommissionInspection::getCommissionInspectionData)
            .map(CommissionInspectionData::getLetter)
            .map(LetterType::getId)
            .filter(StringUtils::hasText)
            .flatMap(letterId -> getOfferLetter(person, letterId))
            .map(PersonType.OfferLetters.OfferLetter::getIdCIP)
            .orElseGet(() -> PersonUtils.getCipId(person)
                .orElse(null)
            );
    }

    /**
     * Returns all commission inspections.
     *
     * @param personId filters by personId
     * @param statuses filters by status
     * @return list of commission inspections
     */
    public List<CommissionInspectionDocument> findAll(final String personId, final String statuses) {
        final List<String> statusList = ofNullable(statuses)
            .map(statusString -> statusString.split(","))
            .map(Arrays::asList)
            .orElse(Collections.emptyList());
        final List<DocumentData> documentDataList = statusList.isEmpty()
            ? commissionInspectionDao.findAll(personId)
            : commissionInspectionDao.findAll(personId, statusList);
        return documentDataList
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Returns commission inspection projections filtered by ccoUnom and date between and time confirmed.
     * @param ccoUnom ccoUnom
     * @param startDate startDate
     * @param endDate endDate
     * @return commission inspection projections
     */
    public List<CommissionInspectionProjection> findAllByCcoUnomAndDateBetweenAndTimeConfirmedStatus(
        final String ccoUnom, final LocalDateTime startDate, final LocalDateTime endDate
    ) {
        return commissionInspectionDao
            .findAllByCcoUnomAndDateBetweenAndTimeConfirmedStatus(ccoUnom, startDate, endDate);
    }

    /**
     * Returns commission inspections by letterId.
     *
     * @param letterId letterId
     * @return commission inspections by letterId
     */
    public List<CommissionInspectionDocument> findRefuseableByLetterId(final String letterId) {
        return commissionInspectionDao.findRefuseableByLetterId(letterId)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

}
