package ru.croc.ugd.ssr.service.flatappointment;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.PersonType.OfferLetters.OfferLetter;
import ru.croc.ugd.ssr.exception.PersonNotFoundException;
import ru.croc.ugd.ssr.flatappointment.Applicant;
import ru.croc.ugd.ssr.flatappointment.FlatAppointment;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentApartmentDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentApplicantDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentCipDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentLetterDto;
import ru.croc.ugd.ssr.generated.dto.flatappointment.ExternalRestFlatAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.mapper.FlatAppointmentMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.flatAppointment.FlatAppointmentDocument;
import ru.croc.ugd.ssr.service.ChedFileService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.validator.impl.flatappointment.FlatAppointmentPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.GenericValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.croc.ugd.ssr.utils.StreamUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultFlatAppointmentCheckService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultFlatAppointmentCheckService implements FlatAppointmentCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.flat.appointment.person.notApplicant";
    private static final String KEYS_ALREADY_ISSUED_MESSAGE = "validate.flat.appointment.person.keysAlreadyIssued";

    private final CipService cipService;
    private final PersonDocumentService personDocumentService;
    private final MessageSource messageSource;
    private final PersonDocumentUtils personDocumentUtils;
    private final FlatAppointmentPersonValidator<ValidatablePersonData> flatAppointmentPersonValidator;
    private final FlatAppointmentMapper flatAppointmentMapper;
    private final ChedFileService chedFileService;

    @Override
    public ExternalRestFlatAppointmentPersonCheckDto checkPerson(final String snils, final String ssoId) {
        final List<PersonDocument> personDocumentList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        final List<PersonDocument> personDocumentNoKeysList = personDocumentList
            .stream()
            .filter(StreamUtils.not(this::isKeysIssued))
            .collect(Collectors.toList());
        if (personDocumentNoKeysList.isEmpty() && !personDocumentList.isEmpty()) {
            return getKeysIssuedResponse();
        }
        return check(personDocumentNoKeysList, flatAppointmentPersonValidator::validate);
    }

    private ExternalRestFlatAppointmentPersonCheckDto check(
        final List<PersonDocument> personDocumentList,
        final Function<ValidatablePersonData, ValidationResult> validator
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personDocumentUtils
            .mapPersonsToExpandedPersonTypes(personDocumentList);
        if (CollectionUtils.isEmpty(validatablePersonDataList)) {
            return getNoSuchApplicantResponse();
        }

        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults = validatablePersonDataList
            .stream()
            .map(validatablePersonData -> Pair.of(validator.apply(validatablePersonData), validatablePersonData))
            .collect(Collectors.toList());

        final List<ExternalRestFlatAppointmentApartmentDto> flatAppointmentApartmentDtos = validationResults
            .stream()
            .map(validatablePersonData ->
                getFlatAppointmentApartmentDto(validatablePersonData.getRight(), validatablePersonData.getLeft()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return getFlatAppointmentPersonCheckDto(flatAppointmentApartmentDtos, validationResults);
    }

    private boolean isKeysIssued(final PersonDocument personDocument) {
        return ofNullable(personDocument)
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .map(PersonType::getKeysIssue)
            .map(PersonType.KeysIssue::getActDate)
            .isPresent();
    }

    private ExternalRestFlatAppointmentPersonCheckDto getNoSuchApplicantResponse() {
        return getNotPossibleResponseWithMessage(NOT_APPLICANT_MESSAGE);
    }

    private ExternalRestFlatAppointmentPersonCheckDto getKeysIssuedResponse() {
        return getNotPossibleResponseWithMessage(KEYS_ALREADY_ISSUED_MESSAGE);
    }

    private ExternalRestFlatAppointmentPersonCheckDto getNotPossibleResponseWithMessage(final String messageKey) {
        final ExternalRestFlatAppointmentPersonCheckDto personCheckDto =
            new ExternalRestFlatAppointmentPersonCheckDto();
        personCheckDto.setIsPossible(false);
        personCheckDto.setReason(messageSource.getMessage(messageKey, null, Locale.getDefault()));
        return personCheckDto;
    }

    private ExternalRestFlatAppointmentApartmentDto getFlatAppointmentApartmentDto(
        ValidatablePersonData validatablePersonData, ValidationResult validationResult
    ) {
        final ExternalRestFlatAppointmentApartmentDto flatAppointmentApartmentDto
            = new ExternalRestFlatAppointmentApartmentDto();
        if (shouldIgnoreFlatAppointment(validationResult)) {
            return null;
        }
        final OfferLetter offerLetter = getOfferLetter(validatablePersonData);

        return flatAppointmentApartmentDto
            .isPossible(validationResult.isValid())
            .reason(validationResult.getFirstValidationMessage())
            .cip(getFlatAppointmentCipDto(validatablePersonData.getPerson(), offerLetter))
            .personDocumentId(validatablePersonData.getPersonId())
            .letter(flatAppointmentMapper
                .toExternalRestFlatAppointmentLetterDto(offerLetter, this::toOfferLetterChedDownloadUrl)
            )
            .invitedApplicants(getFlatAppointmentApplicantDtos(validatablePersonData));
    }

    private boolean shouldIgnoreFlatAppointment(final ValidationResult validationResult) {
        final String notAdultMessage = messageSource
            .getMessage(FlatAppointmentPersonValidator.NOT_ADULT_MESSAGE, null, Locale.getDefault());
        final String notOwnerMessage = messageSource
            .getMessage(FlatAppointmentPersonValidator.NOT_OWNER_MESSAGE, null, Locale.getDefault());
        final String noOfferLetterMessage = messageSource
            .getMessage(FlatAppointmentPersonValidator.NO_OFFER_LETTER, null, Locale.getDefault());
        final String joinedValidationMessage = validationResult.getJoinedValidationMessage();
        return joinedValidationMessage.contains(notAdultMessage)
            || joinedValidationMessage.contains(notOwnerMessage)
            || joinedValidationMessage.contains(noOfferLetterMessage);
    }

    private OfferLetter getOfferLetter(final ValidatablePersonData validatablePersonData) {
        return PersonUtils.getLastOfferLetterIfNotDeclined(validatablePersonData.getPerson())
            .orElse(null);
    }

    private String toOfferLetterChedDownloadUrl(final OfferLetter offerLetter) {
        return PersonUtils.getOfferLetterChedId(offerLetter)
            .flatMap(chedFileService::getChedFileLink)
            .orElse(null);
    }

    private List<ExternalRestFlatAppointmentApplicantDto> getFlatAppointmentApplicantDtos(
        final ValidatablePersonData validatablePersonData
    ) {
        final List<PersonType> personTypes = getInvitedPersons(validatablePersonData);
        return flatAppointmentMapper.toExternalRestFlatAppointmentApplicantDtos(personTypes);
    }

    private List<PersonType> getInvitedPersons(final ValidatablePersonData validatablePersonData) {
        return personDocumentService
            .getOtherFlatOwners(validatablePersonData.getPersonId(), validatablePersonData.getPerson(), true)
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());
    }

    private ExternalRestFlatAppointmentCipDto getFlatAppointmentCipDto(
        final PersonType person, final OfferLetter offerLetter
    ) {
        return ofNullable(offerLetter)
            .map(OfferLetter::getIdCIP)
            .flatMap(cipService::fetchById)
            .map(flatAppointmentMapper::toExternalRestFlatAppointmentCipDto)
            .orElse(null);
    }

    private ExternalRestFlatAppointmentPersonCheckDto getFlatAppointmentPersonCheckDto(
        final List<ExternalRestFlatAppointmentApartmentDto> externalRestFlatAppointmentApartmentDtos,
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        final Boolean isPossible = validationResults
            .stream()
            .anyMatch(resultValidatablePersonDataPair ->
                Boolean.TRUE.equals(resultValidatablePersonDataPair.getLeft().isValid())
            );

        final ExternalRestFlatAppointmentPersonCheckDto personCheckDto =
            new ExternalRestFlatAppointmentPersonCheckDto();

        if (!isPossible) {
            personCheckDto.setReason(externalRestFlatAppointmentApartmentDtos
                .stream()
                .map(ExternalRestFlatAppointmentApartmentDto::getReason)
                .findFirst()
                .orElseGet(() -> validationResults
                    .stream()
                    .map(Pair::getLeft)
                    .map(GenericValidationResult::getFirstValidationMessage)
                    .findFirst()
                    .orElse(null)
                )
            );
        }

        personCheckDto.setIsPossible(isPossible);
        personCheckDto.setApartments(externalRestFlatAppointmentApartmentDtos);
        return personCheckDto;
    }

    @Override
    public boolean canCreateApplication(final FlatAppointmentDocument flatAppointmentDocument) {
        final PersonDocument personDocument = retrievePerson(flatAppointmentDocument);

        final ExternalRestFlatAppointmentPersonCheckDto flatAppointmentCheckResponse = check(
            Collections.singletonList(personDocument),
            flatAppointmentPersonValidator::validate
        );

        if (!flatAppointmentCheckResponse.getIsPossible()) {
            log.error(
                "canCreateApplication: check returned isPossible = false, personDocumentId = {}",
                personDocument.getId()
            );
            return false;
        }

        return canCreateApplication(flatAppointmentDocument, flatAppointmentCheckResponse);
    }

    private boolean canCreateApplication(
        final FlatAppointmentDocument flatAppointmentDocument,
        final ExternalRestFlatAppointmentPersonCheckDto flatAppointmentCheckResponse
    ) {
        final FlatAppointmentData flatAppointment = flatAppointmentDocument
            .getDocument()
            .getFlatAppointmentData();

        if (Objects.isNull(flatAppointment.getOfferLetterId())) {
            log.error(
                "canCreateApplication: letterId is null, personDocumentId = {}",
                flatAppointment.getApplicant().getPersonDocumentId()
            );
            return false;
        }

        return canCreateApplicationWithLetter(flatAppointment, flatAppointmentCheckResponse);
    }

    private boolean canCreateApplicationWithLetter(
        final FlatAppointmentData flatAppointment,
        final ExternalRestFlatAppointmentPersonCheckDto flatAppointmentCheckResponse
    ) {
        final String letterId = flatAppointment.getOfferLetterId();

        final boolean result = of(flatAppointmentCheckResponse)
            .map(ExternalRestFlatAppointmentPersonCheckDto::getApartments)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(ExternalRestFlatAppointmentApartmentDto::getIsPossible)
            .map(ExternalRestFlatAppointmentApartmentDto::getLetter)
            .filter(Objects::nonNull)
            .map(ExternalRestFlatAppointmentLetterDto::getId)
            .filter(Objects::nonNull)
            .anyMatch(id -> id.equals(letterId));

        if (!result) {
            log.error(
                "canCreateApplication: check returned isPossible = false, letterId = {}, personDocumentId = {}",
                letterId,
                flatAppointment.getApplicant().getPersonDocumentId()
            );
        }

        return result;
    }

    private PersonDocument retrievePerson(final FlatAppointmentDocument flatAppointmentDocument) {
        return of(flatAppointmentDocument.getDocument())
            .map(FlatAppointment::getFlatAppointmentData)
            .map(FlatAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElseThrow(() -> new PersonNotFoundException(
                "Applicant was not found for contractAppointment " + flatAppointmentDocument.getId()
            ));
    }
}
