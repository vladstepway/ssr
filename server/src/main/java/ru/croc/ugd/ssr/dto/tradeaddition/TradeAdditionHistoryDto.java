package ru.croc.ugd.ssr.dto.tradeaddition;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class TradeAdditionHistoryDto {

    private final String uploadedFileId;
    private final String uniqueRecordKey;
    private final String tradeAdditionDocumentId;
    private final String sellId;
    private final LocalDateTime updateDateTime;
    private final String pageName;
    private final String recordNumber;
    private final String comment;
    private final String changes;
    private final String mfrFlowFileStoreId;
}
