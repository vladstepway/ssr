package ru.croc.ugd.ssr.service;

import org.springframework.data.domain.Page;
import ru.croc.ugd.ssr.ApartmentInspectionType;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.AcceptedDefectsActDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.ApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.CloseActWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.DelayReasonDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.IntegrationLogDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.PersonConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestApartmentInspectionReportDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestCloseActsWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestConfirmClosingWithoutConsentDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestDefectsEliminationDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestFileDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestSendTasksToGeneralContractorDto;
import ru.croc.ugd.ssr.dto.apartmentinspection.RestUpdateDefectDto;
import ru.croc.ugd.ssr.model.ApartmentInspectionDocument;
import ru.croc.ugd.ssr.model.ssrcco.SsrCcoDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис по утранению дефектов квартиры.
 */
public interface ApartmentInspectionService {

    /**
     * Получения акта по идентификатору.
     *
     * @param id id
     * @return акт
     */
    ApartmentInspectionDocument fetchDocument(final String id);

    /**
     * Создать документ.
     *
     * @param document Документ
     * @return новый документ
     */
    ApartmentInspectionDocument createDocument(final ApartmentInspectionDocument document);

    /**
     * Создать документ.
     *
     * @param document Документ
     * @return ID нового документа
     */
    ApartmentInspectionDocument createOrUpdateDocument(final ApartmentInspectionDocument document);

    /**
     * Найти документ который на который был запущен процесс.
     *
     * @param personId Житель ID
     * @return Документ
     */
    ApartmentInspectionDocument findDocumentWithStartedProcessByPersonId(final String personId);

    /**
     * Помечает акт как удаленный если процесс устранения дефектов еще не начат.
     *
     * @param actId Act ID
     */
    void safeDeleteActByActId(final String actId);

    /**
     * Проставляет дату отправки уведомление. Обновляет документ.
     *
     * @param apartmentInspectionId apartmentInspectionId
     */
    void setCurrentDefectsEliminatedDate(final String apartmentInspectionId);

    /**
     * Принудительно завершает процесс устранения дефекта на подрядчике.
     *
     * @param apartmentInspectionDocumentId Id Документа
     * @param sendDefectsFixedIfNeeded      sendDefectsFixedIfNeeded
     */
    void forceFinishProcess(final String apartmentInspectionDocumentId, final boolean sendDefectsFixedIfNeeded);

    /**
     * Принудительно завершает процесс устранения дефекта на подрядчике.
     *
     * @param apartmentInspectionDocument apartmentInspectionDocument
     */
    void forceFinishProcess(final ApartmentInspectionDocument apartmentInspectionDocument);

    /**
     * Обработка отказа в коммиссионном осмотре.
     *
     * @param apartmentInspectionDocumentId apartmentInspectionDocumentId
     * @param isFlatRefusal                 isFlatRefusal
     */
    void processCommissionInspectionRefusal(final String apartmentInspectionDocumentId, final boolean isFlatRefusal);

    /**
     * Создает документ и начинает процесс устранения дефектов / назначает задачу на подрядчика.
     *
     * @param document Документ
     * @return Id Документа
     */
    String createDocumentAndStartDefectEliminationProcess(final ApartmentInspectionDocument document);

    /**
     * Возвращает отчеты по дефектам.
     *
     * @param personId               personId
     * @param commissionInspectionId commissionInspectionId
     * @return отчеты по дефектам
     */
    List<ApartmentInspectionDto> findAll(final String personId, final String commissionInspectionId);

    /**
     * Обновить документ.
     *
     * @param document Документ
     * @return обновленный документ
     */
    ApartmentInspectionDocument updateDocument(final ApartmentInspectionDocument document);

    /**
     * Обновить документ.
     *
     * @param document документ
     * @param notes    комментарий
     * @return обновленный документ
     */
    ApartmentInspectionDocument updateDocument(final ApartmentInspectionDocument document, final String notes);

    /**
     * Возвращает все документы.
     *
     * @return список документов
     */
    List<ApartmentInspectionDocument> getApartmentInspections();

    /**
     * Получить ApartmentInspection по personId.
     *
     * @param personId PersonId
     * @return ApartmentInspection
     */
    List<ApartmentInspectionDocument> fetchByPersonId(final String personId);

    /**
     * Актуализировать список кандидатов активных задач.
     *
     * @param documentId documentId
     */
    void actualizeTasksCandidates(final String documentId);

    /**
     * Актуализировать список кандидатов активных задач по всем актам.
     */
    void actualizeTaskCandidates();

    /**
     * Актуализировать список кандидатов активных задач по SsrCco.
     */
    void actualizeTaskCandidatesBySsrCco();

