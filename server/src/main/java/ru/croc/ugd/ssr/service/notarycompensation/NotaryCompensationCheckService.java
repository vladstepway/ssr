package ru.croc.ugd.ssr.service.notarycompensation;

import static java.util.Optional.ofNullable;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestAffairNotaryCompensationDto;
import ru.croc.ugd.ssr.generated.dto.notarycompensation.ExternalRestCheckNotaryCompensationDto;
import ru.croc.ugd.ssr.mapper.NotaryCompensationMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.notarycompensation.NotaryCompensationPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.GenericValidationResult;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotaryCompensationCheckService {

    private static final String NOT_APPLICANT_MESSAGE = "validate.notary.compensation.person.notApplicant";

    private final PersonDocumentService personDocumentService;
    private final NotaryCompensationPersonValidator<ValidatablePersonData> notaryCompensationPersonValidator;
    private final PersonDocumentUtils personDocumentUtils;
    private final MessageSource messageSource;
    private final NotaryCompensationMapper notaryCompensationMapper;
    private final RealEstateDocumentService realEstateDocumentService;

    public ExternalRestCheckNotaryCompensationDto getCheckResult(final String snils, final String ssoId) {
        final List<PersonDocument> personDocumentList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        return check(personDocumentList, notaryCompensationPersonValidator::validate);
    }

    private ExternalRestCheckNotaryCompensationDto check(
        final List<PersonDocument> personDocumentList,
        final Function<ValidatablePersonData, ValidationResult> validator
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personDocumentUtils
            .mapPersonsToExpandedPersonTypes(personDocumentList);
        if (CollectionUtils.isEmpty(validatablePersonDataList)) {
            return getNotaryCompensationCheckResponseNotApplicantDto();
        }

        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults = validatablePersonDataList
            .stream()
            .map(validatablePersonData -> Pair.of(validator.apply(validatablePersonData), validatablePersonData))
            .collect(Collectors.toList());

        final List<ExternalRestAffairNotaryCompensationDto> affairDtos = validationResults
            .stream()
            .map(pair -> getAffairDto(pair.getRight(), pair.getLeft()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return getNotaryCompensationCheckResponseDto(affairDtos, validationResults);
    }

    private ExternalRestCheckNotaryCompensationDto getNotaryCompensationCheckResponseNotApplicantDto() {
        final ExternalRestCheckNotaryCompensationDto checkDto = new ExternalRestCheckNotaryCompensationDto();
        checkDto.setIsPossible(false);
        checkDto.setReason(messageSource.getMessage(NOT_APPLICANT_MESSAGE, null, Locale.getDefault()));
        return checkDto;
    }

    private ExternalRestAffairNotaryCompensationDto getAffairDto(
        final ValidatablePersonData validatablePersonData, final ValidationResult validationResult
    ) {
        final List<PersonDocument> invitedPersonDocuments = personDocumentService
            .getOtherFlatOwners(validatablePersonData.getPersonId(), validatablePersonData.getPerson(), true);

        return notaryCompensationMapper.toRestAffairDto(
            validatablePersonData,
            invitedPersonDocuments,
            validationResult.isValid(),
            validationResult.getFirstValidationMessage(),
            this::retrieveAddress
        );
    }

    private String retrieveAddress(final BigInteger unom) {
        return ofNullable(unom)
            .map(BigInteger::toString)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData)
            .map(RealEstateUtils::getRealEstateAddress)
            .orElse(null);
    }

    private ExternalRestCheckNotaryCompensationDto getNotaryCompensationCheckResponseDto(
        final List<ExternalRestAffairNotaryCompensationDto> affairDtos,
        final List<Pair<ValidationResult, ValidatablePersonData>> validationResults
    ) {
        final Boolean isPossible = validationResults.stream()
            .anyMatch(pair -> Boolean.TRUE.equals(pair.getLeft().isValid()));

        final ExternalRestCheckNotaryCompensationDto checkDto = new ExternalRestCheckNotaryCompensationDto();

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
        checkDto.setAffairs(affairDtos);
        return checkDto;
    }
}
