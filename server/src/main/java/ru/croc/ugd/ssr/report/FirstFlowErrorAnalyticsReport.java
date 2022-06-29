package ru.croc.ugd.ssr.report;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.FirstFlowError;
import ru.croc.ugd.ssr.FirstFlowErrorAnalyticsData;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.model.FirstFlowErrorAnalyticsDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.FirstFlowErrorAnalyticsService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Сервис формирования отчета по задаче на разбор ошибок ДГИ.
 */
@Service
@RequiredArgsConstructor
public class FirstFlowErrorAnalyticsReport {

    private static final String SUBSYSTEM_CODE = "UGD_SSR";

    private final FirstFlowErrorAnalyticsService firstFlowErrorAnalyticsService;
    private final FilestoreRemoteService filestoreRemoteService;
    private final BeanFactory beanFactory;
    private final SystemProperties systemProperties;
    private final FilestoreV2RemoteService remoteService;
    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;

    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    /**
     * Формирование отчета.
     * 
     * @param id   id документа задачи на разбор ошибок ДГИ
     * @return ид файла отчета в альфреско.
     */
    @SneakyThrows
    public String createReport(final String id) {
        FirstFlowErrorAnalyticsDocument document = firstFlowErrorAnalyticsService.fetchDocument(id);
        FirstFlowErrorAnalyticsData data = document.getDocument().getData();
        final Workbook workbook = new HSSFWorkbook();

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        final Sheet sheet = workbook.createSheet(data.getUnom());

        fillSheet(sheet, data, cellStyle);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return filestoreRemoteService.createFile(
                "Ошибки загрузки данных дома " + data.getUnom() + " от " + LocalDateTime.now() + ".xls",
                "application/vnd.ms-excel",
                outputStream.toByteArray(),
                createFolder(),
                "xls",
                id,
                null,
                "UGD"
        );
    }

