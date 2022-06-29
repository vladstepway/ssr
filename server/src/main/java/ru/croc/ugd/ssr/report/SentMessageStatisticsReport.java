package ru.croc.ugd.ssr.report;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.dto.SentMessageStatisticsDto;
import ru.croc.ugd.ssr.service.NativeQueryService;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис формирования отчета по статистике уведомлений жителям.
 */
@Service
@RequiredArgsConstructor
public class SentMessageStatisticsReport {

    private static final String SUBSYSTEM_CODE = "UGD_SSR";

    private final FilestoreRemoteService filestoreRemoteService;
    private final BeanFactory beanFactory;
    private final SystemProperties systemProperties;
    private final FilestoreV2RemoteService remoteService;
    private final NativeQueryService nativeQueryService;

    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    /**
     * Формирование отчета.
     * 
     * @return ид файла отчета в альфреско.
     */
    @SneakyThrows
    public String createReport() {
        List<SentMessageStatisticsDto> sentMessagesStatistics = nativeQueryService.getSentMessagesStatistics();
        final Workbook workbook = new HSSFWorkbook();
        final Sheet sheet = workbook.createSheet("Отчет");

        fillSheet(sheet, sentMessagesStatistics);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return filestoreRemoteService.createFile(
                "Отчет со статистикой уведомлений от " + LocalDateTime.now() + ".xls",
                "application/vnd.ms-excel",
                outputStream.toByteArray(),
                createFolder(),
                "xls",
                null,
                null,
                "UGD"
        );
    }

    private void fillSheet(Sheet sheet, List<SentMessageStatisticsDto> sentMessagesStatistics) {
        SentMessageStatisticsReportBuilder builder
                = beanFactory.getBean(SentMessageStatisticsReportBuilder.class);

        final Row header = sheet.createRow(0);
        SentMessageStatisticsReportBuilder.createHeader(header);

        for (SentMessageStatisticsDto sentMessagesStatistic : sentMessagesStatistics) {
            builder.addName(sentMessagesStatistic.getName());
            builder.addElk(sentMessagesStatistic.getElk());
            builder.addSended(sentMessagesStatistic.getSended());
            builder.addHanded(sentMessagesStatistic.getHanded());
            builder.addPercentage(sentMessagesStatistic.getPercentage());

            builder.fillRow(sheet.createRow(sheet.getLastRowNum() + 1));
        }

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String createFolder() {
        CreateFolderRequest request = new CreateFolderRequest();
        request.setPath(rootPath + "/reports");
        request.setErrorIfAlreadyExists(false);
        request.setAttrs(FilestoreFolderAttrs.builder()
                .folderTypeID("-")
                .folderEntityID("-")
                .folderSourceReference(FilestoreSourceRef.SERVICE.name())
                .build());
        return remoteService.createFolder(request, systemProperties.getSystem(), SUBSYSTEM_CODE).getId();
    }
}
