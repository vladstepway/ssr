package ru.croc.ugd.ssr.service.contractdigitalsign;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.Cip;
import ru.croc.ugd.ssr.CipType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSign;
import ru.croc.ugd.ssr.contractdigitalsign.ContractDigitalSignData;
import ru.croc.ugd.ssr.contractdigitalsign.Owner;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestApartmentContractDigitalSignDto;
import ru.croc.ugd.ssr.generated.dto.contractdigitalsign.ExternalRestCheckContractDigitalSignDto;
import ru.croc.ugd.ssr.mapper.ContractDigitalSignMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.contractDigitalSign.ContractDigitalSignDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.contractdigitalsign.ContractDigitalSignPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.GenericValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ContractDigitalSignCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.contact.digital.sign.person.notApplicant";

    private final PersonDocumentService personDocumentService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;
    private final FlatService flatService;
    private final CipService cipService;
    private final ContractDigitalSignPersonValidator<ValidatablePersonData> contractDigitalSignPersonValidator;
    private final PersonDocumentUtils personDocumentUtils;
    private final MessageSource messageSource;
    private final ContractDigitalSignMapper contractDigitalSignMapper;

    public ExternalRestCheckContractDigitalSignDto getCheckResult(final String eno) {
        final ContractDigitalSignDocument contractDigitalSignDocument = contractDigitalSignDocumentService
            .findByNotificationEno(eno)
            .orElse(null);

        final List<PersonDocument> personDocumentList = ofNullable(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .filter(owner -> existsNotification(owner, eno))
            .findFirst()
            .map(Owner::getPersonDocumentId)
            .flatMap(personDocumentService::fetchById)
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());

        return check(personDocumentList, contractDigitalSignDocument, contractDigitalSignPersonValidator::validate);
    }

    private boolean existsNotification(final Owner owner, final String eno) {
        return ofNullable(owner)
            .map(Owner::getElkUserNotifications)
            .map(Owner.ElkUserNotifications::getElkUserNotification)
            .orElse(Collections.emptyList())
            .stream()
            .anyMatch(elkUserNotification -> Objects.equals(eno, elkUserNotification.getEno()));
    }

    private ExternalRestCheckContractDigitalSignDto check(
        final List<PersonDocument> personDocumentList,
        final ContractDigitalSignDocument contractDigitalSignDocument,
        final Function<ValidatablePersonData, ValidationResult> validator
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personDocumentUtils
            .mapPersonsToExpandedPersonTypes(personDocumentList);
        if (CollectionUtils.isEmpty(validatablePersonDataList)) {
            return getContractDigitalSignCheckResponseNotApplicantDto();
        }

        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults = validatablePersonDataList
            .stream()
            .map(validatableData -> Pair.of(validatableData, getNewFlats(validatableData)))
            .flatMap(pair -> pair.getRight()
                .stream()
                .map(newFlat -> Pair.of(pair.getLeft(), newFlat))
            )
            .map(pair -> getValidatablePersonData(
                pair.getLeft().getPersonId(),
                pair.getLeft().getPerson(),
                pair.getRight(),
                contractDigitalSignDocument
            ))
            .map(validatablePersonData -> Pair.of(validator.apply(validatablePersonData), validatablePersonData))
            .collect(Collectors.toList());

        final List<ExternalRestApartmentContractDigitalSignDto> apartmentDtos = validationResults.stream()
            .map(pair -> getApartmentDto(pair.getRight(), pair.getLeft()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return getContractDigitalSignCheckResponseDto(apartmentDtos, validationResults);
    }

    private ValidatablePersonData getValidatablePersonData(
        final String personId,
        final PersonType person,
        final PersonType.NewFlatInfo.NewFlat newFlat,
        final ContractDigitalSignDocument contractDigitalSignDocument
    ) {
        final ContractDigitalSignData contractDigitalSignData = ofNullable(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getDocument)
            .map(ContractDigitalSign::getContractDigitalSignData)
            .orElse(null);
        final String contractDigitalSignId = ofNullable(contractDigitalSignDocument)
            .map(ContractDigitalSignDocument::getId)
            .orElse(null);

        final CipType cipData = PersonUtils.getCipId(person)
            .flatMap(cipService::fetchById)
            .map(CipDocument::getDocument)
            .map(Cip::getCipData)
            .orElse(null);

        return ValidatablePersonData.builder()
            .person(person)
            .personId(personId)
            .newFlat(newFlat)
            .contractDigitalSignId(contractDigitalSignId)
            .contractDigitalSignData(contractDigitalSignData)
            .cipData(cipData)
            .build();
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

    private ExternalRestCheckContractDigitalSignDto getContractDigitalSignCheckResponseNotApplicantDto() {
        final ExternalRestCheckContractDigitalSignDto checkDto = new ExternalRestCheckContractDigitalSignDto();
        checkDto.setIsPossible(false);
        checkDto.setReason(messageSource.getMessage(NOT_APPLICANT_MESSAGE, null, Locale.getDefault()));
        return checkDto;
    }

    private ExternalRestApartmentContractDigitalSignDto getApartmentDto(
        final ValidatablePersonData validatablePersonData, final ValidationResult validationResult
    ) {
        if (!validationResult.isValid()
            && shouldIgnoreApartment(validationResult, validatablePersonData)) {
            return null;
        }

        final CipDocument cipDocument = PersonUtils.getCipId(validatablePersonData.getPerson())
            .flatMap(cipService::fetchById)
            .orElse(null);

        final Owner owner = ofNullable(validatablePersonData.getContractDigitalSignData())
            .map(ContractDigitalSignData::getOwners)
            .map(ContractDigitalSignData.Owners::getOwner)
            .orElse(Collections.emptyList())
            .stream()
            .filter(person -> Objects.equals(person.getPersonDocumentId(), validatablePersonData.getPersonId()))
            .findFirst()
            .orElse(null);

        return contractDigitalSignMapper.toExternalRestApartmentDto(
            validatablePersonData.getContractDigitalSignId(),
            owner,
            cipDocument,
            PersonUtils.getNewFlatAddress(validatablePersonData.getNewFlat()),
            validationResult.isValid(),
            validationResult.getFirstValidationMessage()
        );
    }

    private boolean shouldIgnoreApartment(
        final ValidationResult validationResult,
        final ValidatablePersonData validatablePersonData
    ) {
        final ContractDigitalSignData contractDigitalSignData = validatablePersonData.getContractDigitalSignData();
        final CipType cipData = validatablePersonData.getCipData();

        final String notAdultMessage = messageSource.getMessage(
            ContractDigitalSignPersonValidator.NOT_ADULT_MESSAGE, null, Locale.getDefault()
        );
        final String notOwnerMessage = messageSource.getMessage(
            ContractDigitalSignPersonValidator.NOT_OWNER_MESSAGE,
            new Object[]{
                Optional.ofNullable(cipData)
                    .map(CipType::getPhone)
                    .orElse(null)
            },
            Locale.getDefault()
        );
        final String contractSigningPeriodIncorrectMessage = messageSource.getMessage(
            ContractDigitalSignPersonValidator.CONTRACT_SIGNING_PERIOD_INCORRECT_MESSAGE,
            new Object[]{
                Optional.ofNullable(contractDigitalSignData)
                    .map(ContractDigitalSignData::getAppointmentDate)
                    .map(dateTime -> dateTime.format(ContractDigitalSignPersonValidator.DATE_FORMATTER))
                    .orElse(null)
            },
            Locale.getDefault()
        );
        final String electronicSignImpossibleMessage = messageSource.getMessage(
            ContractDigitalSignPersonValidator.ELECTRONIC_SIGN_IMPOSSIBLE_MESSAGE, null, Locale.getDefault()
        );

        final String joinedValidationMessage = validationResult.getJoinedValidationMessage();
        return joinedValidationMessage.contains(notAdultMessage)
            || joinedValidationMessage.contains(notOwnerMessage)
            || joinedValidationMessage.contains(contractSigningPeriodIncorrectMessage)
            || joinedValidationMessage.contains(electronicSignImpossibleMessage);
    }

    private ExternalRestCheckContractDigitalSignDto getContractDigitalSignCheckResponseDto(
        final List<ExternalRestApartmentContractDigitalSignDto> apartmentDtos,
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        final Boolean isPossible = validationResults.stream()
            .anyMatch(pair -> Boolean.TRUE.equals(pair.getLeft().isValid()));

        final ExternalRestCheckContractDigitalSignDto checkDto = new ExternalRestCheckContractDigitalSignDto();

        if (!isPossible) {
            final String reason = validationResults.stream()
                .map(Pair::getLeft)
                .map(GenericValidationResult::getFirstValidationMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
            checkDto.setReason(reason);
        }

        checkDto.setIsPossible(isPossible);
        checkDto.setApartments(apartmentDtos);
        return checkDto;
    }
}
