package ru.croc.ugd.ssr.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.dashboard.DashboardAnalyticsService;

/**
 * Контроллер для ручного вызова подсчета отдельных показателей для дашборда.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-analytics")
public class DashboardAnalyticsController {

    private final DashboardAnalyticsService dashboardAnalyticsService;

    /**
     * Получить Равнозначных квартир представлено гражданам.
     *
     * @return Равнозначных квартир представлено гражданам.
     */
    @ApiOperation("Получить Равнозначных квартир представлено гражданам")
    @GetMapping("/getEquivalentApartmentsAmount")
    public Integer getEquivalentApartmentsAmount() {
        return dashboardAnalyticsService.getEquivalentApartmentsAmount();
    }

    /**
     * Получить Согласия на равнозначные квартиры.
     *
     * @return Согласия на равнозначные квартиры.
     */
    @ApiOperation("Получить Согласия на равнозначные квартиры.")
    @GetMapping("/getEquivalentApartmentConsentedAmount")
    public Integer getEquivalentApartmentConsentedAmount() {
        return dashboardAnalyticsService.getEquivalentApartmentConsentedAmount();
    }

    /**
     * Получить В том числе семей - переехали.
     *
     * @return В том числе семей - переехали.
     */
    @ApiOperation("Получить В том числе семей - переехали.")
    @GetMapping("/getResettledFamiliesAmount")
    public Integer getResettledFamiliesAmount() {
        return dashboardAnalyticsService.getResettledFamiliesAmount();
    }

    /**
     * Получить ДГИ предлагает равнозначные квартиры.
     *
     * @return ДГИ предлагает равнозначные квартиры.
     */
    @ApiOperation("Получить ДГИ предлагает равнозначные квартиры.")
    @GetMapping("/getEquivalentApartmentProposedAmount")
    public Integer getEquivalentApartmentProposedAmount() {
        return dashboardAnalyticsService.getEquivalentApartmentProposedAmount();
    }

    /**
     * Получить Количество семей, всего.
     *
     * @return Количество семей, всего.
     */
    @ApiOperation("Получить Количество семей, всего.")
    @GetMapping("/getFamiliesAmount")
    public Integer getFamiliesAmount() {
        return dashboardAnalyticsService.getFamiliesAmount();
    }

    /**
     * Получить Количество человек - переехали.
     *
     * @return Количество человек - переехали.
     */
    @ApiOperation("Получить Количество человек - переехали.")
    @GetMapping("/getResettledResidentsAmount")
    public Integer getResettledResidentsAmount() {
        return dashboardAnalyticsService.getResettledResidentsAmount();
    }

    /**
     * Получить Количество человек - переехали.
     *
     * @return количество УНОМ.
     */
    @ApiOperation("Получить количество уникальных УНОМ домов, переданных под заселение.")
    @GetMapping("/getNewBuildingsTotalAmount")
    public Integer getNewBuildingsTotalAmount() {
        return dashboardAnalyticsService.getNewBuildingsTotalAmount();
    }

    /**
     * Получить Всего квартир в новостройках.
     *
     * @return Всего квартир в новостройках
     */
    @ApiOperation("Получить Всего квартир в новостройках.")
    @GetMapping("/getFlatsTotalAmount")
    public Integer getFlatsTotalAmount() {
        return dashboardAnalyticsService.getFlatsTotalAmount();
    }

    /**
     * Получить Ведется отселение, завершение планируется в указанном периоде.
     *
     * @return Ведется отселение, завершение планируется в указанном периоде.
     */
    @ApiOperation("Получить Ведется отселение, завершение планируется в указанном периоде.")
    @GetMapping("/getResettlementInProcessAmount")
    public Integer getResettlementInProcessAmount() {
        return dashboardAnalyticsService.getResettlementInProcessAmount();
    }

    /**
     * Получить Период планируемого завершения отселения.
     *
     * @return Период планируемого завершения отселения.
     */
    @ApiOperation("Получить Период планируемого завершения отселения.")
    @GetMapping("/getPlannedResettlementCompletionTime")
    public String getPlannedResettlementCompletionTime() {
        return dashboardAnalyticsService.getPlannedResettlementCompletionTime();
    }

    /**
     * Получить Ведется предложение равнозначных квартир жителям.
     *
     * @return Ведется предложение равнозначных квартир жителям.
     */
    @ApiOperation("Получить Ведется предложение равнозначных квартир жителям.")
    @GetMapping("/getOfferedEquivalentApartmentsAmount")
    public Integer getOfferedEquivalentApartmentsAmount() {
        return dashboardAnalyticsService.getOfferedEquivalentApartmentsAmount();
    }

    /**
     * Получить количество уникальных УНОМ отселяемых домов.
     *
     * @return количество УНОМ.
     */
    @ApiOperation("Получить количество уникальных УНОМ отселяемых домов.")
    @GetMapping("/getAllBuildingsAmount")
    public Integer getAllBuildingsAmount() {
        return dashboardAnalyticsService.getAllBuildingsAmount();
    }

    /**
     * Получить ДГИ завершил оформление документов жителям,
     * остались вопросы Фонда (предоставление квартир с компенсацией за меньшую площадь).
     *
     * @return ДГИ завершил оформление документов жителям.
     */
    @ApiOperation("Получить ДГИ завершил оформление документов жителям, "
        + "остались вопросы Фонда (предоставление квартир с компенсацией за меньшую площадь).")
    @GetMapping("/getPaperworkCompletedAndFundIssuesAmount")
    public Integer getPaperworkCompletedAndFundIssuesAmount() {
        return dashboardAnalyticsService.getPaperworkCompletedAndFundIssuesAmount();
    }

    /**
     * Получить Приобретены гражданами в порядке докупки.
     *
     * @return Приобретены гражданами в порядке докупки.
     */
    @ApiOperation("Получить Приобретены гражданами в порядке докупки.")
    @GetMapping("/getBuyInApartmentsAmount")
    public Integer getBuyInApartmentsAmount() {
        return dashboardAnalyticsService.getBuyInApartmentsAmount();
    }

    /**
     * Получить Согласия на компенсацию и докупку.
     *
     * @return Согласия на компенсацию и докупку.
     */
    @ApiOperation("Получить Согласия на компенсацию и докупку.")
    @GetMapping("/getApartmentWithCompensationAmount")
    public Integer getApartmentWithCompensationAmount() {
        return dashboardAnalyticsService.getApartmentWithCompensationAmount();
    }

    /**
     * Получить Представлены с компенсацией за меньшую жилую площадь.
     *
     * @return Представлены с компенсацией за меньшую жилую площадь.
     */
    @ApiOperation("Получить Представлены с компенсацией за меньшую жилую площадь.")
    @GetMapping("/getApartmentsWithCompensationAmount")
    public Integer getApartmentsWithCompensationAmount() {
        return dashboardAnalyticsService.getApartmentsWithCompensationAmount();
    }

    /**
     * Получить Получить Жители отселены, завершается изъятие нежилых помещений.
     * @return Получить Жители отселены, завершается изъятие нежилых помещений.
     */
    @ApiOperation("Получить Жители отселены, завершается изъятие нежилых помещений.")
    @GetMapping("/getResidentsResettledAmount")
    public Integer getResidentsResettledAmount() {
        return dashboardAnalyticsService.getResidentsResettledAmount();
    }

    /**
     * Получить Ведется частичное переселение.
     * @return Ведется частичное переселение.
     */
    @ApiOperation("Получить Ведется частичное переселение.")
    @GetMapping("/getPartResettlementInProcessAmount")
    public Integer getPartResettlementInProcessAmount() {
        return dashboardAnalyticsService.getPartResettlementInProcessAmount();
    }

    /**
     * Получить Ведется частичное переселение.
     * @return Ведется частичное переселение.
     */
    @ApiOperation("Получить Отселены, готовятся к сносу.")
    @GetMapping("/getPreparingForDemolitionAmount")
    public Integer getPreparingForDemolitionAmount() {
        return dashboardAnalyticsService.getPreparingForDemolitionAmount();
    }

    /**
     * Получить Снесены.
     * @return Снесены.
     */
    @ApiOperation("Получить Снесены.")
    @GetMapping("/getDemolishedAmount")
    public Integer getDemolishedAmount() {
        return dashboardAnalyticsService.getDemolishedAmount();
    }

    /**
     * Получить Расселен, сохраняемый дом.
     * @return Расселен, сохраняемый дом.
     */
    @ApiOperation("Получить Расселен, сохраняемый дом.")
    @GetMapping("/getPreservedAmount")
    public Integer getPreservedAmount() {
        return dashboardAnalyticsService.getPreservedAmount();
    }

    /**
     * Получить Расселен, сохраняемый дом.
     * @return Расселен, сохраняемый дом.
     */
    @ApiOperation("Получить Фонд предлагает компенсацию (нет равнозначных).")
    @GetMapping("/getApartmentWithCompensationProposedAmount")
    public Integer getApartmentWithCompensationProposedAmount() {
        return dashboardAnalyticsService.getApartmentWithCompensationProposedAmount();
    }

    /**
     * Получить Отказы, суды.
     * @return Отказы, суды.
     */
    @ApiOperation("Получить Отказы, суды.")
    @GetMapping("/getRefusedAmount")
    public Integer getRefusedAmount() {
        return dashboardAnalyticsService.getRefusedAmount();
    }

    /**
     * Получить Ведется предложение квартир с компенсацией за меньшую жилую площадь.
     * @return Ведется предложение квартир с компенсацией за меньшую жилую площадь.
     */
    @ApiOperation("Получить Ведется предложение квартир с компенсацией за меньшую жилую площадь.")
    @GetMapping("/getOfferedApartmentsWithCompensationAmount")
    public Integer getOfferedApartmentsWithCompensationAmount() {
        return dashboardAnalyticsService.getOfferedApartmentsWithCompensationAmount();
    }

    /**
     * Получить Переселение вне района проживания (включены в Реновацию + ведется изъятие).
     * @return Переселение вне района проживания (включены в Реновацию + ведется изъятие).
     */
    @ApiOperation("Получить Переселение вне района проживания (включены в Реновацию + ведется изъятие).")
    @GetMapping("/getOutsideResidenceResettlementAmount")
    public Integer getOutsideResidenceResettlementAmount() {
        return dashboardAnalyticsService.getOutsideResidenceResettlementAmount();
    }
}
