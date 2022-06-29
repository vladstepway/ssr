package ru.croc.ugd.ssr.service.excel.person;

import static ru.croc.ugd.ssr.integration.util.FileUtils.createDir;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeErrorLog;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeInfoLog;
import static ru.croc.ugd.ssr.service.excel.person.PersonLetterAndContractLogUtils.writeWarnLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.croc.ugd.ssr.service.excel.model.process.ProcessResult;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonLetterAndContractService {

    private static final String LOG_PATH = "%s%s/";
    private static final String LOG_FILE_NAME = "%s_%s_logs.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Value("${ugd.ssr.log.person-letter-and-contract-upload.path}")
    private String logPath;

    private final PersonLetterAndContractSheetRowProcessor personLetterAndContractSheetRowProcessor;

    public void uploadOfferLetter(final MultipartFile multipartFile) {
        final String fullLogPath = String.format(LOG_PATH, logPath, LocalDate.now().format(DATE_FORMATTER));
        final String logFileName = String.format(LOG_FILE_NAME, Instant.now().toEpochMilli(), multipartFile.getName());
        createDir(fullLogPath);
        try (final FileOutputStream fos = new FileOutputStream(fullLogPath + logFileName)) {
            writeInfoLog(
                fos,
                "Запущена загрузка данных",
                "PersonLetterAndContract. Started data upload"
            );
            uploadOfferLetter(multipartFile.getBytes(), fos);
        } catch (Exception e) {
            log.warn("PersonLetterAndContract. Unable to upload offer letter data: {}", e.getMessage(), e);
        }
    }

    @Async
    void uploadOfferLetter(final byte[] fileToProcess, final FileOutputStream fos) {
        try (final InputStream inputStream = new ByteArrayInputStream(fileToProcess);
             final XSSFWorkbook sheetWorkbook = new XSSFWorkbook(inputStream)) {
            writeInfoLog(
                fos,
                "Начался процесс разбора файла и загрузки данных",
                "PersonLetterAndContract. Start upload offer letter data"
            );
            final ProcessResult processResult = personLetterAndContractSheetRowProcessor.process(sheetWorkbook, fos);
            writeInfoLog(
                fos,
                "Выполнен разбор файла и загрузка данных",
                "PersonLetterAndContract. Table parsed to upload offer letter data"
            );
            if (processResult.isAnyInvalid()) {
                writeWarnLog(
                    fos,
                    processResult.getPrintedResults(),
                    "PersonLetterAndContract. " + processResult.getPrintedResults()
                );
            }
            writeInfoLog(
                fos,
                "Завершился процесс разбора файла и загрузки данных",
                "PersonLetterAndContract. Finish upload offer letter data"
            );
        } catch (Exception e) {
            writeErrorLog(
                fos,
                "Ошибка разбора файла и загрузки данных",
                "PersonLetterAndContract. Failed to upload offer letter data: ",
                e
            );
        }
    }
}
