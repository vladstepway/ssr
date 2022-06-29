package ru.croc.ugd.ssr.integration.eno;

import static ru.croc.ugd.ssr.integration.eno.EnoSequenceCode.UGD_SSR_ENO_ASUR_SEQ;
import static ru.croc.ugd.ssr.integration.eno.EnoSequenceCode.UGD_SSR_ENO_ELK_SEQ;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.croc.ugd.ssr.integration.config.IntegrationPropertyConfig;
import ru.reinform.cdp.seqs.model.SequenceData;
import ru.reinform.cdp.seqs.service.SequencesRemoteApi;
import ru.reinform.cdp.utils.core.RIExceptionUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Класс-генератор ЕНО для интеграций.
 */
@Component
@AllArgsConstructor
public class EnoCreator {

    private static final Logger LOG = LoggerFactory.getLogger(EnoCreator.class);

    private final SequencesRemoteApi sequencesRemoteApi;

    private final IntegrationPropertyConfig config;

    /**
     * Генерируем ЕНО для ЕТП МВ (уведомления).
     *
     * @param guNumber номер ГУ
     * @return ЕНО номер
     */
    public String generateEtpMvNotificationEnoNumber(String guNumber) {
        try {
            SequenceData nextValue =
                sequencesRemoteApi.nextValue(UGD_SSR_ENO_ELK_SEQ, String.valueOf(LocalDate.now().getYear()));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String eno = MessageFormat.format("{0}-{1}-{2,number,000000}/{3}",
                config.getEtpMvEnoCode(),
                guNumber,
                nextValue.getValue(),
                Integer.toString(year).substring(2, 4));
            LOG.debug("Сформировано ЕНО: {}", eno);
            return eno;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0} ()", RIExceptionUtils.method())
                .withUserMessage("Ошибка получения ЕНО.");
        }
    }

    /**
     * Генерируем ЕНО для ЕТП МВ (уведомления).
     *
     * @param guNumber    номер ГУ
     * @param enoSequence название последовательности для генерации ено.
     * @return ЕНО номер
     */
    public String generateEtpMvEnoNumber(String guNumber, String enoSequence) {
        try {
            SequenceData nextValue =
                sequencesRemoteApi.nextValue(enoSequence, String.valueOf(LocalDate.now().getYear()));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String eno = MessageFormat.format("{0}-{1}-{2,number,000000}/{3}",
                config.getEtpMvEnoCode(),
                guNumber,
                nextValue.getValue(),
                Integer.toString(year).substring(2, 4));
            LOG.debug("Сформировано ЕНО: {}", eno);
            return eno;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0} ()", RIExceptionUtils.method())
                .withUserMessage("Ошибка получения ЕНО.");
        }
    }

    /**
     * Генерируем ЕНО для АСУР.
     *
     * @return номер
     */
    public String generateAsurEnoNumber() {
        return generateAsurEnoNumber(config.getAsurEnoCode(), config.getAsurEnoService());
    }

    /**
     * Генерируем ЕНО для АСУР.
     *
     * @param code code
     * @param guNumber guNumber
     * @return номер
     */
    public String generateAsurEnoNumber(final String code, final String guNumber) {
        try {
            SequenceData nextValue =
                sequencesRemoteApi.nextValue(UGD_SSR_ENO_ASUR_SEQ, String.valueOf(LocalDate.now().getYear()));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String eno = MessageFormat.format("{0}-{1}-{2,number,000000}/{3}",
                code,
                guNumber,
                nextValue.getValue(),
                Integer.toString(year).substring(2, 4));
            LOG.debug("Сформировано ЕНО: {}", eno);
            return eno;
        } catch (Exception e) {
            throw RIExceptionUtils.wrap(e, "error on {0} ()", RIExceptionUtils.method())
                .withUserMessage("Ошибка получения ЕНО.");
        }
    }

}
