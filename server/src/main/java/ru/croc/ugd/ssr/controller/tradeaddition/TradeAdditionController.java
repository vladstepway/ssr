package ru.croc.ugd.ssr.controller.tradeaddition;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionConfirmTradesDto;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionDto;
import ru.croc.ugd.ssr.dto.tradeaddition.TradeAdditionHistoryDto;
import ru.croc.ugd.ssr.generated.api.TradeAdditionApi;
import ru.croc.ugd.ssr.generated.dto.RestTradeAdditionBatchesDeployedResponseDto;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionHistoryDocument;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionHistoryDocumentService;
import ru.croc.ugd.ssr.service.document.TradeApplicationFileDocumentService;
import ru.croc.ugd.ssr.service.trade.batch.TradeAdditionBatchService;
import ru.croc.ugd.ssr.service.trade.history.TradeAdditionHistoryService;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Контроллер TradeAdditionController.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class TradeAdditionController implements TradeAdditionApi {

    private final TradeAdditionBatchService tradeAdditionBatchService;
    private final TradeAdditionHistoryDocumentService tradeAdditionHistoryDocumentService;
    private final TradeApplicationFileDocumentService tradeApplicationFileDocumentService;
    private final JsonMapper jsonMapper;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionHistoryService tradeAdditionHistoryService;

    @Override
    public ResponseEntity<Void> tradeAdditionProcess(@NotNull @Valid String uploadedFileId,
                                                     @NotNull @Valid String documentId) {
        tradeAdditionBatchService.processTradeAdditionSheetFile(uploadedFileId, documentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestTradeAdditionBatchesDeployedResponseDto> tradeAdditionAreAllBatchesDeployed() {
        final Boolean areAllBatchesDeployed = tradeAdditionBatchService.areAllBatchesDeployed();
        RestTradeAdditionBatchesDeployedResponseDto responseDto = new RestTradeAdditionBatchesDeployedResponseDto();
        responseDto.deployed(areAllBatchesDeployed);
        return ResponseEntity.ok(responseDto);
    }

    @Override
    public ResponseEntity<Void> tradeAdditionDeployBatch(@NotNull @Valid String documentId) {
        tradeAdditionBatchService.deployBatch(documentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Получить всю историю документа.
     *
     * @param uniqueKey Ид заявки
     * @return карточка
     */
    @RequestMapping(
        path = {"/tradeaddition/history/{uniqueKey}"},
        method = {RequestMethod.GET},
        produces = {"application/json;charset=UTF-8"}
    )
    public String fetchById(
        @PathVariable("uniqueKey") final String uniqueKey
    ) {
        List<TradeAdditionHistoryDocument> createdDocuments = tradeAdditionHistoryDocumentService
            .findIndexedHistoryByUniqueKey(uniqueKey);
        return jsonMapper.writeObject(createdDocuments);
    }

    @RequestMapping(
        path = {"/tradeaddition/applicationfile/{batchDocumentId}/notLinked"},
        method = {RequestMethod.GET},
        produces = {"application/json;charset=UTF-8"}
    )
    public List<String> getNotLinkedTradeApplicationFilesForBatch(
        @PathVariable("batchDocumentId") final String batchDocumentId
    ) {
        return tradeApplicationFileDocumentService.getNotLinkedTradeApplicationFilesForBatch(batchDocumentId);
    }

    @ApiOperation(value = "Получение списка trade addition по batch ID, странице и размеру страницы.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "TradeAdditionResponse", response = List.class),
        @ApiResponse(code = 404, message = "Document not found"),
        @ApiResponse(code = 405, message = "Invalid input")})
    @GetMapping(value = "/tradeaddition/fetch")
    public List<TradeAdditionDocument> fetchPaged(
        @ApiParam(value = "batchDocumentId", required = true)
        @RequestParam final String batchDocumentId,
        @ApiParam(value = "номер страницы", required = true)
        @RequestParam final int pageNum,
        @ApiParam(value = "размер страницы", required = true)
        @RequestParam final int pageSize
    ) {
        return tradeAdditionBatchService.fetchTradeAdditions(batchDocumentId, pageNum, pageSize);
    }

    @ApiOperation(value = "Подсчет кол-ва trade addition по batch ID.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "TradeAdditionResponse", response = List.class),
        @ApiResponse(code = 404, message = "Document not found"),
        @ApiResponse(code = 405, message = "Invalid input")})
    @GetMapping(value = "/tradeaddition/count")
    public long count(
        @ApiParam(value = "batchDocumentId", required = true)
        @RequestParam final String batchDocumentId
    ) {
        return tradeAdditionBatchService.countTradeAdditions(batchDocumentId);
    }

    @PostMapping("/tradeaddition/batch/{batchDocumentId}/confirm-trades")
    @ResponseStatus(HttpStatus.OK)
    public void confirmTrades(
        @PathVariable("batchDocumentId") final String batchDocumentId,
        @RequestBody final TradeAdditionConfirmTradesDto confirmTradesDto
    ) {
        tradeAdditionBatchService.confirmTrades(batchDocumentId, confirmTradesDto);
    }

    @GetMapping("/tradeaddition/{id}")
    public TradeAdditionDto fetchTradeAdditionById(@PathVariable("id") final String id) {
        return tradeAdditionDocumentService.fetchById(id);
    }

    @GetMapping("/tradeaddition/{id}/history")
    public List<TradeAdditionHistoryDto> fetchHistoryByTradeAdditionId(@PathVariable("id") final String id) {
        return tradeAdditionHistoryService.fetchByTradeAdditionId(id);
    }
}
