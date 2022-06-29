package ru.croc.ugd.ssr.service.validator.impl.person;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * We check if current date is less then 3 days before 14 working days after last signed
 * contract. 01.01.2000 - contract signed. 19.01.2000 --- we here? -> fail ---
 * 22.01.2000
 * менее трех дней до даты освобождения квартиры по договору.
 * @param <T> тип валидируемого объекта
 */
@Slf4j
public class RequestLessThenBeforeThreeDaysOfShipping<T extends ValidatablePersonData>
    extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     */
    public static final String TOO_LATE_FOR_SHIPPING = "validate.person.tooLateForShipping";
    public static final String TOO_LATE_FOR_SHIPPING_CODE = "too_late_to_apply";

    private final PersonDocumentService personDocumentService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     * @param personDocumentService Сервис работы с документами персон
     * @param tradeAdditionDocumentService Сервис работы с документами докупки
     */
    public RequestLessThenBeforeThreeDaysOfShipping(
        MessageSource messageSource,
        PersonDocumentService personDocumentService,
        TradeAdditionDocumentService tradeAdditionDocumentService
    ) {
        super(messageSource);
        this.personDocumentService = personDocumentService;
        this.tradeAdditionDocumentService = tradeAdditionDocumentService;
    }

    @Override
    public void validate(final T validatablePersonData) {
        log.info(
            "Shipping check: start RequestLessThenBeforeThreeDaysOfShipping.validate, person {}",
            validatablePersonData.getPersonId()
        );
        final LocalDate movementDate = getMoveDate(validatablePersonData);
        if (movementDate == null) {
            log.debug("Unable to calculate movement date: personDocumentId: {}", validatablePersonData.getPersonId());
            return;
        }
        final LocalDate threeDaysBeforeShipping =
            movementDate.minus(3, ChronoUnit.DAYS);
        if (LocalDate.now().isAfter(threeDaysBeforeShipping)) {
            throw getValidationException(getContractNumber(validatablePersonData));
        }
        log.info(
            "Shipping check: finish RequestLessThenBeforeThreeDaysOfShipping.validate, person {}",
            validatablePersonData.getPersonId()
        );
    }

    private LocalDate getMoveDate(final T validatablePersonData) {
        if (validatablePersonData.getTradeAddition() != null) {
            return tradeAdditionDocumentService.getMoveDateFromTradeAddition(validatablePersonData.getTradeAddition());
        }
        return personDocumentService
            .getContractSignDateAndNumber(validatablePersonData.getPerson())
            .map(Pair::getRight)
            .map(personDocumentService::getPersonMovementDate)
            .orElse(null);
    }

    private String getContractNumber(final T validatablePersonData) {
        if (validatablePersonData.getTradeAddition() != null) {
            return validatablePersonData.getTradeAddition().getContractNumber();
        }
        return personDocumentService
            .getContractSignDateAndNumber(validatablePersonData.getPerson())
            .map(Pair::getLeft)
            .orElse(null);
    }

    public RuntimeException getValidationException() {
        return getValidationException("");
    }

    private RuntimeException getValidationException(String contractNumber) {
        return new PersonNotValidForShippingException(
            String.format(getMessage(TOO_LATE_FOR_SHIPPING), contractNumber),
            TOO_LATE_FOR_SHIPPING_CODE
        );
    }

}
