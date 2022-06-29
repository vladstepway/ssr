package ru.croc.ugd.ssr.report;

import static ru.croc.ugd.ssr.utils.PersonUtils.EVENT_RESETTLEMENT_ACCEPTED;
import static ru.croc.ugd.ssr.utils.PersonUtils.EVENT_RESETTLEMENT_DECLINED;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.Agreements.Agreement;
import ru.croc.ugd.ssr.PersonType.FlatDemo.FlatDemoItem;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.ResettlementHistory;
import ru.croc.ugd.ssr.config.SystemProperties;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.reinform.cdp.filestore.model.FilestoreFolderAttrs;
import ru.reinform.cdp.filestore.model.FilestoreSourceRef;
import ru.reinform.cdp.filestore.model.remote.api.CreateFolderRequest;
import ru.reinform.cdp.filestore.service.FilestoreRemoteService;
import ru.reinform.cdp.filestore.service.FilestoreV2RemoteService;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис формирования отчета.
 */
@Service
@RequiredArgsConstructor
public class FlowsReport {

    private static final String SUBSYSTEM_CODE = "UGD_SSR";

    private static final String FIO_ON_DAY = "По ФИО-на день";

    private static final String SUMMARY_ALL = "Сводный-нарастающим итогом";

    private static final String FIO_ALL = "По ФИО-нарастающим итогом";

    private static final String SUMMARY_ON_DAY = "Сводный отчет-на день";

    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";

    private static DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

    private final PersonDocumentService service;

    private final RealEstateDocumentService realEstateDocumentService;

    private final BeanFactory beanFactory;

    private final FilestoreRemoteService filestoreRemoteService;

    private final SystemProperties systemProperties;

    private final FilestoreV2RemoteService remoteService;

    private final CapitalConstructionObjectService ccoService;

    @Value("${app.filestore.ssr.rootPath}")
    private String rootPath;

    private Map<String, RealEstateDataType> realEstateData = new HashMap<>();

    /**
     * Формирование отчета.
     *
     * @param date
     *            дата на которую формируется отчет.
     * @return ид файла отчета в альфреско.
     */
    @SneakyThrows
    public String createReport(final LocalDate date) {

        final Comparator<PersonType> comparing = Comparator.comparing((PersonType personType) -> {
            final RealEstateDataType realEstateByUnom = getRealEstateByUnom(personType.getUNOM());
            return realEstateByUnom == null
                    ? null
                    : (realEstateByUnom.getAddress() == null
                            ? null
                            : realEstateByUnom.getAddress().toLowerCase());
        }, Comparator.nullsFirst(String::compareTo))
                .thenComparing(PersonType::getFlatNum, Comparator.nullsFirst(String::compareTo))
                .thenComparing(PersonType::getFIO, Comparator.nullsFirst(String::compareTo))
                .thenComparing(PersonType::getAffairId, Comparator.nullsFirst(String::compareTo));

        final List<PersonType> personWithLetters = service.fetchPersonsWithOfferLetters()
                .stream()
                .map(personDocument -> personDocument.getDocument().getPersonData())
                .sorted(comparing)
                .collect(Collectors.toList());

        final List<PersonType> personWithLettersOnDay = personWithLetters.stream()
                .filter(personType -> !getLetterOnDay(personType, date).isEmpty())
                .collect(Collectors.toList());

        final Workbook workbook = new HSSFWorkbook();

        final Sheet fioOnDay = workbook.createSheet(FIO_ON_DAY);
        final Sheet fioAll = workbook.createSheet(FIO_ALL);
        final Sheet summaryOnDay = workbook.createSheet(SUMMARY_ON_DAY);
        final Sheet summaryAll = workbook.createSheet(SUMMARY_ALL);

        fillSheetFioOnDay(personWithLettersOnDay, date, fioOnDay);
        fillSheetFioAll(personWithLetters, fioAll);
        fillSheetSummaryOnDay(personWithLettersOnDay, date, summaryOnDay);
        fillSheetSummary(personWithLetters, summaryAll);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);

