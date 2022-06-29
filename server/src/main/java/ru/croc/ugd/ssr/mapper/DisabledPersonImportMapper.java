package ru.croc.ugd.ssr.mapper;

import static java.util.Optional.ofNullable;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonImportDto;
import ru.croc.ugd.ssr.model.disabledperson.DisabledPersonImportDocument;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ERROR)
public interface DisabledPersonImportMapper {

    @Mapping(target = "document.disabledPersonImportData.creationDateTime", source = "creationDateTime")
    DisabledPersonImportDocument toEmptyDisabledPersonImportDocument(final LocalDateTime creationDateTime);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "folderId", source = "folderId")
    @Mapping(target = "percentageReady", source = "document.disabledPersonImportData.percentageReady")
    @Mapping(target = "disabledPersons", source = "document.disabledPersonImportData.disabledPersonRow")
    RestDisabledPersonImportDto toRestDisabledPersonImportDto(
        final DisabledPersonImportDocument disabledPersonImportDocument
    );

    @Mapping(target = "document.disabledPersonImportData.status", constant = "INPROGRESS")
    @Mapping(target = "document.disabledPersonImportData.uploadedFileId", source = "fileStoreId")
    @Mapping(target = "document.disabledPersonImportData.percentageReady", constant = "0")
    DisabledPersonImportDocument toDocumentWithInProgressStatus(
        @MappingTarget final DisabledPersonImportDocument disabledPersonImportDocument, final String fileStoreId
    );

    @Mapping(target = "document.disabledPersonImportData.status", constant = "ERROR")
    @Mapping(
        target = "document.disabledPersonImportData.errorDetails.dateTime",
        expression = "java( java.time.LocalDateTime.now() )"
    )
    @Mapping(
        target = "document.disabledPersonImportData.errorDetails.stacktrace",
        expression = "java( org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exception) )"
    )
    @Mapping(target = "document.disabledPersonImportData.errorDetails.description", source = "exception.message")
    DisabledPersonImportDocument toDocumentWithErrorStatus(
        @MappingTarget final DisabledPersonImportDocument disabledPersonImportDocument, final Exception exception
    );

    @Mapping(target = "document.disabledPersonImportData.status", constant = "COMPLETED")
    @Mapping(target = "document.disabledPersonImportData.processedFileId", source = "processedFileId")
    @Mapping(
        target = "document.disabledPersonImportData.warnings", source = "processResult", qualifiedByName = "toWarnings"
    )
    DisabledPersonImportDocument toDocumentWithCompletedStatus(
        @MappingTarget final DisabledPersonImportDocument disabledPersonImportDocument,
        final String processedFileId,
        final ProcessResult processResult
    );

    @Named("toWarnings")
    default String toWarnings(final ProcessResult processResult) {
        return ofNullable(processResult)
            .filter(ProcessResult::isAnyInvalid)
            .map(ProcessResult::getPrintedResults)
            .orElse(null);
    }
}
