package ru.croc.ugd.ssr.service.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import ru.reinform.cdp.document.model.DocumentAbstract;
import ru.reinform.cdp.document.model.DocumentType;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * IExtendedDocumentService.
 */
public interface IExtendedDocumentService {

    /**
     * fetchDocumentsPageByQueryNode.
     *
     * @param documentType documentType
     * @param queryNode queryNode
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return listOfDocuments
     */
    @Nonnull
    <T extends DocumentAbstract> List<T> fetchDocumentsPageByQueryNode(
        DocumentType<T> documentType,
        ObjectNode queryNode,
        List<String> searchCriterias,
        String sortDirection,
        int pageNum,
        int pageSize
    );

    /**
     * countDocuments.
     *
     * @param anyDocumentTypeCode anyDocumentTypeCode
     * @param queryNode queryNode
     * @return amount of documents
     */
    long countDocuments(final String anyDocumentTypeCode, final ObjectNode queryNode);

    /**
     * deleteDocumentsByIds.
     * @param anyDocumentTypeCode anyDocumentTypeCode
     * @param ids ids
     * @param flagReindex flagReindex
     * @param notes notes
     * @return removed documents
     */
    List<DocumentAbstract> deleteDocumentsByIds(
        final String anyDocumentTypeCode, final List<String> ids, final boolean flagReindex, final String notes
    );
}
