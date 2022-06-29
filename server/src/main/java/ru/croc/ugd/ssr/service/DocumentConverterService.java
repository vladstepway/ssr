package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.RoomType;
import ru.croc.ugd.ssr.db.dao.CipDocumentDao;
import ru.croc.ugd.ssr.db.dao.FlatDao;
import ru.croc.ugd.ssr.db.dao.PersonDocumentDao;
import ru.croc.ugd.ssr.db.dao.RealEstateDocumentDao;
import ru.croc.ugd.ssr.db.dao.RoomDao;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.utils.RealEstateUtils;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;


/**
 * Сервис для конвертеров по всем сущностям.
 */
@Service
@AllArgsConstructor
public class DocumentConverterService {

    private final PersonDocumentDao personDocumentDao;
    private final CipDocumentDao cipDao;
    private final RealEstateDocumentDao realEstateDocumentDao;
    private final FlatDao flatDao;
    private final RoomDao roomDao;
    private final JsonMapper jsonMapper;

    /**
     * Generic parser for documentData.
     * @param documentData documentData.
     * @param clazz clazz.
     * @param <T> t.
     * @return object.
     */
    public <T> T parseDocumentData(final DocumentData documentData, final Class<T> clazz) {
        return parseJson(documentData.getJsonData(), clazz);
    }

    /**
     * Generic parser for documentData list.
     * @param documentDataList documentDataList.
     * @param clazz clazz.
     * @param <T> t.
     * @return parsed object.
     */
    public <T> List<T> parseDocumentData(final List<DocumentData> documentDataList, final Class<T> clazz) {
        return documentDataList.stream()
            .map(documentData -> parseDocumentData(documentData, clazz))
            .collect(Collectors.toList());
    }

    /**
     * Generic parser for serialized document.
     * @param documentJson documentJson.
     * @param clazz clazz.
     * @param <T> t.
     * @return parsed object.
     */
    public <T> T parseJson(@Nonnull final String documentJson, final Class<T> clazz) {
        return jsonMapper.readObject(documentJson, clazz);
    }

    /**
     * Возвращает адрес дома по unom.
     *
     * @param unom - unom Дома
     * @return адрес ОН
     */
    public String getAddressByUnom(String unom) {
        return ofNullable(getRealEstateByUnom(unom))
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse(null);
    }

    /**
     * Возвращает код ЦИП по unom.
     *
     * @param unom - unom Дома
     * @return код ЦИП
     */
    public String getInformationCenterCodeByUnom(String unom) {
        return realEstateDocumentDao.getInformationCenterCodeByUnom(unom);
    }

    /**
     * Возвращает район по unom.
     *
     * @param unom - unom Дома
     * @return район
     */
    public String getDistrictByUnom(String unom) {
        return realEstateDocumentDao.getDistrictByUnom(unom);
    }

    /**
     * Возвращает округ по unom.
     *
     * @param unom - unom Дома
     * @return округ
     */
    public String getPrefectByUnom(String unom) {
        return realEstateDocumentDao.getPrefectByUnom(unom);
    }

    /**
     * Получить объект комнаты по id.
     *
     * @param id - идентификатор комнаты
     * @return JSON объект комнаты
     */
    public RoomType fetchRoom(String id) {
        String roomString = roomDao.fetch(id);
        if (roomString != null) {
            return jsonMapper.readObject(roomString, RoomType.class);
        } else {
            return null;
        }
    }

    /**
     * Получить объект квартиры по id.
     *
     * @param id - идентификатор квартиры
     * @return объект квартиры
     */
    public FlatType getFlatByFlatId(String id) {
        String flatsString = flatDao.getFlatsByFlatId(
            "[{\"flatID\": \"" + id + "\"}]"
        );
        if (flatsString != null) {
            RealEstateDataType.Flats flats = jsonMapper.readObject(flatsString, RealEstateDataType.Flats.class);

            Optional<FlatType> optionalFlat = flats.getFlat()
                .stream().filter(flat -> id.equals(flat.getFlatID())).findAny();

            return optionalFlat.orElse(null);
        } else {
            return null;
        }
    }

    /**
     * Возвращает ОН по UNOM.
     *
     * @param unom - UNOM
     * @return Объект недвижимости (ОН)
     */
    public RealEstateDocument getRealEstateByUnom(String unom) {
        DocumentData documentData = realEstateDocumentDao.fetchByUnom(unom);
        if (documentData == null) {
            return null;
        }
        return parseDocumentData(documentData, RealEstateDocument.class);
    }

    /**
     * Получить адрес ЦИПа по ид.
     *
     * @param id ид ЦИПа
     * @return адрес
     */
    public String getCipAddressById(String id) {
        return cipDao.getCipAddressById(id);
    }

    /**
     * Получить фио жителей по идентификатору.
     *
     * @param personIds идентификаторы жителей
     * @return список фио
     */
    public String getPersonNames(final List<String> personIds) {
        final List<String> personDocuments = personDocumentDao.fetchFioByIds(personIds);
        return String.join("\n", personDocuments);
    }

}
