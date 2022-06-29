package ru.croc.ugd.ssr.service.apartmentdefectconfirmation;

import org.springframework.data.domain.Page;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestCorrectDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectConfirmationDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestDefectDto;
import ru.croc.ugd.ssr.dto.apartmentdefectconfirmation.RestProcessReviewDefectDto;

public interface RestApartmentDefectConfirmationService {

    /**
     * Запросить у застройщика подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restDefectConfirmationDto данные об устранении дефектов
     */
    void requestConfirmation(final String id, final RestDefectConfirmationDto restDefectConfirmationDto);

    /**
     * Получить данные о дефектах.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param pageNum номер страницы
     * @param pageSize размер страницы
     * @param flat фильтр - квартира
     * @param flatElement фильтр - вид помещения
     * @param description фильтр - вид дефекта
     * @param isEliminated фильтр - устранен ли дефект
     * @param skipApproved не возвращать дефекты, изменения по которым уже согласованы
     * @param skipExcluded не возвращать дефекты, изменения по которым исключены из списка на согласование
     * @param sort строка сортировки
     * @return данные о дефектах
     */
    Page<RestDefectDto> fetchDefects(
        final String id,
        final int pageNum,
        final int pageSize,
        final String flat,
        final String flatElement,
        final String description,
        final Boolean isEliminated,
        final boolean skipApproved,
        final boolean skipExcluded,
        final String sort
    );

    /**
     * Согласовать или отклонить изменения по дефектам.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restProcessReviewDefectDto данные о согласовании/отклонении изменений по дефектам
     */
    void processReview(final String id, final RestProcessReviewDefectDto restProcessReviewDefectDto);

    /**
     * Сохранить подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restDefectConfirmationDto данные об устранении дефектов
     */
    void saveConfirmation(final String id, final RestDefectConfirmationDto restDefectConfirmationDto);

    /**
     * Скорректировать сведения и при необходимости запросить у застройщика подтверждение устранения дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @param restCorrectDefectDto скорректированные данные об устранении дефектов
     */
    void correct(final String id, final RestCorrectDefectDto restCorrectDefectDto);

    /**
     * Создать пустой документ подтверждения сведений об устранении дефектов.
     *
     * @return подтверждение сведений об устранении дефектов
     */
    RestDefectConfirmationDto create();

    /**
     * Получить документ подтверждения сведений об устранении дефектов.
     *
     * @param id ИД документа подтверждения сведений об устранении дефектов
     * @return подтверждение сведений об устранении дефектов
     */
    RestDefectConfirmationDto fetchById(final String id);
}
