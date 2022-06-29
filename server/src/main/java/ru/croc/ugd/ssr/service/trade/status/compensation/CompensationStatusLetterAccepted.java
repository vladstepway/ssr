package ru.croc.ugd.ssr.service.trade.status.compensation;

import ru.croc.ugd.ssr.service.trade.status.StatusCalculateCommand;
import ru.croc.ugd.ssr.trade.ClaimStatus;
import ru.croc.ugd.ssr.trade.CompensationStatus;

import java.time.LocalDate;

public class CompensationStatusLetterAccepted extends CompensationStatusSharedUnit {

    public CompensationStatusLetterAccepted() {
        this.nextUnit = new CompensationStatusRejected();
    }

    @Override
    protected CompensationStatus getUnitStatus() {
        return CompensationStatus.LETTER_ACCEPTED;
    }

    @Override
    protected boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand) {
        final ClaimStatus claimStatus = statusCalculateCommand.getTradeAdditionType().getClaimStatus();
        final LocalDate offerLetterDate = statusCalculateCommand.getTradeAdditionType().getOfferLetterDate();

        return isCompensationTradeType(statusCalculateCommand) &&
            ClaimStatus.ACTIVE.equals(claimStatus) &&
            offerLetterDate != null;
    }
}
