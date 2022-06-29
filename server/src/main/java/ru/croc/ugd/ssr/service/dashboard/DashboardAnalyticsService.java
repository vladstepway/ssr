package ru.croc.ugd.ssr.service.dashboard;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.db.dao.DashboardAnalyticsDao;

/**
 * Сервис для работы с данными из вьюх в схеме analytics.
 */
@Service
public class DashboardAnalyticsService {

    private final DashboardAnalyticsDao dao;

    public DashboardAnalyticsService(DashboardAnalyticsDao dao) {
        this.dao = dao;
    }

    /**
     * Получить Равнозначных квартир представлено гражданам.
     *
     * @return Равнозначных квартир представлено гражданам.
     */
    public Integer getEquivalentApartmentsAmount() {
        Integer equivalentApartmentsAmount = dao.getEquivalentApartmentsAmount();
        if (isNull(equivalentApartmentsAmount)) {
            return 0;
        }
        return equivalentApartmentsAmount;
    }

    /**
     * Получить Согласия на равнозначные квартиры.
     *
     * @return Согласия на равнозначные квартиры.
     */
    public Integer getEquivalentApartmentConsentedAmount() {
        Integer equivalentApartmentConsentedAmount = dao.getEquivalentApartmentConsentedAmount();
        if (isNull(equivalentApartmentConsentedAmount)) {
            return 0;
        }
        return equivalentApartmentConsentedAmount;
    }

    /**
     * Получить В том числе семей - переехали.
     *
     * @return В том числе семей - переехали.
     */
    public Integer getResettledFamiliesAmount() {
        Integer resettledFamiliesAmount = dao.getResettledFamiliesAmount();
        if (isNull(resettledFamiliesAmount)) {
            return 0;
        }
        return resettledFamiliesAmount;
    }

    /**
     * Получить ДГИ предлагает равнозначные квартиры.
     *
     * @return ДГИ предлагает равнозначные квартиры.
     */
    public Integer getEquivalentApartmentProposedAmount() {
        Integer equivalentApartmentProposedAmount = dao.getEquivalentApartmentProposedAmount();
        if (isNull(equivalentApartmentProposedAmount)) {
            return 0;
        }
        return equivalentApartmentProposedAmount;
    }

    /**
     * Получить Количество семей, всего.
     *
     * @return Количество семей, всего.
     */
    public Integer getFamiliesAmount() {
        Integer familiesAmount = dao.getFamiliesAmount();
        if (isNull(familiesAmount)) {
            return 0;
        }
        return familiesAmount;
    }

    /**
     * Получить Количество человек - переехали.
     *
     * @return Количество человек - переехали.
     */
    public Integer getResettledResidentsAmount() {
        Integer resettledResidentsAmount = dao.getResettledResidentsAmount();
        if (isNull(resettledResidentsAmount)) {
            return 0;
        }
        return resettledResidentsAmount;
    }

    /**
     * Получить количество уникальных УНОМ домов, переданных под заселение.
     *
     * @return количество УНОМ.
     */
    public Integer getNewBuildingsTotalAmount() {
        Integer newBuildingsTotalAmount = dao.getNewBuildingsTotalAmount();
        if (isNull(newBuildingsTotalAmount)) {
            return 0;
        }
        return newBuildingsTotalAmount;
    }

    /**
     * Получить Всего квартир в новостройках.
     *
     * @return Всего квартир в новостройках
     */
    public Integer getFlatsTotalAmount() {
        Integer flatsTotalAmount = dao.getFlatsTotalAmount();
        if (isNull(flatsTotalAmount)) {
            return 0;
        }
        return flatsTotalAmount;
    }

    /**
     * Получить Ведется отселение, завершение планируется в указанном периоде.
     *
     * @return Ведется отселение, завершение планируется в указанном периоде.
     */
    public Integer getResettlementInProcessAmount() {
        Integer resettlementInProcessAmount = dao.getResettlementInProcessAmount();
        if (isNull(resettlementInProcessAmount)) {
            return 0;
        }
        return resettlementInProcessAmount;
    }

    /**
     * Получить Период планируемого завершения отселения.
     *
     * @return Период планируемого завершения отселения.
     */
    public String getPlannedResettlementCompletionTime() {
        return dao.getPlannedResettlementCompletionTime();
    }

    /**
     * Получить Ведется предложение равнозначных квартир жителям.
     *
     * @return Ведется предложение равнозначных квартир жителям.
     */
    public Integer getOfferedEquivalentApartmentsAmount() {
        Integer offeredEquivalentApartmentsAmount = dao.getOfferedEquivalentApartmentsAmount();
        if (isNull(offeredEquivalentApartmentsAmount)) {
            return 0;
        }
        return offeredEquivalentApartmentsAmount;
    }

    /**
     * Получить количество уникальных УНОМ отселяемых домов.
     *
     * @return количество УНОМ.
     */
    public Integer getAllBuildingsAmount() {
        Integer allBuildingsAmount = dao.getAllBuildingsAmount();
        if (isNull(allBuildingsAmount)) {
            return 0;
        }
        return allBuildingsAmount;
    }

