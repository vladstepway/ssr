package ru.croc.ugd.ssr.service.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.ExtendedDocumentDao;
import ru.reinform.cdp.backend.service.AppIdentity;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.DocumentTypesService;
import ru.reinform.cdp.document.service.IDocumentService;
import ru.reinform.cdp.utils.core.RIExceptionUtils;
import ru.reinform.cdp.utils.core.RIStringUtils;
import ru.reinform.cdp.utils.mapper.JsonMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * ExtendedDocumentService.
 */
@Service
@AllArgsConstructor
public class ExtendedDocumentService implements IExtendedDocumentService {

    private final AppIdentity appIdentity;
    private final ExtendedDocumentDao extendedDocumentDao;
    private final JsonMapper jsonMapper;
    private final DocumentTypesService documentTypesService;

    @Nonnull
    @Override
    public <T extends DocumentAbstract> List<T> fetchDocumentsPageByQueryNode(
        final DocumentType<T> documentType,
        final ObjectNode queryNode,
        final List<String> sortCriterias,
        final String sortDirection,
        final int pageNum,
        final int pageSize
    ) {
        final String jsonQuery = this.jsonMapper.writeObject(queryNode);
        try {
            final List<DocumentData> documentDataList = this.extendedDocumentDao.findByJsonNodeAndDocType(
                jsonQuery,
                documentType.getCode(),
                PageRequest.of(pageNum, pageSize, getSort(sortCriterias, sortDirection))
            );
            return ListUtils.emptyIfNull(documentDataList)
                .stream()
                .map(documentData -> parseDocumentData(documentData, documentType))
                .collect(Collectors.toList());
        } catch (Exception ex) {
            throw RIExceptionUtils
                .wrap(ex, "error on {0}(type: {1}, query: {2})", RIExceptionUtils.method(), documentType, jsonQuery)
                .withUserMessage("Ошибка поиска документов {0}.", documentType);
        }
    }

    /**
     * countDocuments.
     * @param anyDocumentTypeCode anyDocumentTypeCode
     * @param queryNode queryNode
     * @return count
     */
    public long countDocuments(final String anyDocumentTypeCode, final ObjectNode queryNode) {
        final String documentTypeCode = this.appIdentity.briefDocumentTypeCode(anyDocumentTypeCode);
        final String jsonQuery = this.jsonMapper.writeObject(queryNode);
        try {
            return extendedDocumentDao.countAllJsonNodeAndDocType(jsonQuery, documentTypeCode);
        } catch (Exception var2) {
            throw RIExceptionUtils
                .wrap(var2, "error on {0}(type: {1})", RIExceptionUtils.method(), documentTypeCode)
                .withUserMessage("Ошибка подсчета документов {0}.", documentTypeCode);
        }
    }

    @Override
    public List<DocumentAbstract> deleteDocumentsByIds(
        final String anyDocumentTypeCode, final List<String> ids, final boolean flagReindex, final String notes
    ) {
        final String documentTypeCode = this.appIdentity.briefDocumentTypeCode(anyDocumentTypeCode);
        final IDocumentService<DocumentAbstract> documentService = this.documentTypesService
            .getDocumentService(documentTypeCode);

        return ids.stream()
            .map(id -> documentService.deleteDocument(id, flagReindex, notes))
            .collect(Collectors.toList());
    }

    private Sort getSort(
        final List<String> sortCriterias,
        final String sortDirection
    ) {
        if (CollectionUtils.isEmpty(sortCriterias)) {
            return Sort.by(getSortDirection(sortDirection), "id");
        }
        return JpaSort.unsafe(getSortDirection(sortDirection),
            sortCriterias
                .stream()
                .map(this::getFormattedSearchCriteria)
                .collect(Collectors.toList())
        );
    }

    private String getFormattedSearchCriteria(final String sortCriteria) {
        final List<String> splittedTokens = Arrays.asList(sortCriteria.split("\\."));
        return splittedTokens
            .stream()
            .collect(Collectors.joining("'->'", "json_data->'", "'"));
    }

    private static Direction getSortDirection(final String direction) {
        return Direction.fromOptionalString(direction)
            .orElse(Direction.ASC);
    }

    private <T extends DocumentAbstract> T parseDocumentData(
        @Nonnull DocumentData documentData, @Nonnull DocumentType<T> documentType
    ) {
        try {
            return this.parseDocumentJson(documentData.getJsonData(), documentType);
        } catch (Exception ex) {
            throw RIExceptionUtils.wrap(
                ex,
                "error on {0}(id: {1}, docType: {2})",
                RIExceptionUtils.method(),
                documentData.getId(),
                RIStringUtils.objToString(documentType));
        }
    }

    private <T extends DocumentAbstract> T parseDocumentJson(
        @Nonnull String json, @Nonnull DocumentType<T> documentType
    ) {
        return this.jsonMapper.readObject(json, documentType.getTypeClass());
    }
}
