package ru.croc.ugd.ssr.service.realestate;

import java.math.BigInteger;

public interface RealEstateService {

    /**
     * Создать BPM процесс с задачей на внесение дополнительных сведений о расселении дома.
     */
    void createCompleteResettlementProcess(final BigInteger unom);

    /**
     * Вычислить количественные значения по квартирам и сформировать список уномов заселяемых домов.
     */
    void calculateData();
}
