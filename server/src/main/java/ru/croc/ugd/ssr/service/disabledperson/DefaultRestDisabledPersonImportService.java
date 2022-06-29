package ru.croc.ugd.ssr.service.disabledperson;

import static java.util.Objects.nonNull;
import static ru.croc.ugd.ssr.service.disabledperson.DisabledPersonImportProcessor.DOCUMENT_ID_PROCESSING_KEY;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonImportData;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonImportDto;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.mapper.DisabledPersonImportMapper;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.service.SsrFilestoreService;
import ru.croc.ugd.ssr.service.document.DisabledPersonImportDocumentService;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessParameters;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class DefaultRestDisabledPersonImportService implements RestDisabledPersonImportService {

    private final DisabledPersonImportDocumentService disabledPersonImportDocumentService;
    private final DisabledPersonImportMapper disabledPersonImportMapper;
    private final DisabledPersonImportProcessor disabledPersonImportProcessor;
    private final SsrFilestoreService ssrFilestoreService;

    @Override
    public RestDisabledPersonImportDto create() {
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportMapper.toEmptyDisabledPersonImportDocument(LocalDateTime.now());
        final DisabledPersonImportDocument createdDisabledPersonImportDocument =
            disabledPersonImportDocumentService.createDocument(disabledPersonImportDocument, false, "create");
        return disabledPersonImportMapper.toRestDisabledPersonImportDto(createdDisabledPersonImportDocument);
    }

    @Override
    public void parseFile(final String id, final String fileStoreId) {
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportDocumentService.fetchDocument(id);
        final DisabledPersonImportData disabledPersonImportData = disabledPersonImportDocument.getDocument()
            .getDisabledPersonImportData();
        if (nonNull(disabledPersonImportData.getStatus())) {
            throw new SsrException("Процесс разбора файла уже запущен либо выполнен.");
        }

        final byte[] fileToProcess = ssrFilestoreService.getFile(fileStoreId);
        setInProgressStatus(disabledPersonImportDocument, fileStoreId);
        final ProcessParameters processParameters = ProcessParameters.builder()
            .processingParam(DOCUMENT_ID_PROCESSING_KEY, disabledPersonImportDocument.getId())
            .build();
        disabledPersonImportProcessor.performFileParsing(
            disabledPersonImportDocument, fileToProcess, processParameters
        );
    }

    @Override
    public RestDisabledPersonImportDto fetchById(final String id) {
        final DisabledPersonImportDocument disabledPersonImportDocument =
            disabledPersonImportDocumentService.fetchDocument(id);
        return disabledPersonImportMapper.toRestDisabledPersonImportDto(disabledPersonImportDocument);
    }

    private void setInProgressStatus(
        final DisabledPersonImportDocument disabledPersonImportDocument, final String fileStoreId
    ) {
        final DisabledPersonImportDocument updatedDisabledPersonImportDocument =
            disabledPersonImportMapper.toDocumentWithInProgressStatus(disabledPersonImportDocument, fileStoreId);
        disabledPersonImportDocumentService.updateDocument(updatedDisabledPersonImportDocument, "setInProgressStatus");
    }
}
