package ru.croc.ugd.ssr.dto.commissioninspection;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфиг для заявлений на комиссионный осмотр.
 */
@Getter
@Configuration
public class CommissionInspectionConfig {

    @Value("${ugd.ssr.commission-inspection.modernization.enabled:false}")
    private boolean commissionInspectionModernizationEnabled;
}
