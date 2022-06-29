package ru.croc.ugd.ssr.service.flowerrorreport.print;

import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.FOUND_SEVERAL_ERROR_TYPE;
import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.NOT_FOUND_ERROR_TYPE;
import static ru.croc.ugd.ssr.service.flowerrorreport.FlowErrorReportService.WRONG_COMMUNAL_LIVER_ERROR_TYPE;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.exception.WorkbookNotCreatedException;
import ru.croc.ugd.ssr.flowreportederror.FlowReportedErrorData;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FlowErrorReportGenerator {

    private final FlowErrorExcelDataPrinter xssfExcelDataPrinter;

    public byte[] printFlowErrorReport(final List<FlowReportedErrorData> flowReportedErrorData) {
        final List<FlowReportedErrorData> personDuplicateErrorData = filterByErrorType(
            flowReportedErrorData, FOUND_SEVERAL_ERROR_TYPE);
        final List<FlowReportedErrorData> personNotFoundErrorData = filterByErrorType(
            flowReportedErrorData, NOT_FOUND_ERROR_TYPE);
        final List<FlowReportedErrorData> wrongCommunalErrorData = filterByErrorType(
            flowReportedErrorData, WRONG_COMMUNAL_LIVER_ERROR_TYPE);
        final List<List<FlowReportedErrorData>> flowReportedErrorDataList = Arrays
            .asList(personDuplicateErrorData, personNotFoundErrorData, wrongCommunalErrorData);

        try (final ByteArrayOutputStream result = new ByteArrayOutputStream();
             final XSSFWorkbook workbookExtracted = getWorkbook(flowReportedErrorDataList)) {
            if (workbookExtracted == null) {
                throw new WorkbookNotCreatedException();
            }
            workbookExtracted.write(result);
            return result.toByteArray();
        } catch (IOException e) {
            log.error("FlowErrorReportGenerator: couldn't print report", e);
            throw new WorkbookNotCreatedException();
        }
    }

    private XSSFWorkbook getWorkbook(final List<List<FlowReportedErrorData>> flowReportedErrorDataList) {
        final AtomicReference<XSSFWorkbook> workbookRef = new AtomicReference<>();
        flowReportedErrorDataList
            .stream()
            .filter(StreamUtils.not(CollectionUtils::isEmpty))
            .forEach(dataForPrinting -> {
                if (workbookRef.get() == null) {
                    workbookRef.set(xssfExcelDataPrinter
                        .printData(dataForPrinting,
                            FlowErrorTableSettings.getPrintSettingForData(dataForPrinting.get(0))));
                } else {
                    workbookRef.set(xssfExcelDataPrinter
                        .printDataToNewSheet(workbookRef.get(),
                            dataForPrinting,
                            FlowErrorTableSettings.getPrintSettingForData(dataForPrinting.get(0))));
                }
            });
        return workbookRef.get();
    }

    private List<FlowReportedErrorData> filterByErrorType(
        final List<FlowReportedErrorData> flowReportedErrorData,
        final String errorType
    ) {
        return flowReportedErrorData
            .stream()
            .filter(filteringObject -> StringUtils.equals(errorType, filteringObject.getErrorType()))
            .collect(Collectors.toList());
    }
}
