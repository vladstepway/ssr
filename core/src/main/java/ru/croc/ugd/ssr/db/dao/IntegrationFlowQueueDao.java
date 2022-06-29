package ru.croc.ugd.ssr.db.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.reinform.cdp.db.model.DocumentData;

import java.time.LocalDateTime;
import java.util.List;

public interface IntegrationFlowQueueDao extends Repository<DocumentData, String> {

    @Query(value = "select * "
        + "  from documents "
        + " where doc_type = 'INTEGRATION-FLOW-QUEUE' "
        + "   and json_data -> 'integrationFlowQueue' -> 'integrationFlowQueueData' ->> 'eno' in :enoList",
        nativeQuery = true)
    List<DocumentData> fetchByEnoList(@Param("enoList") final List<String> enoList);

    @Query(value = "select * "
        + "  from documents "
        + " where doc_type = 'INTEGRATION-FLOW-QUEUE' "
        + "   and (:flowType IS NULL "
        + "    or json_data -> 'integrationFlowQueue' -> 'integrationFlowQueueData'"
        + "         ->> 'flowType' = cast(:flowType as text))"
        + "   and (json_data -> 'integrationFlowQueue' -> 'integrationFlowQueueData'"
        + "     ->> 'processingPostponed' = 'false'"
        + "   or cast(json_data -> 'integrationFlowQueue' -> 'integrationFlowQueueData'"
        + "     ->> 'createdAt' as timestamp) < cast(cast(:createdBefore as text) as timestamp))",
        nativeQuery = true)
    List<DocumentData> fetchNotPostponedByFlowTypeAndCreatedBefore(
        @Param("flowType") final String flowType,
        @Param("createdBefore") final LocalDateTime createdBefore
    );
}
