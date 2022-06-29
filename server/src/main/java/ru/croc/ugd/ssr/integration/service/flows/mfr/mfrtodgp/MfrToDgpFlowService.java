package ru.croc.ugd.ssr.integration.service.flows.mfr.mfrtodgp;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.tradeaddition.EstateInfoDto;
import ru.croc.ugd.ssr.integration.service.flows.mfr.dgptomfr.PersonInfoMfrFlowService;
import ru.croc.ugd.ssr.mapper.TradeApplicationFileMapper.TradeFileDto;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateTaskData;
import ru.croc.ugd.ssr.model.integration.etpmv.TaskDataType;
import ru.croc.ugd.ssr.model.integrationflow.IntegrationFlowDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.model.trade.TradeApplicationFileDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionService;
import ru.croc.ugd.ssr.trade.TradeApplicationFile;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public abstract class MfrToDgpFlowService {

    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final TradeAdditionService tradeAdditionService;
    private final PersonDocumentService personDocumentService;
    private final PersonInfoMfrFlowService personInfoMfrFlowService;

    public void processMessage(
        final CoordinateTaskData coordinateTaskData, final IntegrationFlowDocument integrationFlowDocument
    ) {
        final Optional<TradeAdditionDocument> existingTradeAdditionDocument =
            tradeAdditionDocumentService.fetchIndexedBySellId(retrieveSellId(coordinateTaskData));
        final TradeAdditionDocument newTradeAdditionDocument = existingTradeAdditionDocument
            .map(document -> retrieveTradeAddition(coordinateTaskData, integrationFlowDocument.getId(), document))
            .orElseGet(() -> retrieveTradeAddition(coordinateTaskData, integrationFlowDocument.getId()));

        final TradeAdditionDocument savedTradeAdditionDocument = tradeAdditionService.createTradeAdditionByMfrFlow(
            newTradeAdditionDocument, existingTradeAdditionDocument.isPresent()
        );
        final TradeApplicationFileDocument tradeApplicationFileDocument = createTradeApplicationFileIfNeeded(
            savedTradeAdditionDocument, coordinateTaskData, integrationFlowDocument
        );
        tradeAdditionService.saveTradeAdditionHistoryByMfrFlow(savedTradeAdditionDocument, integrationFlowDocument);
        tradeAdditionService.deployTradeAddition(savedTradeAdditionDocument, tradeApplicationFileDocument);
        processAfterDeploy(savedTradeAdditionDocument);
    }

    private TradeApplicationFileDocument createTradeApplicationFileIfNeeded(
        final TradeAdditionDocument tradeAdditionDocument,
        final CoordinateTaskData coordinateTaskData,
        final IntegrationFlowDocument integrationFlowDocument
    ) {
        final TradeApplicationFileDocument tradeApplicationFileDocument =
            ofNullable(retrieveFileDto(coordinateTaskData))
                .map(tradeFileDto -> tradeAdditionService.createTradeApplicationFile(
                    tradeAdditionDocument, integrationFlowDocument, tradeFileDto
                ))
                .orElse(null);

        ofNullable(tradeApplicationFileDocument)
            .map(TradeApplicationFileDocument::getDocument)
            .map(TradeApplicationFile::getTradeApplicationFileTypeData)
            .ifPresent(fileStoreId -> addAttachedFile(tradeAdditionDocument, fileStoreId));

        return tradeApplicationFileDocument;
    }

    private void addAttachedFile(
        final TradeAdditionDocument tradeAdditionDocument, final TradeApplicationFileType tradeApplicationFileType
    ) {
        addFileStoreId(tradeAdditionDocument, tradeApplicationFileType);
        tradeAdditionDocumentService.updateDocument(
            tradeAdditionDocument.getId(), tradeAdditionDocument, true, false, "addAttachedFile"
        );
    }

    protected TradeFileDto retrieveFileDto(final CoordinateTaskData coordinateTaskData) {
        return null;
    }

    protected void addFileStoreId(
        final TradeAdditionDocument tradeAdditionDocument, final TradeApplicationFileType tradeApplicationFileType
    ) {
    }

    protected void processAfterDeploy(final TradeAdditionDocument tradeAdditionDocument) {
    }

    protected abstract String retrieveSellId(final CoordinateTaskData coordinateTaskData);

    protected abstract TradeAdditionDocument retrieveTradeAddition(
        final CoordinateTaskData coordinateTaskData,
        final String integrationFlowDocumentId,
        final TradeAdditionDocument tradeAdditionDocument
    );

    protected TradeAdditionDocument retrieveTradeAddition(
        final CoordinateTaskData coordinateTaskData, final String integrationFlowDocumentId
    ) {
        return retrieveTradeAddition(coordinateTaskData, integrationFlowDocumentId, new TradeAdditionDocument());
    }

    protected Optional<Object> retrieveCustomAttributes(final CoordinateTaskData coordinateTaskData) {
        return ofNullable(coordinateTaskData)
            .map(CoordinateTaskData::getData)
            .map(TaskDataType::getParameter)
            .map(TaskDataType.Parameter::getAny);
    }

    protected List<PersonDocument> retrievePersonDocuments(final String affairId) {
        return personDocumentService.fetchByAffairId(affairId);
    }

    protected EstateInfoDto retrieveOldEstate(final List<PersonDocument> personDocuments) {
        return personDocuments
            .stream()
            .findFirst()
            .map(personDocument -> EstateInfoDto.builder()
                .unom(PersonUtils.getUnom(personDocument))
                .flatNumber(PersonUtils.getFlatNumber(personDocument))
                .rooms(PersonUtils.getRooms(personDocument))
                .build())
            .orElse(null);
    }

    protected List<String> retrievePersonIds(final List<PersonDocument> personDocuments) {
        return personDocuments.stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getPersonID)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    protected void sendPersonInfoFlow(final TradeAdditionDocument tradeAdditionDocument) {
        final String affairId = tradeAdditionDocument.getDocument().getTradeAdditionTypeData().getAffairId();
        final String sellId = tradeAdditionDocument.getDocument().getTradeAdditionTypeData().getSellId();
        if (nonNull(affairId) && nonNull(sellId)) {
            personInfoMfrFlowService.sendPersonInfo(affairId, sellId);
        }
    }
}
