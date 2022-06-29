package ru.croc.ugd.ssr.service;

import static java.util.Optional.ofNullable;

import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.db.dao.FlatDao;
import ru.croc.ugd.ssr.db.dao.RealEstateDocumentDao;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.flat.AffairDto;
import ru.croc.ugd.ssr.dto.flat.FlatInfoDto;
import ru.croc.ugd.ssr.dto.flat.RestFlatDto;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * Сервис для работы с квартирой.
 */
@Service
public class FlatService {

    private final RealEstateDocumentService realEstateDocumentService;
    private final RealEstateDocumentDao realEstateDocumentDao;
    private final FlatDao flatDao;
    private final CapitalConstructionObjectService ccoService;
    private final JsonMapper jsonMapper;
    private final PersonDocumentService personDocumentService;

    public FlatService(
        RealEstateDocumentService realEstateDocumentService,
        RealEstateDocumentDao realEstateDocumentDao,
        FlatDao flatDao,
        CapitalConstructionObjectService ccoService,
        JsonMapper jsonMapper,
        @Lazy PersonDocumentService personDocumentService,
        final TradeAdditionDocumentService tradeAdditionDocumentService
    ) {
        this.realEstateDocumentService = realEstateDocumentService;
        this.realEstateDocumentDao = realEstateDocumentDao;
        this.flatDao = flatDao;
        this.ccoService = ccoService;
        this.jsonMapper = jsonMapper;
        this.personDocumentService = personDocumentService;
    }


    /**
     * Получить объект квартиры по id.
     *
     * @param id - идентификатор квартиры
     * @return объект квартиры
     */
    public FlatType fetchFlat(String id) {
        return ofNullable(fetchRealEstateAndFlat(id))
            .map(RealEstateDataAndFlatInfoDto::getFlat)
            .orElse(null);
    }

    public Optional<FlatType> fetchFlatByUnomAndFlatNumber(
        final String flatNumber,
        final String unom
    ) {
        return flatDao.fetchFlatByUnomAndFlatNumber(flatNumber, unom)
            .map(this::parseDocumentJson);
    }