    private void fillSheet(Sheet sheet, FirstFlowErrorAnalyticsData data, CellStyle cellStyle) {
        FirstFlowErrorAnalyticsReportRowBuilder builder
                = beanFactory.getBean(FirstFlowErrorAnalyticsReportRowBuilder.class);

        final Row header = sheet.createRow(0);
        FirstFlowErrorAnalyticsReportRowBuilder.createHeader(header);

        for (FirstFlowError error : data.getErrors()) {
            PersonDocument personDocument = personDocumentService.fetchDocument(error.getPersonId());
            PersonType personData = personDocument.getDocument().getPersonData();

            FlatType flat = null;
            if (StringUtils.isNotBlank(personData.getFlatID())) {
                flat = flatService.fetchFlat(personData.getFlatID());
            }

            FirstFlowError.Flat errorFlat = error.getFlat();
            FirstFlowError.Message errorMessage = error.getMessage();
            FirstFlowError.Result errorResult = error.getResult();

            builder.addUnom(data.getUnom())
                    .addFlatNumberDgi(errorFlat == null ? null : errorFlat.getSnosFlatNum())
                    .addFlatNumberEvd(personData.getFlatNum())
                    .addFlatNumberRes(errorResult == null ? null : errorResult.getFlat())
                    .addRoomsDgi(
                            errorFlat == null
                                    ? null
                                    : errorFlat.getSnosRoomsNum()
                                        .stream()
                                        .filter(StringUtils::isNotBlank)
                                        .map(s -> s.replace(" ", ""))
                                        .flatMap(s -> Arrays.stream(s.split(",")))
                                        .sorted(Comparator.comparingInt(Integer::parseInt))
                                        .collect(Collectors.joining(", "))
                    )
                    .addRoomsEvd(
                            personData.getRoomNum()
                                    .stream()
                                    .filter(StringUtils::isNotBlank)
                                    .map(s -> s.replace(" ", ""))
                                    .flatMap(s -> Arrays.stream(s.split(",")))
                                    .sorted(Comparator.comparingInt(Integer::parseInt))
                                    .collect(Collectors.joining(", "))
                    )
                    .addRoomsRes(
                            errorResult == null
                                    ? null
                                    : errorResult.getRoomsNum()
                                        .stream()
                                        .filter(StringUtils::isNotBlank)
                                        .map(s -> s.replace(" ", ""))
                                        .flatMap(s -> Arrays.stream(s.split(",")))
                                        .sorted(Comparator.comparingInt(Integer::parseInt))
                                        .collect(Collectors.joining(", "))
                    )
                    .addPersonIdDgi(errorMessage == null ? null : errorMessage.getPersonId())
                    .addPersonIdEvd(personData.getPersonID())
                    .addPersonIdRes(errorResult == null ? null : errorResult.getPersonId())
                    .addAffairIdDgi(errorFlat == null ? null : errorFlat.getAffairId())
                    .addAffairIdEvd(personData.getAffairId())
                    .addAffairIdRes(errorResult == null ? null : errorResult.getAffairId())
                    .addSnilsDgi(errorMessage == null ? null : errorMessage.getSnils())
                    .addSnilsEvd(personData.getSNILS())
                    .addSnilsRes(errorResult == null ? null : errorResult.getSnils())
                    .addLastNameDgi(errorMessage == null ? null : errorMessage.getLastName())
                    .addLastNameEvd(personData.getLastName())
                    .addLastNameRes(errorResult == null ? null : errorResult.getLastName())
                    .addFirstNameDgi(errorMessage == null ? null : errorMessage.getFirstName())
                    .addFirstNameEvd(personData.getFirstName())
                    .addFirstNameRes(errorResult == null ? null : errorResult.getFirstName())
                    .addMiddleNameDgi(errorMessage == null ? null : errorMessage.getMiddleName())
                    .addMiddleNameEvd(personData.getMiddleName())
                    .addMiddleNameRes(errorResult == null ? null : errorResult.getMiddleName())
                    .addBirthDateDgi(errorMessage == null ? null : errorMessage.getBirthDate())
                    .addBirthDateEvd(personData.getBirthDate())
                    .addBirthDateRes(errorResult == null ? null : errorResult.getBirthDate())
                    .addGenderDgi(errorMessage == null ? null : errorMessage.getSex())
                    .addGenderEvd(personData.getGender())
                    .addGenderRes(errorResult == null ? null : errorResult.getSex())
                    .addStatusLivingDgi(errorFlat == null ? null : errorFlat.getStatusLiving())
                    .addStatusLivingEvd(personData.getStatusLiving())
                    .addStatusLivingRes(errorResult == null ? null : errorResult.getStatusLiving())
                    .addWaiterDgi(errorMessage == null ? null : errorMessage.getIsQueue())
                    .addWaiterEvd(personData.getWaiter())
                    .addWaiterRes(errorResult == null ? "0" : errorResult.getIsQueue())
                    .addDeadDgi(errorMessage == null ? null : errorMessage.getIsDead())
                    .addDeadEvd(personData.getAddInfo() == null ? null : personData.getAddInfo().getIsDead())
                    .addDeadRes(errorResult == null ? "0" : errorResult.getIsDead())
                    .addDelFlagDgi(errorMessage == null || errorFlat == null
                            || StringUtils.isBlank(errorFlat.getSnosUnom())
                            ? null
                            : "Удален".equalsIgnoreCase(errorMessage.getChangeStatus()) ? "1" : "0")
                    .addDelFlagRes(errorResult != null && "Удалить запись".equals(errorResult.getToDelete())
                            ? "1"
                            : "0")
                    .addDelReasonDgi(errorMessage == null ? null : errorMessage.getDelReason())
                    .addDelReasonRes(errorResult == null ? null : errorResult.getDelReason())
                    .addCadNumDgi(errorFlat == null ? null : errorFlat.getSnosCadnum())
                    .addCadNumEvd(personData.getCadNum())
                    .addCadNumRes(errorResult == null ? null : errorResult.getCadastralNumber())
                    .addCommunDgi(errorFlat == null || StringUtils.isBlank(errorFlat.getSnosUnom())
                            ? null
                            : errorFlat.getSnosRoomsNum().size() > 0 ? "1" : "0")
                    .addCommunEvd(flat == null ? null : "Коммунальная".equals(flat.getFlatType()) ? "1" : "0")
                    .addCommunRes(errorResult == null ? "0" : errorResult.isCommun() ? "1" : "0")
                    .addEncumbrancesDgi(errorFlat == null ? null : errorFlat.getEncumbrances())
                    .addEncumbrancesEvd(personData.getEncumbrances())
                    .addEncumbrancesRes(errorResult == null ? null : errorResult.getEncumbrances())
                    .addNoFlatDgi(errorFlat == null ? null : errorFlat.getNoFlat())
                    .addNoFlatEvd(personData.getAddFlatInfo() == null ? null : personData.getAddFlatInfo().getNoFlat())
                    .addNoFlatRes(errorResult == null ? "0" : errorResult.getNoFlat())
                    .addOwnFederalDgi(errorFlat == null ? null : errorFlat.getIsFederal())
                    .addOwnFederalEvd(personData.getAddFlatInfo() == null
                            ? null
                            : personData.getAddFlatInfo().getOwnFederal())
                    .addOwnFederalRes(errorResult == null ? "0" : errorResult.getIsFederal())
                    .addInCourtDgi(errorFlat == null ? null : errorFlat.getInCourt())
                    .addInCourtEvd(personData.getAddFlatInfo() == null
                            ? null
                            : personData.getAddFlatInfo().getInCourt())
                    .addInCourtRes(errorResult == null ? "0" : errorResult.getInCourt());

            builder.fillRow(sheet.createRow(sheet.getLastRowNum() + 1), cellStyle);
        }

        autoSize(sheet, 62);
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

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
