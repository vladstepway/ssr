package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.Optional;

/**
 * DAO для разборов письма с предложением.
 */
public interface OfferLetterParsingDao extends Repository<DocumentData, String> {

    @Query(value = "select exists (select 1 "
        + "  from documents "
        + " where doc_type = 'OFFER-LETTER-PARSING'"
        + "   and json_data -> 'offerLetterParsing' -> 'offerLetterParsingData' "
        + "      ->> 'affairId' = :affairId"
        + "   and json_data -> 'offerLetterParsing' -> 'offerLetterParsingData' "
        + "      ->> 'letterId' = :letterId)",
        nativeQuery = true)
    boolean existsByLetterIdAndAffairId(
        @Param("letterId") final String letterId, @Param("affairId") final String affairId
    );

    @Query(value = "select * "
        + "  from documents "
        + " where doc_type = 'OFFER-LETTER-PARSING'"
        + "   and json_data -> 'offerLetterParsing' -> 'offerLetterParsingData' "
        + "      ->> 'affairId' = :affairId"
        + "   and json_data -> 'offerLetterParsing' -> 'offerLetterParsingData' "
        + "      ->> 'letterId' = :letterId",
        nativeQuery = true)
    Optional<DocumentData> fetchByLetterIdAndAffairId(
        @Param("letterId") final String letterId, @Param("affairId") final String affairId
    );
}