    /**
     * Получить объект квартиры по id.
     *
     * @param id - идентификатор квартиры
     * @return объект квартиры
     */
    public RealEstateDataAndFlatInfoDto fetchRealEstateAndFlat(String id) {
        RealEstateDocument realEstateDocument = null;
        List<String> ids = realEstateDocumentDao.findRealEstateIdsByFlatId(
            "[{\"flatID\": \"" + id + "\"}]"
        );
        if (ids.size() > 0) {
            realEstateDocument = realEstateDocumentService.fetchDocument(ids.get(0));
        }
        if (realEstateDocument == null) {
            return null;
        }

        RealEstateDataType realEstateData = realEstateDocument
            .getDocument()
            .getRealEstateData();

        return ofNullable(realEstateData.getFlats())
            .map(RealEstateDataType.Flats::getFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(flat -> id.equals(flat.getFlatID()))
            .findFirst()
            .map(flat -> RealEstateDataAndFlatInfoDto
                .builder()
                .realEstateData(realEstateData)
                .flat(flat)
                .build())
            .orElse(null);
    }

    /**
     * Loads address from CCO per each new flat.
     *
     * @param person person
     */
    public void loadCcoAddressForNewFlats(final PersonType person) {
        ofNullable(person)
            .map(PersonType::getNewFlatInfo)
            .map(PersonType.NewFlatInfo::getNewFlat)
            .ifPresent(newFlats -> newFlats.stream()
                .filter(newFlat -> Objects.nonNull(newFlat.getCcoUnom()) && Objects.isNull(newFlat.getCcoAddress()))
                .forEach(newFlat -> newFlat.setCcoAddress(
                    ccoService.getCcoAddressByUnom(newFlat.getCcoUnom().toString())
                ))
            );
    }

    /**
     * Получить постранично квартиры ОН для расселения.
     * ВНИМАНИЕ! Сделано Java-кодом, а не SQL, так как номера квартир могут выглядеть так: 57а, 18б, 207/209 и т.п.
     * Но при этом сортировать их надо как числа -> SQL здесь не годится.
     *
     * @param pageNum       номер страницы
     * @param pageSize      размер страницы
     * @param unom          UNOM дома
     * @param roomAmount    количество комнат в квартире
     * @param flatNumber    номер квартиры
     * @param floor         этаж
     * @param orderByString строка сортировки
     * @return json строка со списком квартир
     */
    public String fetchFlatPageByUnom(int pageNum,
                                      int pageSize,
                                      String unom,
                                      String roomAmount,
                                      String flatNumber,
                                      Integer floor,
                                      String orderByString) {
        JSONObject result = new JSONObject();
        RealEstateDocument realEstateDocument = realEstateDocumentService.fetchDocumentByUnom(unom);
        if (realEstateDocument != null) {
            RealEstateDataType.Flats flatsObject = realEstateDocument.getDocument().getRealEstateData().getFlats();
            List<FlatInfoDto> data = new ArrayList<>();
            long count = 0;
            if (flatsObject != null) {
                List<FlatType> allFlats = flatsObject.getFlat();
                if (roomAmount != null) {
                    List<String> roomAmounts = Arrays.asList(roomAmount.split(","));
                    allFlats = allFlats
                        .stream()
                        .filter(flat -> flat.getRoomsCount() != null)
                        .filter(flat -> roomAmounts.contains(flat.getRoomsCount().toString()))
                        .collect(Collectors.toList());
                }
                if (flatNumber != null) {
                    allFlats = allFlats
                        .stream()
                        .filter(flat -> flat.getApartmentL4VALUE() != null)
                        .filter(flat -> flatNumber.equals(flat.getApartmentL4VALUE()))
                        .collect(Collectors.toList());
                }
                if (floor != null) {
                    allFlats = allFlats
                        .stream()
                        .filter(flat -> flat.getFloor() != null)
                        .filter(flat -> floor.equals(flat.getFloor()))
                        .collect(Collectors.toList());
                }
                if (checkSortNeed(orderByString)) {
                    allFlats = allFlats
                        .stream()
                        .sorted(Objects.requireNonNull(getFlatComparator(orderByString)))
                        .collect(Collectors.toList());
                }

                final List<RestFlatDto> restFlatDtos = personDocumentService.fetchFlatsWithLivers(unom, false);

                data = allFlats
                    .stream()
                    .map(flat -> retrieveFlatWithTenants(flat, restFlatDtos))
                    .skip(pageNum * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());

                count = allFlats.size();
            }
            result.put("data", data);
            JSONObject hints = new JSONObject();
            hints.put("total", count);
            result.put("hints", hints);
        }
        return result.toString();
    }

    private FlatInfoDto retrieveFlatWithTenants(final FlatType flat, final List<RestFlatDto> restFlatDtos) {
        final List<AffairDto> affairs = restFlatDtos.stream()
            .filter(flatDto -> flat.getFlatID().equals(flatDto.getFlatId()))
            .map(flatDto -> AffairDto.builder()
                .livers(flatDto.getLivers())
                .affairId(flatDto.getAffairId())
                .roomNum(flatDto.getRoomNum())
                .build()
            )
            .collect(Collectors.toList());

        return FlatInfoDto.builder()
            .flat(flat)
            .affairs(affairs)
            .build();
    }

    /**
     * Получить ид квартиры по ЕНО.
     *
     * @param serviceNumber ено
     * @return ид квартиры
     */
    public String getFlatIdByEno(String serviceNumber) {
        List<String> flats = flatDao.fetchFlatsByEno(serviceNumber);
        if (flats.size() > 0) {
            return flats.get(0);
        }
        return null;
    }

    /**
     * Проставляет ОН квартир код информационного центра по идентификаторам.
     *
     * @param flatIds                список идентификаторов квартир
     * @param informationServiceCode код информационного центра
     */
    public void updateInformationServiceCodeOfRealEstates(List<String> flatIds, String informationServiceCode) {
        if (informationServiceCode == null || informationServiceCode.isEmpty()) {
            return;
        }
        for (String flatId : flatIds) {
            RealEstateDocument realEstateDocument = realEstateDocumentService.fetchByFlatId(flatId);
            RealEstateDataType realEstateData = realEstateDocument.getDocument().getRealEstateData();
            realEstateData.setInformationCenterCode(informationServiceCode);
            realEstateDocumentService.updateDocument(
                realEstateDocument.getId(), realEstateDocument, true, true, "set informationServiceCode"
            );
        }
    }

    /**
     * Метод определяет, нужна ли сортировка в метде постраничного получения квартир.
     *
     * @param orderBy строка сортировки
     * @return true/false
     */
    private boolean checkSortNeed(String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return false;
        }
        String[] strings = orderBy.split(" ");
        String field = strings[0];
        // UNOM и адрес дома у всех квартир одинаковый, сортировка не имеет смысла
        return !field.equals("unom") && !field.equals("address");
    }

