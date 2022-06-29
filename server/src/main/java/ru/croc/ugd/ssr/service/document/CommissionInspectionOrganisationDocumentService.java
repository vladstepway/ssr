package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.CommissionInspectionOrganisationDao;
import ru.croc.ugd.ssr.model.commissionorganisation.CommissionInspectionOrganisationDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.db.model.DocumentData;
import ru.reinform.cdp.document.model.DocumentType;
import ru.reinform.cdp.document.service.AbstractDocumentService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * CommissionInspectionOrganisationDocumentService.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommissionInspectionOrganisationDocumentService
    extends AbstractDocumentService<CommissionInspectionOrganisationDocument> {

    private final CommissionInspectionOrganisationDao commissionInspectionOrganisationDao;

    @NotNull
    @Override
    public DocumentType<CommissionInspectionOrganisationDocument> getDocumentType() {
        return SsrDocumentTypes.COMMISSION_INSPECTION_ORGANISATION;
    }

    /**
     * Returns all commission inspection organisations.
     *
     * @return list of commission inspections organisations
     */
    public List<CommissionInspectionOrganisationDocument> findAll() {
        return commissionInspectionOrganisationDao.fetchAll()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    /**
     * Finds organisation by area and type.
     * @param area area
     * @param type type
     * @return organisation
     */
    public Optional<CommissionInspectionOrganisationDocument> findByAreaAndType(final String area, final int type) {
        return commissionInspectionOrganisationDao.findByAreaAndType(area, type)
            .stream()
            .map(this::parseDocumentData)
            .findFirst();
    }

    private CommissionInspectionOrganisationDocument parseDocumentData(@Nonnull DocumentData documentData) {
        return parseDocumentJson(documentData.getJsonData());
    }

    private CommissionInspectionOrganisationDocument parseDocumentJson(@Nonnull String json) {
        return jsonMapper.readObject(json, CommissionInspectionOrganisationDocument.class);
    }

}
