package ru.croc.ugd.ssr.service.validator.impl.person.value;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.validator.ValueBasedNotEmptyValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatorUnitResult;
import ru.croc.ugd.ssr.utils.CipUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@AllArgsConstructor
public class HasContractAppointmentRequest<T extends ValidatablePersonData>
    implements ValueBasedNotEmptyValidatorUnit<T> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final String nonValidMessage;
    private final MessageSource messageSource;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final PersonDocumentService personDocumentService;
    private final CipService cipService;

    @Override
    public ValidatorUnitResult validate(T object) {
        if (object.getPerson().getNewFlatInfo() == null) {
            return ValidatorUnitResult.ok();
        }
        final Optional<ContractAppointmentDocument> contractAppointmentDocument = PersonUtils
            .getOrderIdByNewFlat(object.getPerson(), object.getNewFlat())
            .flatMap(contractAppointmentDocumentService::fetchActiveOrEnteredContractAppointment);

        return contractAppointmentDocument
            .map(appointmentDocument -> ValidatorUnitResult.fail(
                getMessage(appointmentDocument.getDocument().getContractAppointmentData())
            ))
            .orElseGet(ValidatorUnitResult::ok);
    }

    private String getMessage(final ContractAppointmentData contractAppointmentData) {
        return messageSource.getMessage(
            nonValidMessage, new Object[]{
                Optional.ofNullable(contractAppointmentData.getApplicant().getFio()).orElseGet(() ->
                    Optional.ofNullable(contractAppointmentData.getApplicant().getPersonDocumentId())
                        .flatMap(personDocumentService::fetchById)
                        .map(PersonUtils::getFullName)
                        .orElse(null)),
                Optional.ofNullable(contractAppointmentData.getAppointmentDateTime())
                    .map(dateTime -> dateTime.format(DATE_FORMATTER))
                    .orElse(null),
                Optional.ofNullable(contractAppointmentData.getAppointmentDateTime())
                    .map(dateTime -> dateTime.format(TIME_FORMATTER))
                    .orElse(null),
                Optional.ofNullable(contractAppointmentData.getCipId())
                    .flatMap(cipService::fetchById)
                    .map(CipDocument::getDocument)
                    .map(Cip::getCipData)
                    .map(CipUtils::getCipAddress)
                    .orElse(null)
            },
            Locale.getDefault());
    }
}
