package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

import java.time.LocalDate;

public class CompensationStatusContractSigned extends CompensationStatusSharedUnit {

    public CompensationStatusContractSigned() {
        this.nextUnit = new CompensationStatusContractProvided();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.CONTRACT_SIGNED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate contractSignedDate = statusCalculateCommand.getTradeAdditionType()
            .getContractSignedDate();
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isCompensationTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            contractSignedDate != null;
    }
}
