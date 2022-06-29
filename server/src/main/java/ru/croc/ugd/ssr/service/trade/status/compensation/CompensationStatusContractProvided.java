package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

import java.time.LocalDate;

public class CompensationStatusContractProvided extends CompensationStatusSharedUnit {

    public CompensationStatusContractProvided() {
        this.nextUnit = new CompensationStatusRejectionApplied();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.CONTRACT_PROVIDED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate contractReadinessDate = statusCalculateCommand.getTradeAdditionType()
            .getContractReadinessDate();
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isCompensationTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            contractReadinessDate != null;
    }
}
