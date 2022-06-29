package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.util.List;

public interface IntegrationFlowDao extends Repository<DocumentData, String> {

    @Query(value = "select * "
        + "  from documents "
        + " where doc_type = 'INTEGRATION-FLOW' "
        + "   and json_data -> 'integrationFlow' -> 'integrationFlowData' ->> 'eno' = cast(:eno as text)",
        nativeQuery = true)
    List<DocumentData> fetchByEno(@Param("eno") String eno);

    @Query(value = "select exists (select 1 from documents "
        + " where doc_type = 'INTEGRATION-FLOW' "
        + "   and json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'flowType' = cast(:flowType as text) "
        + "   and (:affairId is null "
        + "        or json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'affairId' = cast(:affairId as text)) "
        + "   and (:contractOrderId is null "
        + "        or json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'contractOrderId' = cast(:contractOrderId as text)) "
        + "   and (:contractStatus is null "
        + "        or json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'contractStatus' = cast(:contractStatus as text)) "
        + "   and (:contractProjectStatus is null "
        + "        or json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'contractProjectStatus' = cast(:contractProjectStatus as text))"
        + "   and (:letterId is null "
        + "        or json_data -> 'integrationFlow' -> 'integrationFlowData' "
        + "             -> 'mfrFlowData' ->> 'letterId' = cast(:letterId as text)))",
        nativeQuery = true)
    boolean existsSentMfrFlow(
        @Param("flowType") final String flowType,
        @Param("affairId") final String affairId,
        @Param("contractOrderId") final String contractOrderId,
        @Param("contractStatus") final String contractStatus,
        @Param("contractProjectStatus") final String contractProjectStatus,
        @Param("letterId") final String letterId
    );
}
