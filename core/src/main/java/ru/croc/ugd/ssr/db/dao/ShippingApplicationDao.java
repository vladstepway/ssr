package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Имплементация.
 */
public interface ShippingApplicationDao extends Repository<DocumentData, String> {

    /**
     * Ищем заявления, по которым истёк срок переезда, но не начат БП фиксации результатов переезда.
     * @return список заявлений, соответствующих параметрам запроса.
     */
    @Query(value = "select * from documents "
        + " where doc_type='SHIPPING-APPLICATION'"
        + " and (json_data -> 'shippingApplication' -> 'shippingApplicationData'"
        + " ->> 'shippingDateEnd')::::timestamptz < now()"
        + " and (json_data -> 'shippingApplication' -> 'shippingApplicationData'"
        + " ->> 'processInstanceId') is null",
        nativeQuery = true)
    List<DocumentData> findApplicationsForBusinessProcessStart();

    /**
     * Finds application by eno.
     * @param eno eno
     * @return application
     */
    @Query(value = "select * from documents "
        + " where doc_type='SHIPPING-APPLICATION'"
        + " and json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'eno' = :eno",
        nativeQuery = true)
    Optional<DocumentData> findByEno(@Param("eno") final String eno);

    /**
     * Finds applications by shippingDateStart.
     * @param startsAt startsAt
     * @return application
     */
    @Query(value = "select * from documents "
        + " where doc_type='SHIPPING-APPLICATION'"
        + " and CAST (json_data -> 'shippingApplication' -> 'shippingApplicationData'"
        + " ->> 'shippingDateStart' as TIMESTAMP) = :startsAt "
        + " and json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'declineDateTime' IS NULL",
        nativeQuery = true)
    List<DocumentData> findActualApplicationsByStartDate(@Param("startsAt") LocalDateTime startsAt);

    /**
     * Ищем заявления по personUid заявителя.
     *
     * @param personUid personUid
     *
     * @return список заявлений, соответствующих параметрам запроса.
     */
    @Query(value = "SELECT * FROM documents "
        + "WHERE doc_type='SHIPPING-APPLICATION' "
        + "AND json_data -> 'shippingApplication' -> 'shippingApplicationData'"
        + " -> 'applicant' ->> 'personUid' = :personUid",
        nativeQuery = true)
    List<DocumentData> findApplicationsByPersonUid(@Param("personUid") final String personUid);

    /**
     * Returns all booking ids.
     * @return list of booking ids
     */
    @Query(value = "SELECT distinct(json_data -> 'shippingApplication' -> 'shippingApplicationData' ->> 'bookingId') "
        + "FROM ssr.documents "
        + "WHERE doc_type = 'SHIPPING-APPLICATION'",
        nativeQuery = true)
    List<String> findAllBookingIds();

    /**
     * Ищем заявления по personUid заявителя и UNOM.
     *
     * @param personUid personUid
     * @param unomTo UNOM заселяемого дома
     * @param unomFrom UNOM отселяемого дома
     * @return заявление
     */
    @Query(value = "select "
            + "    * "
            + "from "
            + "    ssr.documents "
            + "where "
            + "    doc_type = 'SHIPPING-APPLICATION' "
            + "    and json_data @> cast('{"
            + "        \"shippingApplication\": {"
            + "            \"shippingApplicationData\": {"
            + "                \"applicant\": {"
            + "                    \"personUid\": \"' || :personUid || '\""
            + "                }, "
            + "                \"apartmentFrom\": {"
            + "                    \"unom\": \"' || :unomFrom || '\""
            + "                },"
            + "                \"apartmentTo\": {"
            + "                    \"unom\": \"' || :unomTo || '\""
            + "                }"
            + "            }"
            + "        }"
            + "    }' as jsonb) "
            + "limit 1 ",
            nativeQuery = true)
    Optional<DocumentData> findShippingApplicationByPersonUidAndUnom(
            @Nonnull @Param("personUid") String personUid,
            @Nonnull @Param("unomTo") String unomTo,
            @Nonnull @Param("unomFrom") String unomFrom
    );

    /**
     * Проверяет, существует ли уже такой bookingId.
     *
     * @param bookingId bookingId.
     * @param status Статус.
     * @return boolean result
     */
    @Query(value = "select exists"
        + "  (select 1"
        + "     from documents"
        + "    where doc_type = 'SHIPPING-APPLICATION'"
        + "      and json_data -> 'shippingApplication' ->"
        + "          'shippingApplicationData' ->> 'bookingId' = :bookingId"
        + "      and json_data -> 'shippingApplication' ->"
        + "          'shippingApplicationData' ->> 'status' = :status"
        + "  )",
        nativeQuery = true)
    boolean existsByBookingIdAndStatus(
        @Param("bookingId") final String bookingId,
        @Param("status") final String status
    );
}
