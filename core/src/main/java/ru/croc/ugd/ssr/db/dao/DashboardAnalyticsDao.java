package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import ru.reinform.cdp.db.model.DocumentData;

/**
 * DAO для работы с вьюхами в схеме analytics.
 */
public interface DashboardAnalyticsDao extends Repository<DocumentData, String> {

    /**
     * Получить сгруппированные по УНОМ и количеству семей из вьюхи v_equivalent_apartments_amount.
     * @return данные в формате "УНОМ дома = количество семей"
     */
    @Query(
        value = "SELECT SUM(affairIdCount) "
            + "FROM ("
            + "SELECT "
            + "unom, count(affairId) AS affairIdCount "
            + "FROM analytics.v_equivalent_apartments_amount "
            + "WHERE '3' = ANY(fileTypes) OR relocationStatus = ANY('{9,10,11,12,13,14,15}') "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getEquivalentApartmentsAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству personId данные из вьюхи v_equivalent_apartment_consented_amount.
     * @return данные в формате "УНОМ дома = Количество personId, у которых по квартире проставлено согласие"
     */
    @Query(
        value = "SELECT SUM(person) "
            + "FROM ( "
            + "SELECT tabl.unom, count(DISTINCT CONCAT(tabl.personid, tabl.affairId)) as person "
            + "FROM analytics.v_equivalent_apartment_consented_amount as tabl "
            + "WHERE eventLast IN ('1','3','9') "
            + "GROUP BY tabl.unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getEquivalentApartmentConsentedAmount();


    /**
     * Получить сгруппированные по УНОМ и количеству переехавших семей данные из вьюхи v_resettled_families_amount.
     * @return данные в формате "УНОМ дома = количество семей переехали"
     */
    @Query(
        value = "SELECT SUM(affairIdCount) "
            + "FROM ( "
            + "SELECT unom, count(affairId) AS affairIdCount "
            + "FROM analytics.v_resettled_families_amount "
            + "WHERE actDate IS NOT NULL "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getResettledFamiliesAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству людей из вьюхи v_persons_with_relocation_statuses.
     *
     * @return данные в формате "УНОМ дома = количество людей с нужным статусом"
     */
    @Query(
        value = "SELECT SUM(personIdCount) "
            + "FROM ( "
            + "SELECT unom, count(personId) AS personIdCount "
            + "FROM analytics.v_persons_with_relocation_statuses "
            + "WHERE relocationStatus = ANY('{2,3}') "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getEquivalentApartmentProposedAmount();


    /**
     * Получить сгруппированные по УНОМ и количеству семей данные из вьюхи v_families_amount.
     *
     * @return данные в формате "УНОМ дома = количество уникальных affairId в пределах УНОМ"
     */
    @Query(
        value = "SELECT SUM(affairIdCount) "
            + "FROM ( "
            + "SELECT unom, count(DISTINCT affairId) AS affairIdCount "
            + "FROM analytics.v_families_amount "
            + "WHERE type = 'TRADE' "
            + "OR (relocationstatus IS NOT NULL AND relocationstatus <> '0') "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getFamiliesAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству переехавших людей данные из вьюхи v_resettled_residents_amount.
     *
     * @return данные в формате "УНОМ дома = количество людей переехали".
     */
    @Query(
        value = "SELECT SUM(personIdCount) "
            + "FROM ( "
            + "SELECT unom, count(personId) AS personIdCount "
            + "FROM analytics.v_resettled_residents_amount "
            + "WHERE actDate IS NOT NULL "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getResettledResidentsAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_new_buildings_total_amount.
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT COUNT(DISTINCT capitalConstructionObjectId) "
            + "FROM analytics.v_new_buildings_total_amount",
        nativeQuery = true
    )
    Integer getNewBuildingsTotalAmount();

    /**
     * Получить сгруппированные по УНОМ дома и количеству квартир в нем данные из вьюхи v_flats_total_amount.
     *
     * @return данные в формате УНОМ дома = количество квартир.
     */
    @Query(
        value = "SELECT SUM(flatCount) "
            + "FROM ("
            + "SELECT "
            + "capitalConstructionObjectId, unom, startResettlementDate, dateCIP, count(flatNumber) as flatCount "
            + "FROM analytics.v_flats_total_amount "
            + "GROUP BY capitalConstructionObjectId, unom, startResettlementDate, dateCIP "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getFlatsTotalAmount();

    /**
     * Получить сгруппированные по УНОМ дома и количеству квартир в нем данные
     * из вьюхи v_resettlement_in_process_amount.
     *
     * @return данные в формате УНОМ дома = количество квартир.
     */
    @Query(
        value = "SELECT count(unom) "
            + "FROM ( "
            + "SELECT "
            + "unom, "
            + "COUNT(*) "
            + "FROM "
            + "analytics.v_resettlement_in_process_amount "
            + "WHERE "
            + "relocationStatus = ANY('{2,3,4,5,6,8}') "
            + "AND letterid IS NOT NULL "
            + "GROUP BY unom "
            + ") as foo",
        nativeQuery = true
    )
    Integer getResettlementInProcessAmount();

    /**
     * Получить данные по периоду планируемого завершения отселения, сгруппированные по УНОМ дома.
     *
     * @return данные в формате УНОМ дома - дата начала переселения - период планируемого завершения отселения.
     */
    @Query(
        value = "WITH end_dates AS ( "
            + "SELECT "
            + "unom, "
            + "startResettlementDate, "
            + "(startResettlementDate + interval '240' day) AS end_date "
            + "FROM "
            + "analytics.v_resettlement_in_process_amount "
            + "WHERE startResettlementDate is not null "
            + "ORDER BY startResettlementDate desc "
            + ") "
            + "SELECT "
            + "(CASE "
            + "WHEN EXTRACT(QUARTER FROM end_date) = 1 THEN 'I кв. ' || EXTRACT(YEAR FROM end_date) || ' года' "
            + "WHEN EXTRACT(QUARTER FROM end_date) = 2 THEN 'II кв. ' || EXTRACT(YEAR FROM end_date) || ' года' "
            + "WHEN EXTRACT(QUARTER FROM end_date) = 3 THEN 'III кв. ' || EXTRACT(YEAR FROM end_date) || ' года' "
            + "WHEN EXTRACT(QUARTER FROM end_date) = 4 THEN 'IV кв. ' || EXTRACT(YEAR FROM end_date) || ' года' "
            + "END) AS period "
            + "FROM end_dates "
            + "LIMIT 1",
        nativeQuery = true
    )
    String getPlannedResettlementCompletionTime();

    /**
     * Получить сгруппированные по УНОМ и количеству семей у которых отсутствует
     * РД (offerLetters.offerLetter.files.file.fileType = 3)
     * из вьюхи v_offered_equivalent_apartments_amount.
     *
     * @return данные в формате "УНОМ дома = количество семей у которых отсутствует РД".
     */
    @Query(
        value = "SELECT SUM(affairIdCount) "
            + "FROM ( "
            + "SELECT unom, count(affairId) AS affairIdCount "
            + "FROM analytics.v_offered_equivalent_apartments_amount "
            + "WHERE NOT '3' = ANY(fileTypes) AND relocationStatus = ANY('{2,3,4,5,6,8}') "
            + "GROUP BY unom "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getOfferedEquivalentApartmentsAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_all_buildings_amount.
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT COUNT(DISTINCT unom) "
            + "FROM analytics.v_all_buildings_amount",
        nativeQuery = true
    )
    Integer getAllBuildingsAmount();

    /**
     * Получить данные из вьюхи v_paperwork_completed_and_fund_issues_amount,
     * сгруппированные по УНОМ, общему количеству квартир в доме, количеству квартир с оформленным договором,
     * количеству квартир с неоформленным договором, количеству квартир с неподписанным договором по докупкам.
     *
     * @return данные в формате "УНОМ дома = Количество квартир в различных состояниях"
     */
    @Query(
        value = "WITH personsForTradeFilter AS ( "
            + "    SELECT affairId "
            + "    FROM analytics.v_paperwork_completed_and_fund_issues_amount "
            + "    WHERE type = 'PERSON' "
            + "        AND ( "
            + "            (offerLetterDate IS NOT NULL AND tradeType IN ('4', '5')) "
            + "            OR (applicationDate IS NOT NULL AND tradeType IN ('1', '2')) "
            + "        ) "
            + "        AND isTrade = '2' "
            + "), "
            + "filteredTrade AS ( "
            + "    SELECT * "
            + "    FROM analytics.v_paperwork_completed_and_fund_issues_amount "
            + "    WHERE type = 'PERSON' "
            + "        OR (type = 'TRADE' AND affairId IN (SELECT * FROM personsForTradeFilter)) "
            + ") "
            + "SELECT count(unom) "
            + "FROM ( "
            + "    SELECT "
            + "        unom, "
            + "        COUNT(DISTINCT affairId) FILTER ( "
            + "            WHERE type = 'PERSON' "
            + "            AND cast(relocationStatus as integer) >= 9 "
            + "            AND isTrade = '1' "
            + "        ) AS flatPersonDone, "
            + "        COUNT(DISTINCT affairId) FILTER ("
            + "            WHERE type = 'PERSON' "
            + "            AND cast(relocationStatus as integer) BETWEEN 2 AND 8 "
            + "            AND isTrade = '1' "
            + "        ) AS flatPersonProcess, "
            + "        COUNT (DISTINCT affairId) FILTER ( "
            + "            WHERE type = 'TRADE' "
            + "        ) AS flatTrade "
            + "    FROM filteredTrade"
            + "    GROUP BY unom "
            + ") AS FOO "
            + "WHERE flatTrade > 0 AND flatPersonProcess = 0",
        nativeQuery = true
    )
    Integer getPaperworkCompletedAndFundIssuesAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству квартир с типом сделки = 1 или 3
     * (Вид сделки - докупка + докупка в течении 2 лет), у которых заполнен contractSignedDate.
     *
     * @return
     */
    @Query(
        value = "SELECT SUM(flatCount) "
            + "FROM ( "
            + "SELECT "
            + "    unom, "
            + "    address, "
            + "    COUNT(DISTINCT concat(unom, affairId, flatNumber)) AS flatCount "
            + "FROM "
            + "    analytics.v_buy_in_apartments_amount "
            + "WHERE "
            + "    tradeType IN ('1', '2', '3') "
            + "    AND contractSignedDate IS NOT NULL "
            + "    AND claimStatus = '1' "
            + "GROUP BY unom, address "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getBuyInApartmentsAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству жителей с определенным типом сделки,
     * статусом заявления и предложением.
     * из вьюхи v_apartment_with_compensation_consented_amount.
     *
     * @return данные в формате "УНОМ дома = количество уникальных жителей".
     */
    @Query(
        value = "SELECT SUM(personCount) "
            + "FROM ( "
            + "SELECT "
            + "    unom,"
            + "    address, "
            + "    COUNT(DISTINCT personDocumentId) AS personCount "
            + "FROM "
            + "    analytics.v_apartment_with_compensation_consented_amount "
            + "WHERE "
            + "    claimStatus = '1' "
            + "    AND ( "
            + "        tradeType IN ('1', '2') "
            + "        OR tradeType IN ('4', '5') AND agreementDate IS NOT NULL "
            + "    ) "
            + "GROUP BY unom, address "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getApartmentWithCompensationAmount();

    /**
     * Получить сгруппированные по УНОМ и количеству квартир с типом сделки = 2, 4 или 5
     * (Вид сделки - докупка с компенсацией, компенсация и вне района), у которых заполнен contractSignedDate.
     *
     * @return
     */
    @Query(
        value = "SELECT SUM(flatCount) "
            + "FROM ( "
            + "SELECT "
            + "    unom, "
            + "    address, "
            + "    COUNT(DISTINCT concat(unom, affairId, flatNumber)) AS flatCount  "
            + "FROM "
            + "    analytics.v_buy_in_apartments_amount "
            + "WHERE "
            + "    tradeType IN ('4', '5') "
            + "    AND contractSignedDate IS NOT NULL "
            + "    AND claimStatus = '1' "
            + "GROUP BY unom, address "
            + ") AS foo",
        nativeQuery = true
    )
    Integer getApartmentsWithCompensationAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_v2 со статусом =
     * "Расселен, ведется изъятие нежилых помещений".
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(DISTINCT unom) "
            + "FROM analytics.v_resettlement_in_process_amount_v2 "
            + "WHERE resettlementStatus = '3'",
        nativeQuery = true
    )
    Integer getResidentsResettledAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_v2 со статусом =
     * "Ведется частичное переселение".
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(DISTINCT unom) "
            + "FROM analytics.v_resettlement_in_process_amount_v2 "
            + "WHERE resettlementStatus = '2' "
            + "AND resettlementBy = 'Part'",
        nativeQuery = true
    )
    Integer getPartResettlementInProcessAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_v2 со статусом =
     * "Отселены, готовятся к сносу".
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(DISTINCT unom) "
            + "FROM analytics.v_resettlement_in_process_amount_v2 "
            + "WHERE resettlementStatus = '4'",
        nativeQuery = true
    )
    Integer getPreparingForDemolitionAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_v2 со статусом =
     * "Снесены".
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(DISTINCT unom) "
            + "FROM analytics.v_resettlement_in_process_amount_v2 "
            + "WHERE resettlementStatus = '6'",
        nativeQuery = true
    )
    Integer getDemolishedAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_v2 со статусом =
     * "Расселен, сохраняемый дом".
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(DISTINCT unom) "
            + "FROM analytics.v_resettlement_in_process_amount_v2 "
            + "WHERE resettlementStatus = '5'",
        nativeQuery = true
    )
    Integer getPreservedAmount();

    /**
     * Получить количество жителей из сгруппированного запроса для показателя
     * "Фонд предлагает компенсацию (нет равнозначных)".
     *
     * @return количество жителей.
     */
    @Query(
        value = "SELECT SUM(personCount) "
            + "FROM ("
            + "     SELECT "
            + "         unom,"
            + "         address,"
            + "         count(personDocumentId) AS personCount  "
            + "     FROM "
            + "         analytics.v_apartment_with_compensation_consented_amount "
            + "     WHERE "
            + "         offerLetterDate IS NOT NULL "
            + "         AND tradeType IN ('4',  '5') "
            + "         AND agreementDate IS NULL "
            + "         AND contractSignedDate IS NULL "
            + "         AND letterId IS NULL "
            + "         AND contractExists = false "
            + "     GROUP BY "
            + "         unom, address"
            + ") AS foo",
        nativeQuery = true
    )
    Integer getApartmentWithCompensationProposedAmount();

    /**
     * Получить количество жителей из сгруппированного запроса для показателя
     * "Отказы, суды".
     *
     * @return количество жителей.
     */
    @Query(
        value = "SELECT SUM(personIdCount) "
            + "FROM ("
            + "     SELECT "
            + "         unom, "
            + "         address, "
            + "         count(documentId) AS personIdCount "
            + "     FROM "
            + "         analytics.v_equivalent_apartment_consented_amount "
            + "     WHERE "
            + "         (eventLast = '2' AND tradeType IS NULL) "
            + "         OR inCourt = '1' "
            + "         OR ((claimStatus BETWEEN '2' AND '8') AND tradeType IN ('4', '5') "
            + "             AND relocationStatus NOT BETWEEN '2' AND '15') "
            + "     GROUP BY "
            + "         unom, address"
            + ") AS foo",
        nativeQuery = true
    )
    Integer getRefusedAmount();

    /**
     * Получить количество квартир из сгруппированного запроса для показателя
     * "Ведется предложение квартир с компенсацией за меньшую жилую площадь".
     *
     * @return количество жителей.
     */
    @Query(
        value = "SELECT SUM(flatCount) "
            + "FROM ("
            + "     SELECT "
            + "         unom, "
            + "         address, "
            + "         COUNT(DISTINCT CONCAT(unom, affairId, flatNumber)) AS flatCount "
            + "     FROM "
            + "         analytics.v_buy_in_apartments_amount "
            + "     WHERE "
            + "         claimStatus = '1' "
            + "         AND offerLetterDate IS NOT NULL "
            + "         AND tradeType IN ('4', '5') "
            + "         AND contractSignedDate IS NULL "
            + "     GROUP BY "
            + "         unom, address"
            + ") AS foo",
        nativeQuery = true
    )
    Integer getOfferedApartmentsWithCompensationAmount();

    /**
     * Получить количество уникальных УНОМ из вьюхи v_resettlement_in_process_amount_with_trades по условиям.
     *
     * @return количество УНОМ.
     */
    @Query(
        value = "SELECT count(unom) FROM ( "
            + "     SELECT "
            + "         unom, "
            + "         COUNT (DISTINCT personId) AS allPersonsCount, "
            + "         COUNT (DISTINCT affairId) AS allAffairsCount, "
            + "         COUNT (DISTINCT personId) FILTER ( "
            + "             WHERE tradeType = '5' "
            + "             AND agreementDate IS NOT NULL "
            + "             AND contractSignedDate IS NULL "
            + "         ) AS filteredPersonsCount, "
            + "         COUNT (DISTINCT affairId) FILTER ( "
            + "             WHERE relocationStatus BETWEEN '2' AND '8' "
            + "         ) AS filteredAffairsCount "
            + "     FROM "
            + "         analytics.v_resettlement_in_process_amount_with_trades "
            + "     WHERE "
            + "         demolitionDate IS NULL "
            + "         AND resettlementBy = 'full' "
            + "         AND hasNonResidentialSpaces = true "
            + "         AND withdrawnNonResidentialSpaces = false "
            + "     GROUP BY unom "
            + ") AS grouped_unom "
            + "WHERE "
            + "     allPersonsCount = filteredPersonsCount "
            + "     AND filteredAffairsCount = 0",
        nativeQuery = true
    )
    Integer getOutsideResidenceResettlementAmount();
}
