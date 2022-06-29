package ru.croc.ugd.ssr.config.contractappointment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Настройки записи на заключение договора.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = ContractAppointmentProperties.PREFIX)
public class ContractAppointmentProperties {
    /**
     * Префикс конфигурации.
     */
    static final String PREFIX = "ugd.ssr.contract-appointment";
    /**
     * Параметры настройки возможности электронного подписания договора.
     */
    private ElectronicSign electronicSign;

    @Value("${ugd.ssr.contract-appointment.modernization.enabled:false}")
    private boolean modernizationEnabled;

    @Getter
    @Setter
    public static class ElectronicSign {
        /**
         * Флаг включения возможности электронного подписания договора.
         */
        private boolean enabled;
        /**
         * Список семей, для которых должна быть доступна возможность электронного подписания договора.
         * Если список пустой, то доступность регулируется только флагом enabled.
         */
        private List<String> affairs;
        /**
         * Роль пользователей для подписания договора.
         */
        private String signingContractRole;
        /**
         * Ссылка для осуществления подписания.
         */
        private String signingContractTaskLink;
        /**
         * Верификация подписи выключена.
         */
        private Boolean verificationDisabled;
    }
}
