package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

import java.time.LocalDate;

public class CompensationStatusKeysIssued extends CompensationStatusSharedUnit {

    public CompensationStatusKeysIssued() {
        this.nextUnit = new CompensationStatusContractSigned();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.KEYS_ISSUED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final LocalDate keysIssueDate = statusCalculateCommand.getTradeAdditionType()
            .getKeysIssueDate();
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();

        return isCompensationTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            keysIssueDate != null;
    }
}
