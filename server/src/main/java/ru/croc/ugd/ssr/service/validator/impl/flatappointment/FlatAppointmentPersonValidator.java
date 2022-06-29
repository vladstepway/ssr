package ru.croc.ugd.ssr.service.validator.impl.flatappointment;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.ApartmentInspectionService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasFlatAppointmentRequest;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasOfferLetter;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsApartmentInspectionStarted;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class FlatAppointmentPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    public static final String NOT_ADULT_MESSAGE = "validate.flat.appointment.person.notAdult18";
    public static final String NOT_OWNER_MESSAGE = "validate.flat.appointment.person.notOwnerOrTenant";
    public static final String NO_OFFER_LETTER = "validate.flat.appointment.person.noOfferLetter";
    public static final String APARTMENT_INSPECTION_STARTED =
        "validate.flat.appointment.apartmentInspectionStarted";
    public static final String FLAT_APPOINTMENT_REQUESTED = "validate.flat.appointment.alreadyRequested";


    private final MessageSource messageSource;
    private final ApartmentInspectionService apartmentInspectionService;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        this.registerValidatorInChain(new IsOwnerOrTenantFlatAppointment<>(NOT_OWNER_MESSAGE));
        this.registerValidatorInChain(new HasOfferLetter<>(NO_OFFER_LETTER));
        this.registerValidatorInChain(new IsApartmentInspectionStarted<>(
            APARTMENT_INSPECTION_STARTED,
            apartmentInspectionService
        ));
        this.registerValidatorInChain(new HasFlatAppointmentRequest<>(
            FLAT_APPOINTMENT_REQUESTED,
            messageSource,
            flatAppointmentDocumentService
        ));
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }

}