        return filestoreRemoteService.createFile("Отчет реновация на "
                + LocalDateTime.now()
                + ".xls", null, outputStream.toByteArray(), createFolder(), "xls", null, null, "UGD");

    }

    private void fillSheetSummary(List<PersonType> personWithLetters, Sheet summaryAll) {
        RowSummaryReportBuilder builder = beanFactory.getBean(RowSummaryReportBuilder.class);

        final Set<BigInteger> unomsOfResettlementHouse = new HashSet<>();
        final Row header = summaryAll.createRow(0);
        RowSummaryReportBuilder.fillHeaderRowFirstTable(header);

        int flatCountResult = 0;
        int personCountResult = 0;
        int offerCountResult = 0;
        int demoCountResult = 0;
        int refusedCountResult = 0;
        int agreementCountResult = 0;
        int contractProjectCountResult = 0;
        int signedContractCountResult = 0;
        int keysIssueCountResult = 0;
        int releaseFlatCountResult = 0;
        int defectCountResult = 0;
        int agreeDefectCountResult = 0;

        for (PersonType personWithLetter : personWithLetters) {
            if (unomsOfResettlementHouse.contains(personWithLetter.getUNOM())) {
                continue;
            }
            unomsOfResettlementHouse.add(personWithLetter.getUNOM());

            final RealEstateDataType realEstateByUnom = getRealEstateByUnom(personWithLetter.getUNOM());
            builder.addResettelmentAddress(realEstateByUnom.getAddress());
            int flatCount = realEstateByUnom.getFlats() != null ? realEstateByUnom.getFlats().getFlat().size() : 0;
            builder.addFlatCount(flatCount);
            int personCount = 0;
            int offerCount = 0;
            int demoCount = 0;
            int refusedCount = 0;
            int agreementCount = 0;
            int contractProjectCount = 0;
            int signedContractCount = 0;
            int keysIssueCount = 0;
            int releaseFlatCount = 0;
            int defectCount = 0;
            int agreeDefectCount = 0;
            for (PersonType personType : personWithLetters) {
                if (personType.getUNOM().equals(personWithLetter.getUNOM())) {
                    personCount++;
                    offerCount += personType.getOfferLetters().getOfferLetter().size();
                    demoCount +=
                            personType.getFlatDemo() != null ? personType.getFlatDemo().getFlatDemoItem().size() : 0;
                    refusedCount += personType.getAgreements() != null
                            ? personType.getAgreements()
                            .getAgreement()
                            .stream()
                            .filter(agreement -> EVENT_RESETTLEMENT_DECLINED.equals(agreement.getEvent()))
                            .count()
                            : 0;
                    agreementCount += personType.getAgreements() != null
                            ? personType.getAgreements()
                            .getAgreement()
                            .stream()
                            .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent()))
                            .count()
                            : 0;
                    contractProjectCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> "2".equals(c.getContractStatus()))
                            .count()
                            : 0;
                    signedContractCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> c.getContractSignDate() != null)
                            .count()
                            : 0;
                    keysIssueCount += personType.getKeysIssue() != null
                            && personType.getKeysIssue().getActDate() != null
                            ? 1
                            : 0;
                    releaseFlatCount += personType.getReleaseFlat() != null
                            && personType.getReleaseFlat().getActDate() != null
                            ? 1
                            : 0;
                    defectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Составлен акт осмотра квартиры".equals(d.getStatus()))
                            .count()
                            : 0;
                    agreeDefectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Подписан акт об устранении дефектов".equals(d.getStatus()))
                            .count()
                            : 0;
                }
            }
            flatCountResult += flatCount;
            personCountResult += personCount;
            offerCountResult += offerCount;
            demoCountResult += demoCount;
            refusedCountResult += refusedCount;
            agreementCountResult += agreementCount;
            contractProjectCountResult += contractProjectCount;
            signedContractCountResult += signedContractCount;
            keysIssueCountResult += keysIssueCount;
            releaseFlatCountResult += releaseFlatCount;
            defectCountResult += defectCount;
            agreeDefectCountResult += agreeDefectCount;

            builder.addPersonCount(personCount)
                    .addOfferLetterCount(offerCount)
                    .addDemoCount(demoCount)
                    .addRefusedCount(refusedCount)
                    .addAgreementCount(agreementCount)
                    .addContractProjectCount(contractProjectCount)
                    .addSignedContractCount(signedContractCount)
                    .addKeysIssueCount(keysIssueCount)
                    .addReleaseFlatCount(releaseFlatCount)
                    .addDefectCount(defectCount)
                    .addAgreeDefectCount(agreeDefectCount);
            builder.fillRowFirstTable(summaryAll.createRow(summaryAll.getLastRowNum() + 1));
        }
        builder.addResettelmentAddress("ИТОГО")
                .addFlatCount(flatCountResult)
                .addPersonCount(personCountResult)
                .addOfferLetterCount(offerCountResult)
                .addDemoCount(demoCountResult)
                .addRefusedCount(refusedCountResult)
                .addAgreementCount(agreementCountResult)
                .addContractProjectCount(contractProjectCountResult)
                .addSignedContractCount(signedContractCountResult)
                .addKeysIssueCount(keysIssueCountResult)
                .addReleaseFlatCount(releaseFlatCountResult)
                .addDefectCount(defectCountResult)
                .addAgreeDefectCount(agreeDefectCountResult);
        builder.fillRowFirstTable(summaryAll.createRow(summaryAll.getLastRowNum() + 1));

        final Set<BigInteger> unomsOfSettlementHouse = new HashSet<>();
        final Row secondHeader = summaryAll.createRow(summaryAll.getLastRowNum() + 8);
        RowSummaryReportBuilder.fillHeaderRowSecondTable(secondHeader);

        flatCountResult = 0;
        signedContractCountResult = 0;
        keysIssueCountResult = 0;
        defectCountResult = 0;
        agreeDefectCountResult = 0;

        for (PersonType data : personWithLetters) {
            if (data.getNewFlatInfo() == null || data.getNewFlatInfo().getNewFlat().size() == 0) {
                continue;
            }

            PersonType.NewFlatInfo.NewFlat newFlat = data.getNewFlatInfo().getNewFlat()
                    .get(data.getNewFlatInfo().getNewFlat().size() - 1);
            if (newFlat.getCcoUnom() == null || unomsOfSettlementHouse.contains(newFlat.getCcoUnom())) {
                continue;
            }
            unomsOfSettlementHouse.add(newFlat.getCcoUnom());

            Map<String, String> addressAndFlatCount = ccoService
                    .getCcoAddressAndFlatCountByUnom(newFlat.getCcoUnom().toString());
            builder.addSettlementAddress(addressAndFlatCount.get("address"));
            int flatCount = Integer.parseInt(addressAndFlatCount.getOrDefault("flatCount", "0"));
            builder.addSettlementFlatCount(flatCount);

            int signedContractCount = 0;
            int keysIssueCount = 0;
            int defectCount = 0;
            int agreeDefectCount = 0;
            for (PersonType personType : personWithLetters) {
                if (personType.getNewFlatInfo() != null && personType.getNewFlatInfo().getNewFlat()
                        .stream()
                        .anyMatch(ng -> newFlat.getCcoUnom().equals(ng.getCcoUnom()))) {
                    defectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Составлен акт осмотра квартиры".equals(d.getStatus()))
                            .count()
                            : 0;
                    agreeDefectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Подписан акт об устранении дефектов".equals(d.getStatus()))
                            .count()
                            : 0;
                    signedContractCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> c.getContractSignDate() != null)
                            .count()
                            : 0;
                    keysIssueCount += personType.getKeysIssue() != null
                            && personType.getKeysIssue().getActDate() != null
                            ? 1
                            : 0;
                }
            }
            flatCountResult += flatCount;
            signedContractCountResult += signedContractCount;
            keysIssueCountResult += keysIssueCount;
            defectCountResult += defectCount;
            agreeDefectCountResult += agreeDefectCount;

            builder.addSettlementDefectCount(defectCount)
                    .addSettlementAgreeDefectCount(agreeDefectCount)
                    .addSettlementSignedContractCount(signedContractCount)
                    .addSettlementKeysIssueCount(keysIssueCount);
            builder.fillRowSecondTable(summaryAll.createRow(summaryAll.getLastRowNum() + 1));
        }
        builder.addSettlementAddress("ИТОГО")
                .addSettlementFlatCount(flatCountResult)
                .addSettlementDefectCount(defectCountResult)
                .addSettlementAgreeDefectCount(agreeDefectCountResult)
                .addSettlementSignedContractCount(signedContractCountResult)
                .addSettlementKeysIssueCount(keysIssueCountResult);
        builder.fillRowSecondTable(summaryAll.createRow(summaryAll.getLastRowNum() + 1));

        autoSize(summaryAll, 13);
    }

    private void fillSheetSummaryOnDay(List<PersonType> personWithLettersOnDay, LocalDate date, Sheet summaryOnDay) {
        RowSummaryReportBuilder builder = beanFactory.getBean(RowSummaryReportBuilder.class);

        final Set<BigInteger> unoms = new HashSet<>();
        final Row header = summaryOnDay.createRow(0);
        RowSummaryReportBuilder.fillHeaderRowFirstTable(header);

        int flatCountResult = 0;
        int personCountResult = 0;
        int offerCountResult = 0;
        int demoCountResult = 0;
        int refusedCountResult = 0;
        int agreementCountResult = 0;
        int contractProjectCountResult = 0;
        int signedContractCountResult = 0;
        int keysIssueCountResult = 0;
        int releaseFlatCountResult = 0;
        int defectCountResult = 0;
        int agreeDefectCountResult = 0;

        for (PersonType type : personWithLettersOnDay) {
            if (unoms.contains(type.getUNOM())) {
                continue;
            }
            unoms.add(type.getUNOM());

            final RealEstateDataType realEstateByUnom = getRealEstateByUnom(type.getUNOM());
            builder.addResettelmentAddress(realEstateByUnom.getAddress());
            int flatCount = realEstateByUnom.getFlats() != null ? realEstateByUnom.getFlats().getFlat().size() : 0;
            builder.addFlatCount(flatCount);
            int personCount = 0;
            int offerCount = 0;
            int demoCount = 0;
            int refusedCount = 0;
            int agreementCount = 0;
            int contractProjectCount = 0;
            int signedContractCount = 0;
            int keysIssueCount = 0;
            int releaseFlatCount = 0;
            int defectCount = 0;
            int agreeDefectCount = 0;

            for (PersonType personType : personWithLettersOnDay) {
                if (personType.getUNOM().equals(type.getUNOM())) {
                    personCount++;
                    offerCount += personType.getOfferLetters()
                            .getOfferLetter()
                            .stream()
                            .filter(offerLetter -> date.equals(offerLetter.getDate()))
                            .count();
                    demoCount += personType.getFlatDemo() != null
                            ? personType.getFlatDemo()
                            .getFlatDemoItem()
                            .stream()
                            .filter(flatDemoItem -> date.equals(flatDemoItem.getDate()))
                            .count()
                            : 0;
                    refusedCount += personType.getAgreements() != null
                            ? personType.getAgreements()
                            .getAgreement()
                            .stream()
                            .filter(agreement -> EVENT_RESETTLEMENT_DECLINED.equals(agreement.getEvent())
                                    && date.equals(agreement.getDate()))
                            .count()
                            : 0;
                    agreementCount += personType.getAgreements() != null
                            ? personType.getAgreements()
                            .getAgreement()
                            .stream()
                            .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent())
                                    && date.equals(agreement.getDate()))
                            .count()
                            : 0;
                    contractProjectCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> "2".equals(c.getContractStatus()) && c.getMsgDateTime() != null
                                    && date.equals(c.getMsgDateTime().toLocalDate()))
                            .count()
                            : 0;
                    signedContractCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> date.equals(c.getContractSignDate()))
                            .count()
                            : 0;
                    keysIssueCount += personType.getKeysIssue() != null
                            && date.equals(personType.getKeysIssue().getActDate())
                            ? 1
                            : 0;
                    releaseFlatCount += personType.getReleaseFlat() != null
                            && date.equals(personType.getReleaseFlat().getActDate())
                            ? 1
                            : 0;
                    defectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Составлен акт осмотра квартиры".equals(d.getStatus())
                                    && date.equals(d.getDate()))
                            .count()
                            : 0;
                    agreeDefectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Подписан акт об устранении дефектов".equals(d.getStatus())
                                    && date.equals(d.getDate()))
                            .count()
                            : 0;
                }
            }
            flatCountResult += flatCount;
            personCountResult += personCount;
            offerCountResult += offerCount;
            demoCountResult += demoCount;
            refusedCountResult += refusedCount;
            agreementCountResult += agreementCount;
            contractProjectCountResult += contractProjectCount;
            signedContractCountResult += signedContractCount;
            keysIssueCountResult += keysIssueCount;
            releaseFlatCountResult += releaseFlatCount;
            defectCountResult += defectCount;
            agreeDefectCountResult += agreeDefectCount;

            builder.addPersonCount(personCount)
                    .addOfferLetterCount(offerCount)
                    .addDemoCount(demoCount)
                    .addRefusedCount(refusedCount)
                    .addAgreementCount(agreementCount)
                    .addContractProjectCount(contractProjectCount)
                    .addSignedContractCount(signedContractCount)
                    .addKeysIssueCount(keysIssueCount)
                    .addReleaseFlatCount(releaseFlatCount)
                    .addDefectCount(defectCount)
                    .addAgreeDefectCount(agreeDefectCount);
            builder.fillRowFirstTable(summaryOnDay.createRow(summaryOnDay.getLastRowNum() + 1));
        }
        builder.addResettelmentAddress("ИТОГО")
                .addFlatCount(flatCountResult)
                .addPersonCount(personCountResult)
                .addOfferLetterCount(offerCountResult)
                .addDemoCount(demoCountResult)
                .addRefusedCount(refusedCountResult)
                .addAgreementCount(agreementCountResult)
                .addContractProjectCount(contractProjectCountResult)
                .addSignedContractCount(signedContractCountResult)
                .addKeysIssueCount(keysIssueCountResult)
                .addReleaseFlatCount(releaseFlatCountResult)
                .addDefectCount(defectCountResult)
                .addAgreeDefectCount(agreeDefectCountResult);
        builder.fillRowFirstTable(summaryOnDay.createRow(summaryOnDay.getLastRowNum() + 1));

        final Set<BigInteger> unomsOfSettlementHouse = new HashSet<>();
        final Row secondHeader = summaryOnDay.createRow(summaryOnDay.getLastRowNum() + 8);
        RowSummaryReportBuilder.fillHeaderRowSecondTable(secondHeader);

        flatCountResult = 0;
        signedContractCountResult = 0;
        keysIssueCountResult = 0;
        defectCountResult = 0;
        agreeDefectCountResult = 0;

        for (PersonType data : personWithLettersOnDay) {
            if (data.getNewFlatInfo() == null || data.getNewFlatInfo().getNewFlat().size() == 0) {
                continue;
            }
            PersonType.NewFlatInfo.NewFlat newFlat = data.getNewFlatInfo().getNewFlat()
                    .get(data.getNewFlatInfo().getNewFlat().size() - 1);
            if (newFlat.getCcoUnom() == null || unomsOfSettlementHouse.contains(newFlat.getCcoUnom())) {
                continue;
            }
            unomsOfSettlementHouse.add(newFlat.getCcoUnom());

            Map<String, String> addressAndFlatCount = ccoService
                    .getCcoAddressAndFlatCountByUnom(newFlat.getCcoUnom().toString());
            builder.addSettlementAddress(addressAndFlatCount.get("address"));
            int flatCount = Integer.parseInt(addressAndFlatCount.getOrDefault("flatCount", "0"));
            builder.addSettlementFlatCount(flatCount);

            int signedContractCount = 0;
            int keysIssueCount = 0;
            int defectCount = 0;
            int agreeDefectCount = 0;
            for (PersonType personType : personWithLettersOnDay) {
                if (personType.getNewFlatInfo() != null && personType.getNewFlatInfo().getNewFlat()
                        .stream()
                        .anyMatch(ng -> newFlat.getCcoUnom().equals(ng.getCcoUnom()))) {
                    signedContractCount += personType.getContracts() != null
                            ? personType.getContracts()
                            .getContract()
                            .stream()
                            .filter(c -> date.equals(c.getContractSignDate()))
                            .count()
                            : 0;
                    keysIssueCount += personType.getKeysIssue() != null
                            && date.equals(personType.getKeysIssue().getActDate())
                            ? 1
                            : 0;
                    defectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Составлен акт осмотра квартиры".equals(d.getStatus())
                                    && date.equals(d.getDate()))
                            .count()
                            : 0;
                    agreeDefectCount += personType.getResettlementHistory() != null
                            ? personType.getResettlementHistory()
                            .stream()
                            .filter(d -> "Подписан акт об устранении дефектов".equals(d.getStatus())
                                    && date.equals(d.getDate()))
                            .count()
                            : 0;
                }
            }
            flatCountResult += flatCount;
            signedContractCountResult += signedContractCount;
            keysIssueCountResult += keysIssueCount;
            defectCountResult += defectCount;
            agreeDefectCountResult += agreeDefectCount;

            builder.addSettlementDefectCount(defectCount)
                    .addSettlementAgreeDefectCount(agreeDefectCount)
                    .addSettlementSignedContractCount(signedContractCount)
                    .addSettlementKeysIssueCount(keysIssueCount);
            builder.fillRowSecondTable(summaryOnDay.createRow(summaryOnDay.getLastRowNum() + 1));
        }
        builder.addSettlementAddress("ИТОГО")
                .addSettlementFlatCount(flatCountResult)
                .addSettlementDefectCount(defectCountResult)
                .addSettlementAgreeDefectCount(agreeDefectCountResult)
                .addSettlementSignedContractCount(signedContractCountResult)
                .addSettlementKeysIssueCount(keysIssueCountResult);
        builder.fillRowSecondTable(summaryOnDay.createRow(summaryOnDay.getLastRowNum() + 1));

        autoSize(summaryOnDay, 13);
    }

    private void fillSheetFioAll(List<PersonType> personWithLetters, Sheet fioAll) {
        final Row header = fioAll.createRow(0);
        RowFioReportBuilder.createHeader(header);

        personWithLetters.forEach(data -> {
            RowFioReportBuilder builder = beanFactory.getBean(RowFioReportBuilder.class);
            builder.addRessettelmentAddresse(getRealEstateByUnom(data.getUNOM()).getAddress())
                    .addFlatNum(data.getFlatNum())
                    .addRoomNum(data.getRoomNum())
                    .addFio(data.getFIO())
                    .addAffairId(data.getAffairId());
            data.getOfferLetters()
                    .getOfferLetter()
                    .stream()
                    .sorted(Comparator.comparing(OfferLetter::getDate, Comparator.nullsFirst(LocalDate::compareTo))
                            .reversed())
                    .forEach(offerLetter -> {
                        builder.addOfferLetter(offerLetter);

                        final List<FlatDemoItem> flatDemoItems = getFlatDemoItems(data, offerLetter);
                        final List<Agreement> agreements = getAgreements(data, offerLetter);
                        final List<ResettlementHistory> acts = getActs(data);
                        final List<PersonType.Contracts.Contract> contractProjects = getContractProjects(data);
                        final List<PersonType.NewFlatInfo.NewFlat> newFlats = getNewFlats(data);

                        builder.addDemoCount(flatDemoItems.size());
                        String demoDates = flatDemoItems.stream()
                                .filter(fd -> fd.getDate() != null)
                                .map(FlatDemoItem::getDate)
                                .sorted(Comparator.reverseOrder())
                                .map(d -> d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                .collect(Collectors.joining(";"));
                        builder.addDemoDates(demoDates);
                        if (agreements.size() > 0) {
                            builder.addAgreementDate(agreements.get(agreements.size() - 1).getDate());
                            builder.addAgreementType(agreements.get(agreements.size() - 1).getEvent());
                        } else {
                            builder.addAgreementDate(null);
                            builder.addAgreementType(null);
                        }
                        boolean hasAgreement = agreements.stream()
                            .anyMatch(a -> EVENT_RESETTLEMENT_ACCEPTED.equals(a.getEvent()));
                        if (hasAgreement && acts.size() > 0) {
                            String actDates = acts.stream()
                                    .filter(a -> a.getDate() != null)
                                    .map(ResettlementHistory::getDate)
                                    .sorted(Comparator.reverseOrder())
                                    .map(d -> d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                                    .collect(Collectors.joining(";"));
                            builder.addAct(actDates);
                        } else {
                            builder.addAct(null);
                        }
                        if (hasAgreement && contractProjects.size() > 0) {
                            PersonType.Contracts.Contract contract = contractProjects.get(0);
                            builder.addContractProject(contract.getMsgDateTime());

                            if (contract.getContractSignDate() != null) {
                                builder.addSignedContract(contract.getContractSignDate());
                            } else {
                                builder.addSignedContract(null);
                            }
                        } else {
                            builder.addContractProject(null);
                            builder.addSignedContract(null);
                        }
                        if (hasAgreement && newFlats.size() > 0) {
                            PersonType.NewFlatInfo.NewFlat newFlat = newFlats.get(0);
                            if (newFlat.getCcoUnom() != null) {
                                String unom = newFlat.getCcoUnom().toString();
                                String address = ccoService.getCcoAddressByUnom(unom);
                                if (StringUtils.isNotBlank(address)) {
                                    builder.addNewFlat(address + ", кв. " + newFlat.getCcoFlatNum());
                                } else {
                                    builder.addNewFlat(null);
                                }
                            } else {
                                builder.addNewFlat(null);
                            }
                        } else {
                            builder.addNewFlat(null);
                        }
                        if (hasAgreement && data.getKeysIssue() != null) {
                            builder.addKeysIssue(data.getKeysIssue().getActDate());
                        } else {
                            builder.addKeysIssue(null);
                        }
                        if (hasAgreement && data.getReleaseFlat() != null) {
                            builder.addReleaseFlat(data.getReleaseFlat().getActDate());
                        } else {
                            builder.addReleaseFlat(null);
                        }

                        final Row row = fioAll.createRow(fioAll.getLastRowNum() + 1);
                        builder.fillRow(row);
                    });

        });

        autoSize(fioAll, 16);

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

    private void fillSheetFioOnDay(List<PersonType> personWithLettersOnDay, LocalDate date, Sheet fioOnDay) {
        final Row header = fioOnDay.createRow(0);
        RowFioReportBuilder.createHeader(header);

        personWithLettersOnDay.forEach(data -> {
            RowFioReportBuilder builder = beanFactory.getBean(RowFioReportBuilder.class);
            builder.addRessettelmentAddresse(getRealEstateByUnom(data.getUNOM()).getAddress())
                    .addFlatNum(data.getFlatNum())
                    .addRoomNum(data.getRoomNum())
                    .addFio(data.getFIO())
                    .addAffairId(data.getAffairId());
            getLetterOnDay(data, date).stream()
                    .sorted(Comparator.comparing(OfferLetter::getDate, Comparator.nullsFirst(LocalDate::compareTo))
                            .reversed())
                    .forEach(offerLetter -> {
                        builder.addOfferLetter(offerLetter);

                        final List<FlatDemoItem> flatDemoItems = getFlatDemoItems(data, offerLetter, date);
                        final List<Agreement> agreements = getAgreements(data, offerLetter, date);
                        final List<Agreement> allAgreements = getAgreements(data, offerLetter);
                        final List<ResettlementHistory> acts = getActs(data, date);
                        final List<PersonType.Contracts.Contract> contractProjects = getContractProjects(data, date);
                        final List<PersonType.NewFlatInfo.NewFlat> newFlats = getNewFlats(data, date);

                        builder.addDemoCount(flatDemoItems.size());
                        if (flatDemoItems.size() > 0 && flatDemoItems.get(flatDemoItems.size() - 1).getDate() != null) {
                            builder.addDemoDates(
                                    flatDemoItems.get(flatDemoItems.size() - 1).getDate()
                                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            );
                        } else {
                            builder.addDemoDates(null);
                        }
                        if (agreements.size() > 0) {
                            builder.addAgreementDate(agreements.get(agreements.size() - 1).getDate());
                            builder.addAgreementType(agreements.get(agreements.size() - 1).getEvent());
                        } else {
                            builder.addAgreementDate(null);
                            builder.addAgreementType(null);
                        }
                        boolean hasAgreement = allAgreements.stream()
                            .anyMatch(a -> EVENT_RESETTLEMENT_ACCEPTED.equals(a.getEvent()));
                        if (hasAgreement && acts.size() > 0) {
                            if (acts.size() > 1) {
                                builder.addAct(acts.size() + " акта от " + dateFormatter.format(acts.get(0).getDate()));
                            } else {
                                builder.addAct(dateFormatter.format(acts.get(0).getDate()));
                            }
                        } else {
                            builder.addAct(null);
                        }
                        if (hasAgreement && contractProjects.size() > 0) {
                            PersonType.Contracts.Contract contract = contractProjects.get(0);
                            builder.addContractProject(contract.getMsgDateTime());
                            if (date.equals(contract.getContractSignDate())) {
                                builder.addSignedContract(contract.getContractSignDate());
                            } else {
                                builder.addSignedContract(null);
                            }
                        } else {
                            builder.addContractProject(null);
                            builder.addSignedContract(null);
                        }
                        if (hasAgreement && newFlats.size() > 0) {
                            PersonType.NewFlatInfo.NewFlat newFlat = newFlats.get(0);
                            if (newFlat.getCcoUnom() != null) {
                                String unom = newFlat.getCcoUnom().toString();
                                String address = ccoService.getCcoAddressByUnom(unom);
                                if (StringUtils.isNotBlank(address)) {
                                    builder.addNewFlat(address + ", кв. " + newFlat.getCcoFlatNum());
                                } else {
                                    builder.addNewFlat(null);
                                }
                            } else {
                                builder.addNewFlat(null);
                            }
                        } else {
                            builder.addNewFlat(null);
                        }
                        if (hasAgreement && data.getKeysIssue() != null && date != null
                                && date.equals(data.getKeysIssue().getActDate())) {
                            builder.addKeysIssue(data.getKeysIssue().getActDate());
                        } else {
                            builder.addKeysIssue(null);
                        }
                        if (hasAgreement && data.getReleaseFlat() != null && date != null
                                && date.equals(data.getReleaseFlat().getActDate())) {
                            builder.addReleaseFlat(data.getReleaseFlat().getActDate());
                        } else {
                            builder.addReleaseFlat(null);
                        }

                        final Row row = fioOnDay.createRow(fioOnDay.getLastRowNum() + 1);
                        builder.fillRow(row);
                    });

        });

        autoSize(fioOnDay, 16);

    }

    @NotNull
    private List<Agreement> getAgreements(PersonType data, OfferLetter offerLetter, LocalDate date) {
        if (data.getAgreements() != null) {
            return data.getAgreements()
                    .getAgreement()
                    .stream()
                    .filter(agreement -> EVENT_RESETTLEMENT_ACCEPTED.equals(agreement.getEvent())
                        || EVENT_RESETTLEMENT_DECLINED.equals(agreement.getEvent()))
                    .filter(agreement -> Objects.equals(agreement.getLetterId(), offerLetter.getLetterId())
                            && (date == null || date.equals(agreement.getDate())))
                    .sorted(Comparator.comparing(Agreement::getDate, Comparator.nullsFirst(LocalDate::compareTo))
                            .reversed())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @NotNull
    private List<Agreement> getAgreements(PersonType data, OfferLetter offerLetter) {
        return getAgreements(data, offerLetter, null);
    }

    @NotNull
    private List<FlatDemoItem> getFlatDemoItems(PersonType data, OfferLetter offerLetter, LocalDate date) {
        if (data.getFlatDemo() != null) {
            return data.getFlatDemo()
                    .getFlatDemoItem()
                    .stream()
                    .filter(flatDemoItem -> Objects.equals(flatDemoItem.getLetterId(), (offerLetter.getLetterId()))
                            && (date == null || date.equals(flatDemoItem.getDate())))
                    .sorted(Comparator.comparing(FlatDemoItem::getDate, Comparator.nullsFirst(LocalDate::compareTo))
                            .reversed())
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();

    }

    @NotNull
    private List<FlatDemoItem> getFlatDemoItems(PersonType data, OfferLetter offerLetter) {
        return getFlatDemoItems(data, offerLetter, null);
    }

    @NotNull
    private List<ResettlementHistory> getActs(PersonType data, LocalDate date) {
        return data.getResettlementHistory()
                .stream()
                .filter(h -> "Составлен акт осмотра квартиры".equals(h.getStatus())
                        && (date == null || date.equals(h.getDate()))
                ).sorted(
                        Comparator.comparing(
                                ResettlementHistory::getDate,
                                Comparator.nullsFirst(LocalDate::compareTo)
                        ).reversed()
                )
                .collect(Collectors.toList());
    }

    @NotNull
    private List<ResettlementHistory> getActs(PersonType data) {
        return getActs(data, null);
    }

    @NotNull
    private List<PersonType.Contracts.Contract> getContractProjects(PersonType data, LocalDate date) {
        if (data.getContracts() != null) {
            return data.getContracts()
                    .getContract()
                    .stream()
                    .filter(contract -> "2".equals(contract.getContractStatus())
                            && (date == null || contract.getMsgDateTime() != null
                            && date.equals(contract.getMsgDateTime().toLocalDate())))
                    .sorted(
                            Comparator.comparing(
                                    PersonType.Contracts.Contract::getMsgDateTime,
                                    Comparator.nullsFirst(LocalDateTime::compareTo)
                            ).reversed()
                    )
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @NotNull
    private List<PersonType.Contracts.Contract> getContractProjects(PersonType data) {
        return getContractProjects(data, null);
    }

    @NotNull
    private List<PersonType.NewFlatInfo.NewFlat> getNewFlats(PersonType data, LocalDate date) {
        if (data.getNewFlatInfo() != null) {
            return data.getNewFlatInfo()
                    .getNewFlat()
                    .stream()
                    .filter(f -> date == null || f.getMsgDateTime() != null
                            && date.equals(f.getMsgDateTime().toLocalDate()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @NotNull
    private List<PersonType.NewFlatInfo.NewFlat> getNewFlats(PersonType data) {
        return getNewFlats(data, null);
    }

    private RealEstateDataType getRealEstateByUnom(BigInteger unom) {
        final String key = unom.toString();
        if (!realEstateData.containsKey(key)) {
            realEstateData.put(key,
                    realEstateDocumentService.fetchDocumentByUnom(unom.toString()).getDocument().getRealEstateData());
        }
        return realEstateData.get(key);
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private List<OfferLetter> getLetterOnDay(PersonType personType, LocalDate date) {
        return personType.getOfferLetters().getOfferLetter()
                .stream()
                .filter(offerLetter ->
                        date.equals(offerLetter.getDate())
                                || !getAgreements(personType, offerLetter, date).isEmpty()
                                || !getFlatDemoItems(personType, offerLetter, date).isEmpty()
                                || !getActs(personType, date).isEmpty()
                                || !getContractProjects(personType, date).isEmpty()
                                || !getNewFlats(personType, date).isEmpty()
                )
                .collect(Collectors.toList());
    }
}
