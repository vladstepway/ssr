package ru.croc.ugd.ssr.integration.mqetpmv;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки интеграции с mqetpmv.
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = MqetpmvProperties.PREFIX)
public class MqetpmvProperties {

    /**
     * Префикс свойств mqetpmv.
     */
    public static final String PREFIX = "ugd.ssr.mqetpmv";

    private String rsmObjectRequestMessageType;

    private String rsmObjectResponseStatusOutProfile;

    private String mfrFlowTaskOutProfile;

    private String mfrFlowStatusOutProfile;

    private String mfrFlowTaskIncMessageType;

    private String mfrFlowStatusIncMessageType;

    private String courtFlowTaskIncMessageType;

    private String courtFlowStatusOutProfile;

    private String disabledPersonFlowTaskOutProfile;

    private String disabledPersonFlowStatusIncMessageType;

    private String tradeAdditionFlowStatusIncMessageType;

    private String tradeAdditionFlowTaskOutProfile;

    private String flatLayoutStatusIncMessageType;

    private String flatLayoutFlowTaskOutProfile;

    private String notaryCompensationRequestIncMessageType;

    private String notaryCompensationStatusOutProfile;

    private String affairCollationTaskOutProfile;

    private String affairCollationReportMessageType;

    private String personResponseMessageType;

    private String personUpdateMessageType;

    private String offerLetterMessageType;

    private String statusRelocationMessageType;

    private String contractReadyMessageType;

    private String contractSignedMessageType;

    private String contractRegisterMessageType;

    private String infoSettlementMessageType;

    private String contractPrReadyMessageType;

    private String administrativeDocumentMessageType;
}
