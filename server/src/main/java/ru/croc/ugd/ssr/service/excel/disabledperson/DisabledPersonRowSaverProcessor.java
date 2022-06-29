package ru.croc.ugd.ssr.service.excel.disabledperson;

import static java.util.Objects.nonNull;
import static ru.croc.ugd.ssr.service.disabledperson.DisabledPersonImportProcessor.DOCUMENT_ID_PROCESSING_KEY;
import static ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants.COMMENT_COLUMN_INDEX;
import static ru.croc.ugd.ssr.utils.DateTimeUtils.getDateFromString;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.disabledperson.DisabledPersonRow;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.DisabledPersonDocumentService;
import ru.croc.ugd.ssr.service.document.DisabledPersonImportDocumentService;
import ru.croc.ugd.ssr.service.excel.disabledperson.model.DisabledPersonSheetConstants;
import ru.croc.ugd.ssr.service.excel.model.process.XssfCellProcessResult;
import ru.croc.ugd.ssr.service.excel.model.process.XssfRowProcessResult;
import ru.croc.ugd.ssr.utils.DateTimeUtils;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class DisabledPersonRowSaverProcessor extends DisabledPersonSheetRowProcessor {

    private static final String FLAT_NUMBER_REGEXP = ",?\\s*кв((артира)?|\\.)\\s*([0-9]+)";

    private final PersonDocumentService personDocumentService;
    private final DisabledPersonDocumentService disabledPersonDocumentService;

    public DisabledPersonRowSaverProcessor(
        final DisabledPersonSheetCellProcessor disabledPersonSheetCellProcessor,
        final DisabledPersonImportDocumentService disabledPersonImportDocumentService,
        final PersonDocumentService personDocumentService,
        final DisabledPersonDocumentService disabledPersonDocumentService
    ) {
        super(disabledPersonSheetCellProcessor, disabledPersonImportDocumentService);
        this.personDocumentService = personDocumentService;
        this.disabledPersonDocumentService = disabledPersonDocumentService;
    }

    @Override
    protected XssfRowProcessResult saveRow(
        final XSSFRow rowToProcess, final XssfRowProcessResult xssfRowProcessResult
    ) {
        try {
            final DisabledPersonRow disabledPersonRow = parseDisabledPerson(xssfRowProcessResult);
            disabledPersonRow.setComment(getCommentRowValue(rowToProcess));
            disabledPersonRow.setPageName(getPageNameFromRow(rowToProcess));
            disabledPersonRow.setRowNumber(rowToProcess.getRowNum());
            setUniqueExcelRecordId(disabledPersonRow);
            setPersonDocumentIdIfFound(disabledPersonRow);
            setDisabledPersonDocumentIdIfFound(disabledPersonRow);
            updateDisabledPersonImportDocument(disabledPersonImportDocument -> disabledPersonImportDocument
                .getDocument()
                .getDisabledPersonImportData()
                .getDisabledPersonRow()
                .add(disabledPersonRow)
            );
            saveIsRowRecorded(rowToProcess, true);
            updateDisabledPersonImportReportInfo(true);
        } catch (Exception e) {
            updateDisabledPersonImportReportInfo(false);
            log.error(
                "Failed to store disabled person data: rowNumber = {}, disabledPersonImportDocumentId = {}",
                rowToProcess.getRowNum(),
                getDocumentId(),
                e
            );
            addAndSaveRowCommentSection(rowToProcess, "Ошибка сохранения строки!");
        }
        return xssfRowProcessResult;
    }

    private void setUniqueExcelRecordId(final DisabledPersonRow disabledPersonRow) {
        final String uniqueExcelRecordId = Stream.of(
                disabledPersonRow.getFullAddressFrom(),
                disabledPersonRow.getLastName(),
                disabledPersonRow.getFirstName(),
                disabledPersonRow.getMiddleName(),
                DateTimeUtils.getFormattedDate(disabledPersonRow.getBirthDate())
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining("_"))
            .replace(" ", "_");
        disabledPersonRow.setUniqueExcelRecordId(uniqueExcelRecordId);
    }

    private void setDisabledPersonDocumentIdIfFound(final DisabledPersonRow disabledPersonRow) {
        disabledPersonDocumentService.fetchByUniqueExcelRecordId(disabledPersonRow.getUniqueExcelRecordId())
            .ifPresent(disabledPersonDocument -> disabledPersonRow.setDisabledPersonDocumentId(
                disabledPersonDocument.getId()
            ));
    }

    private void setPersonDocumentIdIfFound(final DisabledPersonRow disabledPersonRow) {
        final String fullName = Stream.of(
                disabledPersonRow.getLastName(), disabledPersonRow.getFirstName(), disabledPersonRow.getMiddleName()
            )
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));

        personDocumentService.fetchPersonByFullNameAndBirthDate(fullName, disabledPersonRow.getBirthDate())
            .map(PersonDocument::getId)
            .ifPresent(disabledPersonRow::setPersonDocumentId);
    }

    private String getDocumentId() {
        return processParameters.getProcessingParams().get(DOCUMENT_ID_PROCESSING_KEY);
    }

    private void addAndSaveRowCommentSection(XSSFRow rowToUpdate, String addNewValue) {
        saveCell(
            rowToUpdate,
            COMMENT_COLUMN_INDEX.getColumnIndex(),
            getCommentRowValue(rowToUpdate) + "\n" + addNewValue
        );
    }

    private DisabledPersonRow parseDisabledPerson(final XssfRowProcessResult xssfRowProcessResult) {
        final DisabledPersonRow disabledPersonRow = new DisabledPersonRow();
        final List<XssfCellProcessResult> xssfCellProcessResults = xssfRowProcessResult
            .getXssfCellProcessResults();
        if (!CollectionUtils.isEmpty(xssfCellProcessResults)) {
            xssfCellProcessResults.forEach(xssfCellProcessResult -> mapProcessRowResult(
                xssfCellProcessResult, disabledPersonRow
            ));
        }
        return disabledPersonRow;
    }

    private void mapProcessRowResult(
        final XssfCellProcessResult xssfCellProcessResult, final DisabledPersonRow disabledPersonRow
    ) {
        final DisabledPersonSheetConstants disabledPersonSheetConstants = DisabledPersonSheetConstants.findByIndex(
            xssfCellProcessResult.getCollIndex()
        );
        if (disabledPersonSheetConstants == null) {
            return;
        }
        final String cellValue = StringUtils.trimToNull(xssfCellProcessResult.getCellRawValue());
        switch (disabledPersonSheetConstants) {
            case LAST_NAME_COLUMN_INDEX:
                disabledPersonRow.setLastName(cellValue);
                break;
            case FIRST_NAME_COLUMN_INDEX:
                disabledPersonRow.setFirstName(cellValue);
                break;
            case MIDDLE_NAME_COLUMN_INDEX:
                disabledPersonRow.setMiddleName(cellValue);
                break;
            case BIRTH_DATE_COLUMN_INDEX:
                disabledPersonRow.setBirthDate(getDateFromString(cellValue));
                break;
            case ADDRESS_FROM_COLUMN_INDEX:
                disabledPersonRow.setFullAddressFrom(cellValue);
                if (nonNull(cellValue)) {
                    disabledPersonRow.setFlatNumber(
                        StringUtils.trim(retrieveFlatNumber(cellValue))
                    );
                    disabledPersonRow.setAddressFrom(
                        StringUtils.trim(cellValue.replaceAll(FLAT_NUMBER_REGEXP, ""))
                    );
                }
                break;
            case PHONE_COLUMN_INDEX:
                disabledPersonRow.setPhone(cellValue);
                break;
            case DISTRICT_COLUMN_INDEX:
                disabledPersonRow.setDistrict(cellValue);
                break;
            case AREA_COLUMN_INDEX:
                disabledPersonRow.setArea(cellValue);
                break;
            case USING_WHEELCHAIR_COLUMN_INDEX:
                disabledPersonRow.setUsingWheelchair(
                    "ДА".equals(StringUtils.upperCase(cellValue))
                );
                break;
            case UNOM_COLUMN_INDEX:
                disabledPersonRow.setUnom(cellValue);
                break;
            default:
                break;
        }
    }

    private String retrieveFlatNumber(final String content) {
        final Pattern pattern = Pattern.compile(FLAT_NUMBER_REGEXP, Pattern.UNICODE_CHARACTER_CLASS);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(3);
        }
        return StringUtils.EMPTY;
    }
}
