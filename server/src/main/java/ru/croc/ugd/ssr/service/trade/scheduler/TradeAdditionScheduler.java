package ru.croc.ugd.ssr.service.trade.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.service.flows.RemovableStatusUpdateService;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionValueDecoder;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;

import java.util.ArrayList;

@Slf4j
@Component
@AllArgsConstructor
public class TradeAdditionScheduler {

    private final RemovableStatusUpdateService statusFlatUpdateService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionValueDecoder tradeAdditionValueDecoder;

    @Scheduled(cron = "${schedulers.trade-addition-update-flat-status.cron:0 0 0 * * ?}")
    public void updateFlatStatus() {
        tradeAdditionDocumentService
            .getAllDocumentsForStatusUpdateProcessing()
            .forEach(this::updateFlatStatus);
    }

    private void updateFlatStatus(final TradeAdditionDocument document) {
        final TradeAdditionType tradeAddition = document
            .getDocument()
            .getTradeAdditionTypeData();

        tradeAddition.getNewEstates()
            .forEach(this::updateFlatStatus);
        tradeAddition.setRemovalStatusUpdated(true);

        tradeAdditionDocumentService.updateDocument(document.getId(), document, true, true, "updateFlatStatus");
    }

    private void updateFlatStatus(final EstateInfoType estateInfoType) {
        final String flatNumber = estateInfoType.getFlatNumber();
        final String unom = estateInfoType.getUnom();
        final String cadNum = tradeAdditionValueDecoder.extractCadNumber(estateInfoType);
        statusFlatUpdateService.updateRemovalStatus(flatNumber, new ArrayList<>(), unom, cadNum);
    }
}
