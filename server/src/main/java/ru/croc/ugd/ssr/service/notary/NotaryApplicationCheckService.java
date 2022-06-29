package ru.croc.ugd.ssr.service.notary;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryClaimDto;
import ru.croc.ugd.ssr.generated.dto.notary.RestNotaryPersonCheckDto;
import ru.croc.ugd.ssr.mapper.NotaryMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.notary.ExternalNotaryPersonValidator;
import ru.croc.ugd.ssr.service.validator.impl.notary.InternalNotaryPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NotaryCheckService.
 */
@Service
@AllArgsConstructor
public class NotaryApplicationCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.notary.notApplicant";

    private final NotaryMapper notaryMapper;
    private final PersonDocumentService personDocumentService;
    private final MessageSource messageSource;
    private final FlatService flatService;
    private final InternalNotaryPersonValidator internalNotaryPersonValidator;
    private final ExternalNotaryPersonValidator externalNotaryPersonValidator;
    private final PersonDocumentUtils personDocumentUtils;
    private final RealEstateDocumentService realEstateDocumentService;

    public RestNotaryPersonCheckDto getInternalCheckResult(final String personId) {
        final List<PersonDocument> personDocumentList = Collections.singletonList(
            personDocumentService.fetchDocument(personId)
        );

        return check(personDocumentList, internalNotaryPersonValidator::validate);
    }

    public RestNotaryPersonCheckDto getExternalCheckResult(final String snils, final String ssoId) {
        final List<PersonDocument> personDocumentList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);

        return check(personDocumentList, externalNotaryPersonValidator::validate);
    }

    private RestNotaryPersonCheckDto check(
        final List<PersonDocument> personDocumentList,
        final Function<ValidatablePersonData, ValidationResult> validator
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personDocumentUtils
            .mapPersonsToExpandedPersonTypes(personDocumentList);
        if (CollectionUtils.isEmpty(validatablePersonDataList)) {
            return getNotaryCheckResponseNotApplicantDto();
        }
        final List<RestNotaryClaimDto> notaryClaimDtos = validatablePersonDataList
            .stream()
            .map(validatablePersonData -> getNotaryClaimDto(
                validatablePersonData,
                validator.apply(validatablePersonData)
            ))
            .collect(Collectors.toList());
        return getNotaryCheckResponseDto(notaryClaimDtos);
    }

    private RestNotaryPersonCheckDto getNotaryCheckResponseNotApplicantDto() {
        final RestNotaryPersonCheckDto notaryPersonCheckDto = new RestNotaryPersonCheckDto();
        notaryPersonCheckDto.setReason(messageSource.getMessage(NOT_APPLICANT_MESSAGE, null, Locale.getDefault()));
        notaryPersonCheckDto.setIsPossible(false);
        return notaryPersonCheckDto;
    }

    private RestNotaryClaimDto getNotaryClaimDto(ValidatablePersonData validatablePersonData,
                                                 ValidationResult validationResult) {
        final PersonType applicant = validatablePersonData.getPerson();
        final RestNotaryClaimDto notaryClaimDto = new RestNotaryClaimDto();
        notaryClaimDto.setAffairId(applicant.getAffairId());

        final String oldAddress = ofNullable(applicant.getUNOM())
            .map(BigInteger::toString)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse(null);

        notaryClaimDto.setApartmentFrom(
            notaryMapper.toRestNotaryApartmentFromDto(oldAddress, applicant)
        );

        if (!validationResult.isValid()) {
            notaryClaimDto.setIsPossible(false);
            notaryClaimDto.setReason(validationResult.getFirstValidationMessage());
            return notaryClaimDto;
        }
        final List<PersonType> invitedPersonTypes = getInvitedPersonTypes(validatablePersonData);
        final List<PersonType.NewFlatInfo.NewFlat> newFlats = getNewFlats(validatablePersonData);

        notaryClaimDto.setIsPossible(true);
        notaryClaimDto.setInvitedApplicants(notaryMapper.toRestApplicantDtoList(invitedPersonTypes));
        notaryClaimDto.setApartmentsTo(notaryMapper.toRestNotaryApartmentToDtoList(newFlats));
        return notaryClaimDto;
    }

    private List<PersonType.NewFlatInfo.NewFlat> getNewFlats(ValidatablePersonData validatablePersonData) {
        final PersonType personType = validatablePersonData.getPerson();
        flatService.loadCcoAddressForNewFlats(personType);

        return ofNullable(personType.getNewFlatInfo())
            .map(PersonType.NewFlatInfo::getNewFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .collect(Collectors.toList());
    }

    private List<PersonType> getInvitedPersonTypes(ValidatablePersonData validatablePersonData) {
        return personDocumentService
            .getOtherFlatOwners(validatablePersonData.getPersonId(), validatablePersonData.getPerson(), true)
            .stream()
            .map(PersonDocument::getDocument)
            .map(Person::getPersonData)
            .collect(Collectors.toList());
    }

    private RestNotaryPersonCheckDto getNotaryCheckResponseDto(List<RestNotaryClaimDto> notaryClaimDtos) {
        final RestNotaryPersonCheckDto notaryPersonCheckDto = new RestNotaryPersonCheckDto();
        notaryPersonCheckDto.claims(notaryClaimDtos);
        final Boolean isPossible = notaryClaimDtos
            .stream()
            .anyMatch(notaryClaimDto -> Boolean.TRUE.equals(notaryClaimDto.getIsPossible()));
        if (!isPossible) {
            notaryPersonCheckDto.setReason(notaryClaimDtos
                .stream()
                .map(RestNotaryClaimDto::getReason)
                .findFirst()
                .orElse(null)
            );
        }
        notaryPersonCheckDto.setIsPossible(isPossible);
        return notaryPersonCheckDto;
    }
}
