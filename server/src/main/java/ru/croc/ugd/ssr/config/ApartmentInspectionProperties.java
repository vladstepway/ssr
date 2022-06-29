package ru.croc.ugd.ssr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ApartmentInspectionConfig.
 */
@ConfigurationProperties(prefix = "ugd.ssr.apartment-inspection")
@Data
public class ApartmentInspectionProperties {
    private String defaultTechUserOrg;
    private String kpUgsInn;
    /**
     * Группа, дающая право без подтверждения выполнять закрытие актов без согласия жителя.
     */
    private String closingActConfirmationGroup;
}