    /**
     * Актуализировать список кандидатов активных задач по SsrCco.
     *
     * @param unom уном
     */
    void actualizeTaskCandidatesBySsrCco(final String unom);

    /**
     * Получить ApartmentInspection по unom и flatNumber.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @return список ApartmentInspection
     */
    List<ApartmentInspectionDocument> fetchByUnomAndFlatNumber(final String unom, final String flatNumber);

    /**
     * Уведомить об устраненых дефектах.
     */
    void defectsFixed(final String apartmentInspectionId);

    /**
     * Закрыть акт по дефектам.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param personConsentDto      personConsentDto
     */
    void close(final String apartmentInspectionId, final PersonConsentDto personConsentDto);

    /**
     * Внести согласие жителя с устранением дефектов.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param personConsentDto      personConsentDto
     */
    void addPersonConsent(final String apartmentInspectionId, final PersonConsentDto personConsentDto);

    /**
     * Создать акт на основе закрытого.
     *
     * @param apartmentInspection apartmentInspection
     * @return apartmentInspectionDocument
     */
    ApartmentInspectionDocument createDocumentFromPrevious(final ApartmentInspectionType apartmentInspection);

    /**
     * Получить ApartmentInspection по unom.
     *
     * @param unom уном
     * @return список ApartmentInspection
     */
    List<ApartmentInspectionDocument> fetchByUnom(final String unom);

    /**
     * Создать акт привязанный к комиссионному осмотру.
     *
     * @param commissionInspectionId commissionInspectionId
     * @param commissionInspection   commissionInspection
     * @param confirmedDateTime      confirmedDateTime
     * @return apartmentInspectionDocument
     */
    ApartmentInspectionDocument createPendingApartmentInspectionDocument(
        final String commissionInspectionId,
        final CommissionInspectionData commissionInspection,
        final LocalDateTime confirmedDateTime
    );

    void deleteAll();

    /**
     * Обновить акт принятых устранений дефектов.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param acceptedDefectsActDto акт принятых устранений дефектов
     * @return акт осмотра квартиры
     */
    ApartmentInspectionDto updateAcceptedDefectsAct(
        final String apartmentInspectionId, final AcceptedDefectsActDto acceptedDefectsActDto
    );

    /**
     * Закрыть акт по дефектам без получения согласия жителя.
     *
     * @param apartmentInspectionId     apartmentInspectionId
     * @param closeActWithoutConsentDto closeActWithoutConsentDto
     */
    void closeWithoutConsent(
        final String apartmentInspectionId, final CloseActWithoutConsentDto closeActWithoutConsentDto
    );

    /**
     * Закрыть акты по дефектам без получения согласия жителя.
     *
     * @param restCloseActsWithoutConsentDto restCloseActsWithoutConsentDto
     */
    void closeWithoutConsent(final RestCloseActsWithoutConsentDto restCloseActsWithoutConsentDto);

    /**
     * Подтвердить закрытие акта по дефектам без получения согласия жителя.
     *
     * @param apartmentInspectionId               apartmentInspectionId
     * @param restConfirmClosingWithoutConsentDto restConfirmClosingWithoutConsentDto
     */
    void confirmClosingWithoutConsent(
        final String apartmentInspectionId,
        final RestConfirmClosingWithoutConsentDto restConfirmClosingWithoutConsentDto
    );

    /**
     * Продление срока устранения дефектов.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param delayReasonDto        Данные о сроке и причине продления
     */
    void defectsProlongation(
        final String apartmentInspectionId, final DelayReasonDto delayReasonDto
    );

    /**
     * Продление срока устранения дефектов и уведомление об устраненных дефектах.
     *
     * @param apartmentInspectionId apartmentInspectionId
     * @param delayReasonDto        Данные о сроке и причине продления
     */
    void defectsElimination(
        final String apartmentInspectionId, final DelayReasonDto delayReasonDto
    );

    /**
     * Установка планового срока устранения дефектов по списку актов.
     *
     * @param restDefectsEliminationDto Данные о плановом сроке устранения дефектов по актам
     */
    void defectsElimination(final RestDefectsEliminationDto restDefectsEliminationDto);

    /**
     * Назначение задач на генподрядчика.
     *
     * @param restSendTasksToGeneralContractorDto Данные о генподрядчике по актам
     */
    void sendTasksToGeneralContractor(final RestSendTasksToGeneralContractorDto restSendTasksToGeneralContractorDto);

    /**
     * Назначение задач на генподрядчика.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param generalContractorId   ИД генерального подрядчика
     */
    void sendTaskToGeneralContractor(final String apartmentInspectionId, final String generalContractorId);