    /**
     * Метод возвращает компаратор по стркое соритровки для метода постраничного получения квартир.
     *
     * @param orderBy строка сортировки
     * @return Компаратор
     */
    private Comparator<FlatType> getFlatComparator(String orderBy) {
        String[] strings = orderBy.split(" ");
        String field = strings[0];
        String direction = strings.length > 1 ? strings[1] : "asc";
        if (field.equals("roomAmount")) {
            if (direction.equals("asc")) {
                return Comparator.comparingInt(a -> getSafeIntFromBigInteger(a.getRoomsCount()));
            } else {
                return (first, second) -> getSafeIntFromBigInteger(
                    second.getRoomsCount()) - getSafeIntFromBigInteger(first.getRoomsCount()
                );
            }
        }
        if (field.equals("flatNumber")) {
            if (direction.equals("asc")) {
                return Comparator.comparingInt(a -> getIntValueFromFlatNumber(a.getApartmentL4VALUE()));
            } else {
                return (first, second) -> getIntValueFromFlatNumber(second.getApartmentL4VALUE())
                    - getIntValueFromFlatNumber(first.getApartmentL4VALUE());
            }
        }
        if (field.equals("floor")) {
            if (direction.equals("asc")) {
                return Comparator.comparingInt(a -> getSafeIntFromInteger(a.getFloor()));
            } else {
                return (
                    first, second) ->
                    getSafeIntFromInteger(second.getFloor()) - getSafeIntFromInteger(first.getFloor()
                    );
            }
        }
        return null;
    }

    private int getSafeIntFromBigInteger(BigInteger value) {
        return value != null ? value.intValue() : 0;
    }

    private int getSafeIntFromInteger(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * Метод для получения числового представления номера квартиры (для сортировки).
     *
     * @param flatNumber номер квартиры (м.б. 27а, 23б, 207/209 и т.п.)
     * @return числовое представление
     */
    private int getIntValueFromFlatNumber(String flatNumber) {
        if (flatNumber == null || flatNumber.isEmpty()) {
            return 0;
        }
        if (flatNumber.contains("/")) {
            flatNumber = flatNumber.substring(0, flatNumber.indexOf("/"));
        }
        String units = flatNumber.replace("\\w", "");
        int multiplier = 10000;

        int unitsSum = 0;
        for (int i = 0; i < units.length(); i++) {
            unitsSum += units.charAt(i);
        }
        return Integer.parseInt(flatNumber) * multiplier + unitsSum;
    }

    /**
     * Получить информацию по номеру квартиры по УНОМу дома.
     *
     * @param unom UNOM
     * @return список квартир
     */
    public List<String> getFlatsNumInfoByUnom(String unom) {
        return flatDao.getFlatsNumInfoByUnom(unom);
    }

    private FlatType parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, FlatType.class);
    }
}
