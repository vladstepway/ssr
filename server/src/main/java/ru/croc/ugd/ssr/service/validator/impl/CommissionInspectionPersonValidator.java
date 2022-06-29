package ru.croc.ugd.ssr.service.validator.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.CommissionInspectionDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.CommissionInspectionAlreadyRequested;
import ru.croc.ugd.ssr.service.validator.impl.person.NoAgreementForApartment;
import ru.croc.ugd.ssr.service.validator.impl.person.WarrantyPeriodExpired;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsOwnerOrTenant;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

/**
 * Валидатор жильца для комиссионного осмотра.
 */
@Service
@AllArgsConstructor
public class CommissionInspectionPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    private static final String NOT_OWNER_MESSAGE = "validate.commissionInspection.person.notOwnerOrTenant";
    private static final String ALREADY_REQUESTED_MESSAGE =
        "validate.commissionInspection.person.alreadyRequested";
    private static final String NO_AGREEMENT_FOR_APARTMENT_MESSAGE =
        "validate.commissionInspection.person.noAgreementForApartment";
    private static final String WARRANTY_PERIOD_EXPIRED_MESSAGE =
        "validate.commissionInspection.person.warrantyPeriodHasExpired";

    @Getter
    protected final MessageSource messageSource;
    private final PersonDocumentService personDocumentService;
    private final CommissionInspectionDocumentService commissionInspectionDocumentService;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(
            new IsOwnerOrTenant<>(T -> NOT_OWNER_MESSAGE)
        );
        this.registerValidatorInChain(new CommissionInspectionAlreadyRequested<>(
            commissionInspectionDocumentService, ALREADY_REQUESTED_MESSAGE
        ));
        this.registerValidatorInChain(
            new NoAgreementForApartment<>(NO_AGREEMENT_FOR_APARTMENT_MESSAGE)
        );
        this.registerValidatorInChain(
            new WarrantyPeriodExpired<>(personDocumentService, WARRANTY_PERIOD_EXPIRED_MESSAGE)
        );
    }
}
