package ru.croc.ugd.ssr.service.validator.impl.person;

import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.util.Optional;

/**
 * оформить заявку на помощь в переезде можно когда все правообладатели старой квартиры подпишут договор на новую.
 * @param <T> тип валидируемого объекта
 */
public class IsContractSigned<T extends ValidatablePersonData> extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     */
    public static final String CONTRACT_NOT_SIGNED = "validate.person.contractNotSigned";
    public static final String CONTRACT_NOT_SIGNED_TRADE = "validate.person.contractNotSignedTrade";
    public static final String CONTRACT_NOT_SIGNED_CODE = "conclude_contract_first";

    private final PersonDocumentService personDocumentService;

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     * @param personDocumentService Сервис работы с документами персон
     */
    public IsContractSigned(MessageSource messageSource,
                            PersonDocumentService personDocumentService) {
        super(messageSource);
        this.personDocumentService = personDocumentService;
    }

    @Override
    public void validate(final T validatablePersonData) {
        if (validatablePersonData.getTradeAddition() == null) {
            personDocumentService.getContractSignDate(validatablePersonData.getPerson())
                .orElseThrow(this::getValidationException);
        } else {
            Optional.ofNullable(validatablePersonData.getTradeAddition().getContractSignedDate())
                .orElseThrow(this::getValidationExceptionTrade);
        }
    }

    public RuntimeException getValidationException() {
        return new PersonNotValidForShippingException(getMessage(CONTRACT_NOT_SIGNED), CONTRACT_NOT_SIGNED_CODE);
    }

    public RuntimeException getValidationExceptionTrade() {
        return new PersonNotValidForShippingException(getMessage(CONTRACT_NOT_SIGNED_TRADE), CONTRACT_NOT_SIGNED_CODE);
    }

}
