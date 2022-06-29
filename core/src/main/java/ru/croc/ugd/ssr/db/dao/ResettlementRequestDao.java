package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;
import javax.annotation.Nonnull;

public interface ResettlementRequestDao extends Repository<DocumentData, String> {

    @Query(value = "select * from documents "
        + "where json_data "
        + "@> cast('{\"ResettlementRequest\":{\"main\":{\"housesToSettle\":"
        + "[{\"housesToResettle\":[{\"realEstateUnom\":\"'|| :unom ||'\"}]}]}}}' as jsonb) "
        + "and doc_type = 'RESETTLEMENT-REQUEST' and json_data -> 'ResettlementRequest'"
        + " -> 'main' ->> 'status' != 'Отклонена'", nativeQuery = true)
    List<DocumentData> fetchResettlementRequestByUnom(@Nonnull @Param("unom") String unom);

    @Query(value = "select * from documents "
        + "where json_data "
        + "@> cast('{\"ResettlementRequest\":{\"main\":{\"housesToSettle\":"
        + "[{\"informationCenterCode\":\"'|| :cipId ||'\"}]}}}' as jsonb) "
        + "and doc_type = 'RESETTLEMENT-REQUEST' and json_data -> 'ResettlementRequest'"
        + " -> 'main' ->> 'status' = 'Завершена'", nativeQuery = true)
    List<DocumentData> fetchResettlementRequestByCipId(@Nonnull @Param("cipId") String cipId);

}