    /**
     * Получить ДГИ завершил оформление документов жителям,
     * остались вопросы Фонда (предоставление квартир с компенсацией за меньшую площадь).
     *
     * @return ДГИ завершил оформление документов жителям.
     */
    public Integer getPaperworkCompletedAndFundIssuesAmount() {
        Integer paperworkCompletedAndFundIssuesAmount = dao.getPaperworkCompletedAndFundIssuesAmount();
        if (isNull(paperworkCompletedAndFundIssuesAmount)) {
            return 0;
        }
        return paperworkCompletedAndFundIssuesAmount;
    }

    /**
     * Получить Приобретены гражданами в порядке докупки.
     *
     * @return Приобретены гражданами в порядке докупки.
     */
    public Integer getBuyInApartmentsAmount() {
        Integer buyInApartmentsAmount = dao.getBuyInApartmentsAmount();
        if (isNull(buyInApartmentsAmount)) {
            return 0;
        }
        return buyInApartmentsAmount;
    }

    /**
     * Получить Согласия на компенсацию и докупку.
     *
     * @return Согласия на компенсацию и докупку.
     */
    public Integer getApartmentWithCompensationAmount() {
        Integer apartmentWithCompensationAmount = dao.getApartmentWithCompensationAmount();
        if (isNull(apartmentWithCompensationAmount)) {
            return 0;
        }
        return apartmentWithCompensationAmount;
    }

    /**
     * Получить Представлены с компенсацией за меньшую жилую площадь.
     *
     * @return Представлены с компенсацией за меньшую жилую площадь.
     */
    public Integer getApartmentsWithCompensationAmount() {
        Integer apartmentsWithCompensationAmount = dao.getApartmentsWithCompensationAmount();
        if (isNull(apartmentsWithCompensationAmount)) {
            return 0;
        }
        return apartmentsWithCompensationAmount;
    }

    /**
     * Получить Получить Жители отселены, завершается изъятие нежилых помещений.
     * @return Получить Жители отселены, завершается изъятие нежилых помещений.
     */
    public Integer getResidentsResettledAmount() {
        Integer residentsResettledAmount = dao.getResidentsResettledAmount();
        if (isNull(residentsResettledAmount)) {
            return 0;
        }
        return residentsResettledAmount;
    }

    /**
     * Получить Ведется частичное переселение.
     * @return Ведется частичное переселение.
     */
    public Integer getPartResettlementInProcessAmount() {
        Integer partResettlementInProcessAmount = dao.getPartResettlementInProcessAmount();
        if (isNull(partResettlementInProcessAmount)) {
            return 0;
        }
        return partResettlementInProcessAmount;
    }

    /**
     * Получить Отселены, готовятся к сносу.
     * @return Отселены, готовятся к сносу.
     */
    public Integer getPreparingForDemolitionAmount() {
        Integer preparingForDemolitionAmount = dao.getPreparingForDemolitionAmount();
        if (isNull(preparingForDemolitionAmount)) {
            return 0;
        }
        return preparingForDemolitionAmount;
    }

    /**
     * Получить Снесены.
     * @return Снесены.
     */
    public Integer getDemolishedAmount() {
        Integer demolishedAmount = dao.getDemolishedAmount();
        if (isNull(demolishedAmount)) {
            return 0;
        }
        return demolishedAmount;
    }

    /**
     * Получить Расселен, сохраняемый дом.
     * @return Расселен, сохраняемый дом.
     */
    public Integer getPreservedAmount() {
        Integer preservedAmount = dao.getPreservedAmount();
        if (isNull(preservedAmount)) {
            return 0;
        }
        return preservedAmount;
    }

    /**
     * Получить Фонд предлагает компенсацию (нет равнозначных).
     * @return Фонд предлагает компенсацию (нет равнозначных).
     */
    public Integer getApartmentWithCompensationProposedAmount() {
        Integer apartmentWithCompensationProposedAmount = dao.getApartmentWithCompensationProposedAmount();
        if (isNull(apartmentWithCompensationProposedAmount)) {
            return 0;
        }
        return apartmentWithCompensationProposedAmount;
    }

    /**
     * Получить Отказы, суды.
     * @return Отказы, суды.
     */
    public Integer getRefusedAmount() {
        Integer refusedAmount = dao.getRefusedAmount();
        if (isNull(refusedAmount)) {
            return 0;
        }
        return refusedAmount;
    }

    /**
     * Получить Ведется предложение квартир с компенсацией за меньшую жилую площадь.
     * @return Ведется предложение квартир с компенсацией за меньшую жилую площадь.
     */
    public Integer getOfferedApartmentsWithCompensationAmount() {
        Integer offeredApartmentsWithCompensationAmount = dao.getOfferedApartmentsWithCompensationAmount();
        if (isNull(offeredApartmentsWithCompensationAmount)) {
            return 0;
        }
        return offeredApartmentsWithCompensationAmount;
    }

    /**
     * Получить Переселение вне района проживания (включены в Реновацию + ведется изъятие).
     * @return Переселение вне района проживания (включены в Реновацию + ведется изъятие).
     */
    public Integer getOutsideResidenceResettlementAmount() {
        Integer outsideResidenceResettlementAmount = dao.getOutsideResidenceResettlementAmount();
        if (isNull(outsideResidenceResettlementAmount)) {
            return 0;
        }
        return outsideResidenceResettlementAmount;
    }

}
