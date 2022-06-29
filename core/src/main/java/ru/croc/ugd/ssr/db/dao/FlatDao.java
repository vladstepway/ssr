package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * DAO по работе с квартирами.
 */
public interface FlatDao extends Repository<DocumentData, String> {

    /**
     * Получить FlatType по ЕНО (IntegrationNumber).
     *
     * @param eno IntegrationNumber
     * @return список квартир
     */
    @Query(
        value = "select json_data ->> 'flatID' "
            + "from (select jsonb_array_elements(json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') "
            + "as json_data from documents where doc_type = 'REAL-ESTATE') as flats "
            + "where json_data ->> 'IntegrationNumber' = :eno",
        nativeQuery = true
    )
    List<String> fetchFlatsByEno(@Nonnull @Param("eno") String eno);

    /**
     * Получить Квартиры ОН по flatID.
     *
     * @param jsonNode нода с квартирой
     * @return квартиры ОН
     */
    @Query(
        value = "select json_data -> 'RealEstate' -> 'RealEstateData' ->> 'Flats' "
            + "from documents "
            + "where json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat' @> cast(:jsonNode as jsonb) "
            + "and doc_type = 'REAL-ESTATE'",
        nativeQuery = true
    )
    String getFlatsByFlatId(@Nonnull @Param("jsonNode") String jsonNode);

    /**
     * Получить информацию по номеру квартиры по УНОМу дома.
     *
     * @param unom UNOM
     * @return список квартир
     */
    @Query(
        value = "select json_data ->> 'apartment_L4_VALUE' "
            + "from (select jsonb_array_elements(json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') "
            + "as json_data from documents where doc_type = 'REAL-ESTATE' "
            + "and json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom) as flats ",
        nativeQuery = true
    )
    List<String> getFlatsNumInfoByUnom(@Nonnull @Param("unom") String unom);


    @Query(value = "select flats"
            + "    from documents,"
            + " jsonb_array_elements(json_data -> 'RealEstate' -> 'RealEstateData' -> 'Flats' -> 'Flat') as flats"
            + "    where doc_type = 'REAL-ESTATE'"
            + "     AND json_data -> 'RealEstate' -> 'RealEstateData' ->> 'UNOM' = :unom "
            + "     AND coalesce (flats ->> 'apartment_L4_VALUE', flats ->> 'FlatNumber') = :flatNumber"
            + " LIMIT 1",
        nativeQuery = true
    )
    Optional<String> fetchFlatByUnomAndFlatNumber(
        @Param("flatNumber") String flatNumber,
        @Param("unom") String unom
    );

}
