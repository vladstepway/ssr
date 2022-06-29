package ru.croc.ugd.ssr.integration.service.flows.mfr.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.croc.ugd.ssr.integrationflow.IntegrationFlowType;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpAuctionClaimResultInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpAuctionResultInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInClaimInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpBuyInDecisionResultInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimCancelInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpClaimInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpCompensationOfferStatus;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractReadyInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractRegistrationInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractSignInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpContractTerminationInfo;
import ru.croc.ugd.ssr.model.integration.mfr.MfrToDgpKeyIssuanceInfo;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum MfrToDgpFlowDataType {

    COMPENSATION_OFFER_INFO_TYPE(
        "880153",
        MfrToDgpCompensationOfferInfo.class,
        "mfrToDgpCompensationOfferInfoService",
        IntegrationFlowType.MFR_TO_DGP_COMPENSATION_OFFER_INFO
    ),
    COMPENSATION_OFFER_STATUS_TYPE(
        "880154",
        MfrToDgpCompensationOfferStatus.class,
        "mfrToDgpCompensationOfferStatusService",
        IntegrationFlowType.MFR_TO_DGP_COMPENSATION_OFFER_STATUS
    ),
    BUY_IN_CLAIM_INFO_TYPE(
        "880155",
        MfrToDgpBuyInClaimInfo.class,
        "mfrToDgpBuyInClaimInfoService",
        IntegrationFlowType.MFR_TO_DGP_BUY_IN_CLAIM_INFO
    ),
    CLAIM_INFO_TYPE(
        "880156",
        MfrToDgpClaimInfo.class,
        "mfrToDgpClaimInfoService",
        IntegrationFlowType.MFR_TO_DGP_CLAIM_INFO
    ),
    BUY_IN_DECISION_RESULT_INFO_TYPE(
        "880157",
        MfrToDgpBuyInDecisionResultInfo.class,
        "mfrToDgpBuyInDecisionResultInfoService",
        IntegrationFlowType.MFR_TO_DGP_BUY_IN_DECISION_RESULT_INFO
    ),
    AUCTION_CLAIM_RESULT_INFO_TYPE(
        "880158",
        MfrToDgpAuctionClaimResultInfo.class,
        "mfrToDgpAuctionClaimResultInfoService",
        IntegrationFlowType.MFR_TO_DGP_AUCTION_CLAIM_RESULT_INFO
    ),
    AUCTION_RESULT_INFO_TYPE(
        "880159",
        MfrToDgpAuctionResultInfo.class,
        "mfrToDgpAuctionResultInfoService",
        IntegrationFlowType.MFR_TO_DGP_AUCTION_RESULT_INFO
    ),
    CONTRACT_READY_INFO_TYPE(
        "880160",
        MfrToDgpContractReadyInfo.class,
        "mfrToDgpContractReadyInfoService",
        IntegrationFlowType.MFR_TO_DGP_CONTRACT_READY_INFO
    ),
    CONTRACT_SIGN_INFO_TYPE(
        "880161",
        MfrToDgpContractSignInfo.class,
        "mfrToDgpContractSignInfoService",
        IntegrationFlowType.MFR_TO_DGP_CONTRACT_SIGN_INFO
    ),
    CONTRACT_REGISTRATION_INFO_TYPE(
        "880162",
        MfrToDgpContractRegistrationInfo.class,
        "mfrToDgpContractRegistrationInfoService",
        IntegrationFlowType.MFR_TO_DGP_CONTRACT_REGISTRATION_INFO
    ),
    CONTRACT_TERMINATION_INFO_TYPE(
        "880163",
        MfrToDgpContractTerminationInfo.class,
        "mfrToDgpContractTerminationInfoService",
        IntegrationFlowType.MFR_TO_DGP_CONTRACT_TERMINATION_INFO
    ),
    CLAIM_CANCEL_INFO_TYPE(
        "880164",
        MfrToDgpClaimCancelInfo.class,
        "mfrToDgpClaimCancelInfoService",
        IntegrationFlowType.MFR_TO_DGP_CLAIM_CANCEL_INFO
    ),
    KEY_ISSUANCE_INFO_TYPE(
        "880165",
        MfrToDgpKeyIssuanceInfo.class,
        "mfrToDgpKeyIssuanceInfoService",
        IntegrationFlowType.MFR_TO_DGP_KEY_ISSUANCE_INFO
    );

    private final String code;
    private final Class<?> clazz;
    private final String serviceBeanName;
    private final IntegrationFlowType integrationFlowType;

    public static Optional<MfrToDgpFlowDataType> of(final String code) {
        return Arrays.stream(MfrToDgpFlowDataType.values())
            .filter(status -> Objects.equals(code, status.getCode()))
            .findFirst();
    }
}