    /**
     * Получить список логов межведомственной интеграции.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @return логи межведомственной интеграции
     */
    List<IntegrationLogDto> fetchIntegrationLogs(final String apartmentInspectionId);

    /**
     * Обновить список дефектов.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @param restUpdateDefectDto   данные о дефектах
     * @return акт осмотра квартиры
     */
    ApartmentInspectionDto updateDefects(
        final String apartmentInspectionId, final RestUpdateDefectDto restUpdateDefectDto
    );

    /**
     * Добавить ИД дефектов.
     */
    void actualizeDefectIds();

    /**
     * Получить данные о дефектах.
     *
     * @param pageNum      номер страницы
     * @param pageSize     размер страницы
     * @param unom         UNOM заселяемого дома
     * @param flat         фильтр - квартира
     * @param flatElement  фильтр - вид помещения
     * @param description  фильтр - вид дефекта
     * @param isEliminated фильтр - устранен ли дефект
     * @param isDeveloper  данные запрашивает застройщик или генподрядчик
     * @param sort         строка сортировки
     * @return данные о дефектах
     */
    Page<RestDefectDto> fetchDefects(
        final int pageNum,
        final int pageSize,
        final String unom,
        final String flat,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean isDeveloper,
        final String sort
    );

    /**
     * Получить акт по дефектам.
     *
     * @param apartmentInspectionId ИД документа акта осмотра квартиры
     * @return aкт по дефектам
     */
    ApartmentInspectionDto fetchApartmentInspectionById(final String apartmentInspectionId);

    /**
     * Получить список актов по дефектам по unom и flatNumber.
     *
     * @param unom       уном
     * @param flatNumber номер квартиры
     * @return список ApartmentInspection
     */
    List<RestApartmentInspectionDto> fetchAllByUnomAndFlatNumber(final String unom, final String flatNumber);

    /**
     * Скачать все акты в zip.
     *
     * @param unom       уном
     * @param flat       номер квартиры
     * @param actNum     номер акта
     * @param filingDate дата регистрации акта
     * @return файл zip
     */
    RestFileDto getZipReport(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate
    );

    /**
     * Выгрузить список дефектов по дому в xlsx.
     *
     * @param unom                      уном
     * @param flat                      номер квартиры
     * @param actNum                    номер акта
     * @param filingDate                дата регистрации акта
     * @param flatElement               помещение
     * @param description               описание дефекта
     * @param isEliminated              отметка об устранении
     * @param skipNotPlannedElimination возвращать только дефекты, по которым запланировано устранение
     * @return файл zip
     */
    RestFileDto getExcelReport(
        final String unom,
        final String flat,
        final String actNum,
        final LocalDate filingDate,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean skipNotPlannedElimination
    );

    /**
     * Скачать pdf акта по дефектам.
     *
     * @param id ИД акта по дефектам
     * @return pdf-файл акта по дефектам
     */
    RestFileDto getReport(final String id);

    /**
     * Создать pdf акта по дефектам.
     *
     * @param restApartmentInspectionReportDto данные для формирования pdf
     * @return pdf-файл акта по дефектам
     */
    RestFileDto createReport(final RestApartmentInspectionReportDto restApartmentInspectionReportDto);

    /**
     * Добавить информацию о блокировке/разблокировке дефектов.
     *
     * @param apartmentInspectionId ИД акта по дефектам
     * @param defectIds идентификаторы дефектов, для которых нужно установить/снять блокировку
     * @param isBlocked признок блокировки
     */
    void addBlockingInformation(
        final String apartmentInspectionId, final List<String> defectIds, final boolean isBlocked
    );

    /**
     * Добавить информацию о блокировке/разблокировке дефектов.
     *
     * @param apartmentInspectionDocument акт по дефектам
     * @param defectIds идентификаторы дефектов, для которых нужно установить/снять блокировку
     * @param isBlocked признок блокировки
     */
    void addBlockingInformation(
        final ApartmentInspectionDocument apartmentInspectionDocument,
        final List<String> defectIds,
        final boolean isBlocked
    );

    /**
     * Получить ИД семьи из акта по дефектам.
     *
     * @param apartmentInspectionId ИД акт по дефектам
     * @return ИД семьи
     */
    String retrieveAffairId(final String apartmentInspectionId);

    /**
     * Получить документ ОКСа в системе ССР для акта по дефектам.
     *
     * @param apartmentInspectionDocument акт по дефектам
     * @return документ ОКСа
     */
    SsrCcoDocument retrieveSsrCcoDocument(final ApartmentInspectionDocument apartmentInspectionDocument);
}
