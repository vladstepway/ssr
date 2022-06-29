package ru.croc.ugd.ssr.config.agreementapplication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки заявлений о согласии/отказе на предложенную квартиру.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = AgreementApplicationProperties.PREFIX)
public class AgreementApplicationProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.agreement-application";

    private boolean enabled;
    private boolean updatedOfferLetterNotificationEnabled;
    /**
     * Число дней действия предложения.
     */
    private int daysToReceiveOffer;
}
