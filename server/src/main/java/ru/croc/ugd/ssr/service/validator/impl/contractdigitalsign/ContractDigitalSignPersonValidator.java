package ru.croc.ugd.ssr.service.validator.impl.contractdigitalsign;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.IsContactSigningPeriodCorrect;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsContractForFlatSigned;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsContractReady;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsElectronicSignPossible;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsOwnerOrTenant;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsTradeAdditionCompensation;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class ContractDigitalSignPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final String NOT_ADULT_MESSAGE = "validate.contact.digital.sign.person.notAdult18";
    public static final String NOT_OWNER_MESSAGE = "validate.contact.digital.sign.person.notOwnerOrTenant";
    public static final String CONTRACT_SIGNING_PERIOD_INCORRECT_MESSAGE =
        "validate.contact.digital.sign.contactSigningPeriodIncorrect";
    public static final String ELECTRONIC_SIGN_IMPOSSIBLE_MESSAGE =
        "validate.contact.digital.sign.electronicSignImpossible";
    private static final String COMPENSATION_PROVIDED_MESSAGE = "validate.contact.digital.sign.compensationProvided";
    private static final String CONTRACT_READY = "validate.contact.digital.sign.contractReady";
    private static final String CONTRACT_SIGNED = "validate.contact.digital.sign.contractSigned";

    private final MessageSource messageSource;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final ContractAppointmentProperties contractAppointmentProperties;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        this.registerValidatorInChain(new IsOwnerOrTenant<>(this::retrieveNotOwnerMessage));
        this.registerValidatorInChain(new IsContactSigningPeriodCorrect<>(
            messageSource, CONTRACT_SIGNING_PERIOD_INCORRECT_MESSAGE
        ));
        this.registerValidatorInChain(new IsElectronicSignPossible<>(
            contractAppointmentProperties, ELECTRONIC_SIGN_IMPOSSIBLE_MESSAGE
        ));
        this.registerValidatorInChain(new IsTradeAdditionCompensation<>(
            tradeAdditionDocumentService, this::retrieveCompensationProvidedMessage
        ));
        this.registerValidatorInChain(new IsContractReady<>(this::retrieveContractReadyMessage));
        this.registerValidatorInChain(new IsContractForFlatSigned<>(this::retrieveContractSignedMessage));
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }

    private String retrieveNotOwnerMessage(final T validatablePersonData) {
        return retrieveErrorMessageWithCipPhone(validatablePersonData, NOT_OWNER_MESSAGE);
    }

    private String retrieveCompensationProvidedMessage(final T validatablePersonData) {
        return retrieveErrorMessageWithCipPhone(validatablePersonData, COMPENSATION_PROVIDED_MESSAGE);
    }

    private String retrieveContractReadyMessage(final T validatablePersonData) {
        return retrieveErrorMessageWithCipPhone(validatablePersonData, CONTRACT_READY);
    }

    private String retrieveContractSignedMessage(final T validatablePersonData) {
        return retrieveErrorMessageWithCipPhone(validatablePersonData, CONTRACT_SIGNED);
    }

    private String retrieveErrorMessageWithCipPhone(final T validatablePersonData, final String errorMessage) {
        return messageSource.getMessage(
            errorMessage,
            new Object[]{
                Optional.ofNullable(validatablePersonData.getCipData())
                    .map(CipType::getPhone)
                    .orElse(null)
            },
            Locale.getDefault()
        );
    }
}
