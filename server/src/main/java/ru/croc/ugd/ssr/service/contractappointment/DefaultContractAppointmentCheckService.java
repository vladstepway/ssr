package ru.croc.ugd.ssr.service.contractappointment;

import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.config.contractappointment.ContractAppointmentProperties;
import ru.croc.ugd.ssr.contractappointment.Applicant;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.enums.ContractAppointmentSignType;
import ru.croc.ugd.ssr.exception.PersonNotFoundException;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentApartmentDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentApplicantDto;
import ru.croc.ugd.ssr.generated.dto.contractappointment.ExternalRestContractAppointmentPersonCheckDto;
import ru.croc.ugd.ssr.mapper.ContractAppointmentMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.validator.impl.contractappointment.ContractAppointmentPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.GenericValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DefaultContractAppointmentCheckService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultContractAppointmentCheckService implements ContractAppointmentCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.contact.appointment.person.notApplicant";

    private final CipService cipService;
    private final PersonDocumentService personDocumentService;
    private final MessageSource messageSource;
    private final FlatService flatService;
    private final ContractAppointmentPersonValidator<ValidatablePersonData> contractAppointmentPersonValidator;
    private final ContractAppointmentMapper contractAppointmentMapper;
    private final PersonDocumentUtils personDocumentUtils;
    private final ContractAppointmentProperties contractAppointmentProperties;

    @Override
    public ExternalRestContractAppointmentPersonCheckDto checkPerson(final String snils, final String ssoId) {
        final List<PersonDocument> personDocumentList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        return check(personDocumentList, contractAppointmentPersonValidator::validate);
    }

    private ExternalRestContractAppointmentPersonCheckDto check(
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
            .map(validatableData -> Pair.of(validatableData, getNewFlats(validatableData)))
            .flatMap(validatablePersonDataListPair -> validatablePersonDataListPair
                .getRight()
                .stream()
                .map(newFlat -> Pair.of(validatablePersonDataListPair.getLeft(), newFlat)))
            .map(validatablePersonDataListPair -> ValidatablePersonData.builder()
                .person(validatablePersonDataListPair.getLeft().getPerson())
                .personId(validatablePersonDataListPair.getLeft().getPersonId())
                .newFlat(validatablePersonDataListPair.getRight())
                .build())
            .map(validatablePersonData -> Pair.of(validator.apply(validatablePersonData), validatablePersonData))
            .collect(Collectors.toList());

        final List<ExternalRestContractAppointmentApartmentDto> contractAppointmentApartmentList
            = validationResults
            .stream()
            .map(validatablePersonData -> getRestContractAppointmentApartmentDtoList(
                validatablePersonData.getRight(),
                validatablePersonData.getLeft()
            ))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(contractAppointmentApartmentList)) {
            return getContractReadyErrorResponse();
        }

        return getContractAppointmentPersonCheckDto(contractAppointmentApartmentList, validationResults);
    }

    private ExternalRestContractAppointmentPersonCheckDto getNoSuchApplicantResponse() {
        final ExternalRestContractAppointmentPersonCheckDto personCheckDto =
            new ExternalRestContractAppointmentPersonCheckDto();
        personCheckDto.setIsPossible(false);
        personCheckDto.setReason(messageSource.getMessage(NOT_APPLICANT_MESSAGE, null, Locale.getDefault()));
        return personCheckDto;
    }

    private ExternalRestContractAppointmentPersonCheckDto getContractReadyErrorResponse() {
        final ExternalRestContractAppointmentPersonCheckDto personCheckDto =
            new ExternalRestContractAppointmentPersonCheckDto();
        personCheckDto.setIsPossible(false);
        personCheckDto.setReason(
            messageSource.getMessage(ContractAppointmentPersonValidator.CONTRACT_READY, null, Locale.getDefault())
        );
        return personCheckDto;
    }

    private ExternalRestContractAppointmentApartmentDto getRestContractAppointmentApartmentDtoList(
        final ValidatablePersonData validatablePersonData, final ValidationResult validationResult
    ) {
        final PersonType.NewFlatInfo.NewFlat newFlat = validatablePersonData.getNewFlat();
        if (!validationResult.isValid()) {
            final String notAdultMessage = messageSource
                .getMessage(ContractAppointmentPersonValidator.NOT_ADULT_MESSAGE, null, Locale.getDefault());
            final String notOwnerMessage = messageSource
                .getMessage(ContractAppointmentPersonValidator.NOT_OWNER_MESSAGE, null, Locale.getDefault());
            final String joinedValidationMessage = validationResult.getJoinedValidationMessage();
            if (joinedValidationMessage.contains(notAdultMessage)
                || joinedValidationMessage.contains(notOwnerMessage)) {
                return null;
            }

            return getContractAppointmentApartmentDto(
                newFlat,
                validatablePersonData,
                false,
                validationResult.getFirstValidationMessage()
            );
        }
        return getContractAppointmentApartmentDto(newFlat, validatablePersonData);
    }

    private ExternalRestContractAppointmentApartmentDto getContractAppointmentApartmentDto(
        final PersonType.NewFlatInfo.NewFlat newFlat,
        final ValidatablePersonData validatablePersonData
    ) {
        return getContractAppointmentApartmentDto(newFlat, validatablePersonData, true, null);
    }

    private ExternalRestContractAppointmentApartmentDto getContractAppointmentApartmentDto(
        final PersonType.NewFlatInfo.NewFlat newFlat,
        final ValidatablePersonData validatablePersonData,
        final boolean isPossible,
        final String reason
    ) {
        final ExternalRestContractAppointmentApartmentDto contractAppointmentApartmentDto
            = new ExternalRestContractAppointmentApartmentDto();
        contractAppointmentApartmentDto.setPersonDocumentId(validatablePersonData.getPersonId());
        contractAppointmentApartmentDto.setAddress(PersonUtils.getNewFlatAddress(newFlat));

        PersonUtils.getOrderIdByNewFlat(validatablePersonData.getPerson(), newFlat)
            .ifPresent(contractAppointmentApartmentDto::setContractOrderId);

        PersonUtils.getCipId(validatablePersonData.getPerson())
            .flatMap(cipService::fetchById)
            .ifPresent(extractedCipDocument -> contractAppointmentApartmentDto
                .setCip(contractAppointmentMapper.toRestContractAppointmentCipDto(extractedCipDocument))
            );

        final List<PersonType> invitedPersonTypes = getInvitedPersonTypes(validatablePersonData);
        contractAppointmentApartmentDto.setInvitedApplicants(
            contractAppointmentMapper.toRestContractAppointmentApplicantDtoList(invitedPersonTypes)
        );

        final boolean isElectronicSignPossible = retrieveElectronicSignPossibleExists(
            isPossible, validatablePersonData.getPerson()
        );
        contractAppointmentApartmentDto.setIsElectronicSignPossible(isElectronicSignPossible);

        contractAppointmentApartmentDto.setIsPossible(isPossible);
        contractAppointmentApartmentDto.setReason(reason);

        return contractAppointmentApartmentDto;
    }

    private boolean retrieveElectronicSignPossibleExists(final boolean isPossible, final PersonType person) {
        return isPossible
            && nonNull(contractAppointmentProperties.getElectronicSign())
            && contractAppointmentProperties.getElectronicSign().isEnabled()
            && (person.isElectronicSignEnabled() || checkAffair(person));
    }

    private boolean checkAffair(final PersonType person) {
        return CollectionUtils.isEmpty(contractAppointmentProperties.getElectronicSign().getAffairs())
            || (nonNull(person) && contractAppointmentProperties.getElectronicSign().getAffairs()
            .stream()
            .anyMatch(affairId -> affairId.equals(person.getAffairId())));
    }

    private List<PersonType.NewFlatInfo.NewFlat> getNewFlats(final ValidatablePersonData validatablePersonData) {
        final PersonType personType = validatablePersonData.getPerson();
        flatService.loadCcoAddressForNewFlats(personType);

        return ofNullable(personType.getNewFlatInfo())
            .map(PersonType.NewFlatInfo::getNewFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .collect(Collectors.toList());
    }

    private List<PersonType> getInvitedPersonTypes(final ValidatablePersonData validatablePersonData) {
        return personDocumentService
            .getOtherFlatOwners(validatablePersonData.getPersonId(),
                validatablePersonData.getPerson(),
                true)
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());
    }

    private ExternalRestContractAppointmentPersonCheckDto getContractAppointmentPersonCheckDto(
        final List<ExternalRestContractAppointmentApartmentDto> contractAppointmentApartmentDtos,
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        final Boolean isPossible = validationResults
            .stream()
            .anyMatch(resultValidatablePersonDataPair ->
                Boolean.TRUE.equals(resultValidatablePersonDataPair.getLeft().isValid())
            );

        final ExternalRestContractAppointmentPersonCheckDto personCheckDto =
            new ExternalRestContractAppointmentPersonCheckDto();

        if (!isPossible) {
            personCheckDto.setReason(validationResults
                .stream()
                .map(Pair::getLeft)
                .map(GenericValidationResult::getFirstValidationMessage)
                .findFirst()
                .orElse(null));
        }

        personCheckDto.setIsPossible(isPossible);
        personCheckDto.setApartments(contractAppointmentApartmentDtos);
        return personCheckDto;
    }

    @Override
    public boolean canCreateApplication(final ContractAppointmentDocument contractAppointmentDocument) {
        final PersonDocument personDocument = retrievePerson(contractAppointmentDocument);

        final ExternalRestContractAppointmentPersonCheckDto contractAppointmentCheckResponse = check(
            Collections.singletonList(personDocument),
            contractAppointmentPersonValidator::validate
        );

        if (!contractAppointmentCheckResponse.getIsPossible()) {
            log.debug(
                "canCreateApplication: check returned isPossible = false, personDocumentId = {}",
                personDocument.getId()
            );
            return false;
        }
        return canCreateApplication(contractAppointmentDocument, contractAppointmentCheckResponse);
    }

    private boolean canCreateApplication(
        final ContractAppointmentDocument contractAppointmentDocument,
        final ExternalRestContractAppointmentPersonCheckDto contractAppointmentCheckResponse
    ) {
        final ContractAppointmentData contractAppointment = contractAppointmentDocument
            .getDocument()
            .getContractAppointmentData();

        if (Objects.isNull(contractAppointment.getContractOrderId())) {
            log.debug(
                "canCreateApplication: contractOrderId is null, personDocumentId = {}",
                contractAppointment.getApplicant().getPersonDocumentId()
            );
            return false;
        }

        return canCreateApplicationWithContractOrder(contractAppointment, contractAppointmentCheckResponse)
            && (contractAppointment.getSignType() == ContractAppointmentSignType.PERSONAL.getTypeCode()
            || !hasApplicantsWithoutSsoId(contractAppointment, contractAppointmentCheckResponse));
    }

    private boolean canCreateApplicationWithContractOrder(
        final ContractAppointmentData contractAppointment,
        final ExternalRestContractAppointmentPersonCheckDto contractAppointmentCheckResponse
    ) {
        final String contractOrderId = contractAppointment.getContractOrderId();

        final boolean result = of(contractAppointmentCheckResponse)
            .map(ExternalRestContractAppointmentPersonCheckDto::getApartments)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(ExternalRestContractAppointmentApartmentDto::getIsPossible)
            .map(ExternalRestContractAppointmentApartmentDto::getContractOrderId)
            .filter(Objects::nonNull)
            .anyMatch(id -> id.equals(contractOrderId));

        if (!result) {
            log.debug(
                "canCreateApplication: check returned isPossible = false, contractOrderId = {}, personDocumentId = {}",
                contractOrderId,
                contractAppointment.getApplicant().getPersonDocumentId()
            );
        }

        return result;
    }

    private boolean hasApplicantsWithoutSsoId(
        final ContractAppointmentData contractAppointment,
        final ExternalRestContractAppointmentPersonCheckDto contractAppointmentCheckResponse
    ) {
        final String contractOrderId = contractAppointment.getContractOrderId();

        boolean result = of(contractAppointmentCheckResponse)
            .map(ExternalRestContractAppointmentPersonCheckDto::getApartments)
            .map(Collection::stream)
            .orElse(Stream.empty())
            .filter(ExternalRestContractAppointmentApartmentDto::getIsPossible)
            .filter(dto -> Objects.equals(dto.getContractOrderId(), contractOrderId))
            .map(ExternalRestContractAppointmentApartmentDto::getInvitedApplicants)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .filter(Objects::nonNull)
            .map(ExternalRestContractAppointmentApplicantDto::getSsoId)
            .anyMatch(ssoId -> !StringUtils.hasText(ssoId));

        if (result) {
            log.debug(
                "canCreateApplication: has applicants without ssoId, contractOrderId = {}, personDocumentId = {}",
                contractOrderId,
                contractAppointment.getApplicant().getPersonDocumentId()
            );
        }

        return result;
    }

    private PersonDocument retrievePerson(final ContractAppointmentDocument contractAppointmentDocument) {
        return of(contractAppointmentDocument.getDocument())
            .map(ContractAppointment::getContractAppointmentData)
            .map(ContractAppointmentData::getApplicant)
            .map(Applicant::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .orElseThrow(() -> new PersonNotFoundException(
                "Applicant was not found for contractAppointment " + contractAppointmentDocument.getId()
            ));
    }
}
