package ru.croc.ugd.ssr.service.trade.status;


public abstract class TradeAdditionAbstractStatusCalculatorUnit<T extends Enum> {
    public TradeAdditionAbstractStatusCalculatorUnit<T> nextUnit;

    public T getStatus(StatusCalculateCommand statusCalculateCommand) {
        if (isConditionMatched(statusCalculateCommand)) {
            return getUnitStatus();
        }
        return getNextIfNotNull(null, statusCalculateCommand);
    }

    protected abstract boolean isConditionMatched(StatusCalculateCommand statusCalculateCommand);

    protected abstract T getUnitStatus();

    protected T getNextIfNotNull(T current, StatusCalculateCommand statusCalculateCommand) {
        if (nextUnit == null) {
            return current;
        }
        final T nextStatus = nextUnit.getStatus(statusCalculateCommand);
        return nextStatus == null ? current : nextStatus;
    }
}
