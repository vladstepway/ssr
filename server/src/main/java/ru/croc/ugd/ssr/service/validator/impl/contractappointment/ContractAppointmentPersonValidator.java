package ru.croc.ugd.ssr.service.validator.impl.contractappointment;

import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasContractAppointmentRequest;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsContractForFlatSigned;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsContractReady;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsOwnerOrTenant;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsTradeAdditionCompensation;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class ContractAppointmentPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    public static final String NOT_ADULT_MESSAGE = "validate.contact.appointment.person.notAdult18";
    public static final String NOT_OWNER_MESSAGE = "validate.contact.appointment.person.notOwnerOrTenant";
    public static final String CONTRACT_READY = "validate.contact.appointment.contractReady";
    private static final String COMPENSATION_PROVIDED_MESSAGE_OLD =
        "validate.contact.appointment.person.compensationProvided.old";
    private static final String COMPENSATION_PROVIDED_MESSAGE =
        "validate.contact.appointment.person.compensationProvided";
    private static final String CONTRACT_SIGNED = "validate.contact.appointment.contractSigned";
    private static final String REQUEST_EXISTS = "validate.contact.appointment.requestExists";

    private final MessageSource messageSource;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final ContractAppointmentProperties contractAppointmentProperties;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        this.registerValidatorInChain(new IsOwnerOrTenant<>(T -> NOT_OWNER_MESSAGE));
        this.registerValidatorInChain(new IsTradeAdditionCompensation<>(
            tradeAdditionDocumentService, T -> retrieveCompensationProvidedMessage()));
        this.registerValidatorInChain(new IsContractForFlatSigned<>(T -> CONTRACT_SIGNED));
        this.registerValidatorInChain(new IsContractReady<>(T -> CONTRACT_READY));
        this.registerValidatorInChain(new HasContractAppointmentRequest<>(
            REQUEST_EXISTS, messageSource, contractAppointmentDocumentService, personDocumentService, cipService));
    }

    private String retrieveCompensationProvidedMessage() {
        if (nonNull(contractAppointmentProperties.getElectronicSign())
            && contractAppointmentProperties.getElectronicSign().isEnabled()) {
            return COMPENSATION_PROVIDED_MESSAGE;
        } else {
            return COMPENSATION_PROVIDED_MESSAGE_OLD;
        }
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }

}
