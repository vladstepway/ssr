package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.flatappointment.FlatAppointment;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class HasFlatAppointmentRequest<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final String nonValidMessage;
    private final MessageSource messageSource;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;

    @Override
    public ValidatorUnitResult validate(T validatablePersonData) {
        return flatAppointmentDocumentService.findAll(validatablePersonData.getPersonId(), false)
            .stream()
            .map(FlatAppointmentDocument::getDocument)
            .map(FlatAppointment::getFlatAppointmentData)
            .filter(flatAppointment -> Objects.nonNull(flatAppointment.getAppointmentDateTime()))
            .filter(flatAppointment -> flatAppointment.getAppointmentDateTime().isAfter(LocalDateTime.now()))
            .findFirst()
            .map(appointmentDocument -> ValidatorUnitResult.fail(getMessage(appointmentDocument)))
            .orElseGet(ValidatorUnitResult::ok);
    }

    private String getMessage(final FlatAppointmentData flatAppointmentData) {
        return messageSource.getMessage(
            nonValidMessage, new Object[]{
                Optional.ofNullable(flatAppointmentData.getAppointmentDateTime())
                    .map(dateTime -> dateTime.format(DATE_FORMATTER))
                    .orElse(null),
                Optional.ofNullable(flatAppointmentData.getAppointmentDateTime())
                    .map(dateTime -> dateTime.format(TIME_FORMATTER))
                    .orElse(null)},
            Locale.getDefault());
    }
}
