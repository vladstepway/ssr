package ru.croc.ugd.ssr.service.document;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.FlowReportedErrorDao;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedError;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.model.FlowReportedErrorDocument;
import ru.croc.ugd.ssr.types.SsrDocumentTypes;
import ru.reinform.cdp.document.model.DocumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FlowReportedErrorDocumentService extends SsrAbstractDocumentService<FlowReportedErrorDocument> {
    private final FlowReportedErrorDao flowReportedErrorDao;

    @NotNull
    @Override
    public DocumentType<FlowReportedErrorDocument> getDocumentType() {
        return SsrDocumentTypes.FLOW_REPORTED_ERROR;
    }

    public void createDocument(final FlowReportedErrorData flowReportedErrorData) {
        super.createDocument(
            getFlowReportedErrorDocumentFromData(flowReportedErrorData),
            false,
            null
        );
    }

    public List<FlowReportedErrorDocument> findFlowReportedErrorsByReportDate(final LocalDate reportedDate) {
        return flowReportedErrorDao.findFlowReportedErrorsByReportDate(reportedDate)
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<FlowReportedErrorDocument> findAvailableForFix() {
        return flowReportedErrorDao.findAvailableForFix()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    public List<FlowReportedErrorDocument> findUnfixed() {
        return flowReportedErrorDao.findUnfixed()
            .stream()
            .map(this::parseDocumentData)
            .collect(Collectors.toList());
    }

    private FlowReportedErrorDocument getFlowReportedErrorDocumentFromData(
        final FlowReportedErrorData flowReportedErrorData
    ) {
        final FlowReportedErrorDocument flowReportedErrorDocument = new FlowReportedErrorDocument();
        final FlowReportedError flowReportedError = new FlowReportedError();
        flowReportedErrorDocument.setDocument(flowReportedError);
        flowReportedError.setFlowReportedErrorData(flowReportedErrorData);
        return flowReportedErrorDocument;
    }
}
