package ru.croc.ugd.ssr.service.commissioninspection;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.enums.SsrTradeType.ADDITIONAL;
import static ru.croc.ugd.ssr.utils.PersonUtils.getLastAcceptedAgreementOfferLetter;
import static ru.croc.ugd.ssr.utils.PersonUtils.getLastOfferLetter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.commission.CommissionInspectionData;
import ru.croc.ugd.ssr.dto.commissioninspection.CommissionInspectionCheckResponse;
import ru.croc.ugd.ssr.exception.PersonNotFoundException;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionApartmentToDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionLetterDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionPersonInfoDto;
import ru.croc.ugd.ssr.generated.dto.RestCommissionInspectionTradeTypeDto;
import ru.croc.ugd.ssr.mapper.CommissionInspectionCheckMapper;
import ru.croc.ugd.ssr.model.CipDocument;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.commissioninspection.CommissionInspectionDocument;
import ru.croc.ugd.ssr.model.integration.etpmv.ArrayOfBaseDeclarant;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateData;
import ru.croc.ugd.ssr.model.integration.etpmv.CoordinateMessage;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestContact;
import ru.croc.ugd.ssr.model.integration.etpmv.RequestServiceForSign;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.mq.listener.InvalidCoordinateMessage;
import ru.croc.ugd.ssr.service.CapitalConstructionObjectService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.cip.CipService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.CommissionInspectionPersonValidator;
import ru.croc.ugd.ssr.service.validator.impl.GeneralCommissionInspectionPersonValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData.ValidatablePersonDataBuilder;
import ru.croc.ugd.ssr.service.validator.model.ValidationObjectResult;
import ru.croc.ugd.ssr.service.validator.model.ValidationResult;
import ru.croc.ugd.ssr.trade.PersonInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.trade.TradeType;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.PersonUtils;
import ru.mos.gu.service._0834.ServiceProperties;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

/**
 * DefaultCommissionInspectionService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultCommissionInspectionCheckService implements CommissionInspectionCheckService {

    private static final String NOT_OWNER_MESSAGE = "validate.commissionInspection.person.notOwnerOrTenant";

    private final CapitalConstructionObjectService capitalConstructionObjectService;
    private final PersonDocumentService personDocumentService;
    private final CipService cipService;
    private final FlatService flatService;
    private final PersonDocumentUtils personDocumentUtils;
    private final CommissionInspectionPersonValidator<ValidatablePersonData> commissionInspectionPersonValidator;
    private final GeneralCommissionInspectionPersonValidator<ValidatablePersonData>
        generalCommissionInspectionPersonValidator;
    private final CommissionInspectionCheckMapper commissionInspectionCheckMapper;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final MessageSource messageSource;
    private String notOwnerMessage;

    @PostConstruct
    private void translateNotOwnerMessage() {
        try {
            this.notOwnerMessage = messageSource.getMessage(NOT_OWNER_MESSAGE, null, Locale.getDefault());
        } catch (Exception e) {
            this.notOwnerMessage = null;
        }
    }

    @Override
    public CommissionInspectionCheckResponse checkPerson(String snils, String ssoId) {
        final List<PersonDocument> personDocumentDataList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);
        final Map<String, PersonType> personTypeIdMap = personDocumentUtils.mapPersonIdToPerson(personDocumentDataList);
        return validatePersonTypesAndGetInformation(personTypeIdMap);
    }

    private CommissionInspectionCheckResponse validatePersonTypesAndGetInformation(
        final Map<String, PersonType> personTypeIdMap
    ) {
        if (personTypeIdMap.isEmpty()) {
            throw generalCommissionInspectionPersonValidator.getNoObjectExceptionSupplier().get();
        }

        getValidatablePersonDataStream(personTypeIdMap)
            .forEach(generalCommissionInspectionPersonValidator::validate);

        final List<ValidatablePersonData> validatablePersonDataList = personTypeIdMap
            .entrySet()
            .stream()
            .map(e -> collectApartmentToData(e.getKey(), e.getValue()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        final List<ValidationObjectResult<ValidatablePersonData>> validPersonList =
            validatePersonList(validatablePersonDataList);

        return collectCommissionInspectionForAllFlats(validPersonList);
    }

    private Stream<ValidatablePersonData> getValidatablePersonDataStream(
        final Map<String, PersonType> personTypeIdMap
    ) {
        return personTypeIdMap
            .entrySet()
            .stream()
            .map(e -> new ValidatablePersonData(e.getKey(), e.getValue()));
    }

    private List<ValidationObjectResult<ValidatablePersonData>> validatePersonList(
        final List<ValidatablePersonData> validatablePersonDataList
    ) {
        return validatablePersonDataList.stream()
            .map(personData -> {
                final ValidationResult result = commissionInspectionPersonValidator.validate(personData);
                if (result.isValid()) {
                    return ValidationObjectResult.ok(personData);
                } else {
                    return ValidationObjectResult.fail(result.getFirstValidationMessage(), personData, null);
                }
            })
            .collect(Collectors.toList());
    }

    private List<ValidatablePersonData> collectApartmentToData(
        final String personId,
        final PersonType personData
    ) {
        final List<TradeAdditionDocument> tradeAdditionDocuments = tradeAdditionDocumentService
            .findReceivedApplications(personData.getPersonID(), personData.getAffairId());
        final List<ValidatablePersonData> result = tradeAdditionDocuments
            .stream()
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .map(tradeAddition -> ValidatablePersonData.builder()
                .person(personData)
                .personId(personId)
                .tradeAddition(tradeAddition)
                .build()
            )
            .collect(Collectors.toList());

        final boolean isTradeInTwoYears = tradeAdditionDocuments
            .stream()
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .allMatch(t -> t.getTradeType() == TradeType.TRADE_IN_TWO_YEARS)
            && !tradeAdditionDocuments.isEmpty();

        if (tradeAdditionDocuments.isEmpty() || isTradeInTwoYears) {
            final LocalDate contractSignDate = personDocumentService.getContractSignDate(personData).orElse(null);
            if (isTradeInTwoYears && isTwoYearsExpired(contractSignDate)) {
                return result;
            }

            final PersonType.OfferLetters.OfferLetter lastOfferLetter = getLastOfferLetter(personData)
                .orElse(null);

            final ValidatablePersonDataBuilder validatablePersonDataBuilder = ValidatablePersonData
                .builder()
                .personId(personId)
                .person(personData)
                .offerLetter(lastOfferLetter);

            if (lastOfferLetter != null
                && personData.getNewFlatInfo() != null
                && !CollectionUtils.isEmpty(personData.getNewFlatInfo().getNewFlat())
            ) {
                flatService.loadCcoAddressForNewFlats(personData);

                final List<ValidatablePersonData> validatablePersonDataWithNewFlat = personData.getNewFlatInfo()
                    .getNewFlat()
                    .stream()
                    .filter(newFlat -> isNull(newFlat.getLetterId())
                        || lastOfferLetter.getLetterId().equals(newFlat.getLetterId()))
                    .map(newFlat -> validatablePersonDataBuilder
                        .newFlat(newFlat)
                        .build()
                    )
                    .collect(Collectors.toList());

                if (validatablePersonDataWithNewFlat.isEmpty()) {
                    result.add(validatablePersonDataBuilder.build());
                } else {
                    result.addAll(validatablePersonDataWithNewFlat);
                }
            } else {
                result.add(validatablePersonDataBuilder.build());
            }
        }

        return result;
    }

    private static boolean isTwoYearsExpired(final LocalDate contractSignDate) {
        return nonNull(contractSignDate) && contractSignDate.plusYears(2).isBefore(LocalDate.now());
    }

    private CommissionInspectionCheckResponse collectCommissionInspectionForAllFlats(
        final List<ValidationObjectResult<ValidatablePersonData>> personDataList
    ) {
        final List<CommissionInspectionCheckResponse> items = personDataList
            .stream()
            .map(this::getCommissionInspectionCheckResponse)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        if (items.isEmpty()) {
            throw generalCommissionInspectionPersonValidator.getNoObjectExceptionSupplier().get();
        }
        final CommissionInspectionCheckResponse first = items.get(0);
        if (items.size() > 1) {
            for (int i = 1; i < items.size(); i++) {
                final CommissionInspectionCheckResponse next = items.get(i);
                first.getTo().addAll(next.getTo());
            }
        }
        if (first.getTo().isEmpty()) {
            throw generalCommissionInspectionPersonValidator.getNoObjectExceptionSupplier().get();
        }
        first.setIsPossible(true);
        return filterNotOwnerAndDuplicates(first);
    }

    private CommissionInspectionCheckResponse getCommissionInspectionCheckResponse(
        final ValidationObjectResult<ValidatablePersonData> item
    ) {
        final List<RestCommissionInspectionApartmentToDto> result = new ArrayList<>();
        final ValidatablePersonData validatablePersonData = item.getValidatedObject();
        final PersonType personData = validatablePersonData.getPerson();
        final RestCommissionInspectionPersonInfoDto person = commissionInspectionCheckMapper.toPerson(personData);

        ofNullable(validatablePersonData.getTradeAddition())
            .ifPresent(tradeAddition -> result.add(commissionInspectionCheckMapper.toTradeAddition(
                tradeAddition,
                person,
                needFillCipInfoForTradeAddition(tradeAddition)
                    ? defineCipByOfferLetter(getLastAcceptedAgreementOfferLetter(personData)).orElse(null)
                    : null
            )));

        ofNullable(validatablePersonData.getNewFlat())
            .ifPresent(newFlat -> result.add(
                    commissionInspectionCheckMapper.toApartmentTo(
                        newFlat,
                        personDocumentService.getContractSignDate(personData, newFlat),
                        person,
                        Optional.ofNullable(newFlat.getCcoUnom())
                            .map(BigInteger::toString)
                            .map(capitalConstructionObjectService::getCcoAddressByUnom)
                            .orElse(newFlat.getCcoAddress()))
                )
            );

        ofNullable(validatablePersonData.getOfferLetter())
            .ifPresent(letter -> {
                final CipDocument cip = defineCipByOfferLetter(of(letter)).orElse(null);
                result.add(commissionInspectionCheckMapper.toLetter(letter, cip, person));
            });

        result.forEach(e -> e.setErrorMessage(item.getFirstValidationMessage()));

        return CommissionInspectionCheckResponse.builder()
            .to(result)
            .build();
    }

    private boolean needFillCipInfoForTradeAddition(TradeAdditionType tradeAddition) {
        return tradeAddition.getTradeType().equals(TradeType.COMPENSATION)
            && Objects.equals(
            commissionInspectionCheckMapper.toDefineFlatStatusByTradeAddition(tradeAddition).getId(),
            CommissionInspectionCheckMapper.CHECK_IN.getId()
        );
    }

    private Optional<CipDocument> defineCipByOfferLetter(Optional<PersonType.OfferLetters.OfferLetter> letter) {
        return letter.map(PersonType.OfferLetters.OfferLetter::getIdCIP)
            .map(cipService::fetchDocument);
    }

    private CommissionInspectionCheckResponse filterNotOwnerAndDuplicates(
        final CommissionInspectionCheckResponse commissionInspectionCheckResponse
    ) {
        if (isNull(this.notOwnerMessage)) {
            return commissionInspectionCheckResponse;
        }

        final List<RestCommissionInspectionApartmentToDto> filteredApartmentToDtoList = Optional
            .ofNullable(commissionInspectionCheckResponse.getTo())
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(toDto -> !notOwnerMessage.equals(toDto.getErrorMessage()))
            .collect(Collectors.toList());

        if (filteredApartmentToDtoList.isEmpty()) {
            commissionInspectionCheckResponse.setIsPossible(false);
            commissionInspectionCheckResponse.setReason(notOwnerMessage);
            commissionInspectionCheckResponse.setTo(null);
            return commissionInspectionCheckResponse;
        }

        final List<RestCommissionInspectionApartmentToDto> filteredApartmentToDtoWithoutDuplicatesList =
            deleteDuplicates(filteredApartmentToDtoList);

        commissionInspectionCheckResponse.setTo(filteredApartmentToDtoWithoutDuplicatesList);
        return commissionInspectionCheckResponse;
    }

    private List<RestCommissionInspectionApartmentToDto> deleteDuplicates(
        final List<RestCommissionInspectionApartmentToDto> filteredApartmentToDtoList
    ) {
        final List<RestCommissionInspectionApartmentToDto> filteredApartmentToDtoWithoutLetterList =
            filteredApartmentToDtoList.stream()
                .filter(filteredApartmentToDto -> isNull(filteredApartmentToDto.getLetter()))
                .collect(Collectors.toList());

        return filteredApartmentToDtoList.stream()
            .filter(filteredApartmentToDto -> isNull(filteredApartmentToDto.getLetter())
                || !existsDuplicate(filteredApartmentToDto, filteredApartmentToDtoWithoutLetterList))
            .collect(Collectors.toList());
    }

    private boolean existsDuplicate(
        final RestCommissionInspectionApartmentToDto filteredApartmentToDtoWithLetter,
        final List<RestCommissionInspectionApartmentToDto> filteredApartmentToDtoWithoutLetterList
    ) {
        return filteredApartmentToDtoWithoutLetterList.stream()
            .anyMatch(filteredApartmentToDtoWithoutLetter ->
                equalsByTradeTypeId(filteredApartmentToDtoWithLetter, filteredApartmentToDtoWithoutLetter)
                    && equalsByPerson(filteredApartmentToDtoWithLetter, filteredApartmentToDtoWithoutLetter));
    }

    private boolean equalsByTradeTypeId(
        final RestCommissionInspectionApartmentToDto filteredApartmentToDtoWithLetter,
        final RestCommissionInspectionApartmentToDto filteredApartmentToDtoWithoutLetter
    ) {
        final RestCommissionInspectionTradeTypeDto tradeTypeWithLetter =
            filteredApartmentToDtoWithLetter.getTradeType();
        final RestCommissionInspectionTradeTypeDto tradeTypeWithoutLetter =
            filteredApartmentToDtoWithoutLetter.getTradeType();

        return nonNull(tradeTypeWithLetter) && nonNull(tradeTypeWithoutLetter)
            && nonNull(tradeTypeWithLetter.getId()) && nonNull(tradeTypeWithoutLetter.getId())
            && tradeTypeWithLetter.getId().equals(tradeTypeWithoutLetter.getId());
    }

    private boolean equalsByPerson(
        final RestCommissionInspectionApartmentToDto filteredApartmentToDtoWithLetter,
        final RestCommissionInspectionApartmentToDto filteredApartmentToDtoWithoutLetter
    ) {
        final RestCommissionInspectionPersonInfoDto personWithLetter =
            filteredApartmentToDtoWithLetter.getPerson();
        final RestCommissionInspectionPersonInfoDto personWithoutLetter =
            filteredApartmentToDtoWithoutLetter.getPerson();

        return nonNull(personWithLetter) && nonNull(personWithoutLetter)
            && nonNull(personWithLetter.getId()) && nonNull(personWithoutLetter.getId())
            && personWithLetter.getId().equals(personWithoutLetter.getId())
            && nonNull(personWithLetter.getAffairId()) && nonNull(personWithoutLetter.getAffairId())
            && personWithLetter.getAffairId().equals(personWithoutLetter.getAffairId());
    }

    @Override
    public boolean canCreateApplication(
        final CommissionInspectionDocument commissionInspectionDocument, final PersonDocument person
    ) {
        final PersonType personType = person.getDocument().getPersonData();
        final CommissionInspectionCheckResponse commissionInspectionCheckResponse = checkPerson(
            personType.getSNILS(), personType.getSsoID()
        );

        return commissionInspectionCheckResponse.getIsPossible()
            && canCreateApplication(commissionInspectionDocument, commissionInspectionCheckResponse);
    }

    private boolean canCreateApplication(
        final CommissionInspectionDocument commissionInspectionDocument,
        final CommissionInspectionCheckResponse commissionInspectionCheckResponse
    ) {
        final CommissionInspectionData commissionInspection
            = commissionInspectionDocument.getDocument().getCommissionInspectionData();

        return Objects.nonNull(commissionInspection.getLetter())
            ? canCreateApplicationWithLetter(commissionInspection, commissionInspectionCheckResponse)
            : canCreateApplicationWithAddress(commissionInspection, commissionInspectionCheckResponse);
    }

    private boolean canCreateApplicationWithAddress(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionCheckResponse commissionInspectionCheckResponse
    ) {
        final String ccoUnom = commissionInspection.getCcoUnom();
        final String flatNumber = commissionInspection.getFlatNum();
        return commissionInspectionCheckResponse.getTo().stream()
            .filter(to -> isNull(to.getErrorMessage()))
            .filter(to -> Objects.nonNull(to.getUnom()) && Objects.nonNull(to.getFlatNumber()))
            .anyMatch(to -> to.getUnom().equals(ccoUnom) && to.getFlatNumber().equals(flatNumber));
    }

    private boolean canCreateApplicationWithLetter(
        final CommissionInspectionData commissionInspection,
        final CommissionInspectionCheckResponse commissionInspectionCheckResponse
    ) {
        final String letterId = commissionInspection.getLetter().getId();

        return commissionInspectionCheckResponse.getTo().stream()
            .filter(to -> isNull(to.getErrorMessage()))
            .map(RestCommissionInspectionApartmentToDto::getLetter)
            .filter(Objects::nonNull)
            .map(RestCommissionInspectionLetterDto::getId)
            .filter(Objects::nonNull)
            .anyMatch(id -> id.equals(letterId));
    }

    @Override
    public PersonDocument retrievePerson(final CoordinateMessage coordinateMessage) {
        final RequestContact requestContact = retrieveRequestContact(coordinateMessage);

        final String snils = ofNullable(requestContact)
            .map(RequestContact::getSnils)
            .orElseThrow(RuntimeException::new);
        final String ssoId = requestContact.getSsoId();

        final List<PersonDocument> personList = personDocumentService.findPersonDocumentsWithUniqueId(snils, ssoId);

        if (personList.isEmpty()) {
            throw new PersonNotFoundException(
                String.format("Person with such snils %s and ssoId %s was not found", snils, ssoId)
            );
        }

        if (personList.size() == 1) {
            return personList.get(0);
        }

        final ServiceProperties serviceProperties = retrieveServiceProperties(coordinateMessage);

        if (ADDITIONAL.getCommissionInspectionTypeName().equals(serviceProperties.getTradeType())) {
            final List<TradeAdditionDocument> tradeAdditionDocuments =
                tradeAdditionDocumentService.fetchByNewEstateUnom(serviceProperties.getCcoUnom());

            final List<String> personIds = tradeAdditionDocuments
                .stream()
                .map(TradeAdditionDocument::getDocument)
                .map(TradeAddition::getTradeAdditionTypeData)
                .filter(trade -> trade
                    .getNewEstates()
                    .stream()
                    .anyMatch(estate -> Objects.equals(estate.getFlatNumber(), serviceProperties.getFlatNum())))
                .map(TradeAdditionType::getPersonsInfo)
                .flatMap(List::stream)
                .map(PersonInfoType::getPersonDocumentId)
                .collect(Collectors.toList());

            return personList.stream()
                .filter(personDocument -> personIds.contains(personDocument.getId()))
                .findFirst()
                .orElseThrow(() -> new PersonNotFoundException("Person was not found"));
        } else {
            return personList
                .stream()
                .filter(personDocument -> filterPersonByLetterOrNewFlat(personDocument, serviceProperties))
                .findFirst()
                .orElseThrow(() -> new PersonNotFoundException("Person was not found"));
        }
    }

    private RequestContact retrieveRequestContact(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getContacts)
            .map(ArrayOfBaseDeclarant::getBaseDeclarant)
            .map(List::stream)
            .orElse(Stream.empty())
            .findFirst()
            .map(RequestContact.class::cast)
            .orElseThrow(InvalidCoordinateMessage::new);
    }

    private ServiceProperties retrieveServiceProperties(final CoordinateMessage coordinateMessage) {
        return ofNullable(coordinateMessage)
            .map(CoordinateMessage::getCoordinateDataMessage)
            .map(CoordinateData::getSignService)
            .map(RequestServiceForSign::getCustomAttributes)
            .map(RequestServiceForSign.CustomAttributes::getAny)
            .filter(customAttributes -> customAttributes instanceof ServiceProperties)
            .map(ServiceProperties.class::cast)
            .orElseThrow(InvalidCoordinateMessage::new);
    }

    private boolean filterPersonByLetterOrNewFlat(
        final PersonDocument personDocument, final ServiceProperties serviceProperties
    ) {
        if (nonNull(serviceProperties.getLetterId())) {
            return PersonUtils.getLastAcceptedAgreementOfferLetter(personDocument)
                .map(PersonType.OfferLetters.OfferLetter::getLetterId)
                .filter(serviceProperties.getLetterId()::equals)
                .isPresent();
        }

        return of(personDocument.getDocument())
            .map(Person::getPersonData)
            .map(PersonType::getNewFlatInfo)
            .map(PersonType.NewFlatInfo::getNewFlat)
            .map(List::stream)
            .orElse(Stream.empty())
            .filter(newFlat -> nonNull(newFlat.getCcoUnom()) && nonNull(newFlat.getCcoFlatNum()))
            .anyMatch(newFlat -> Objects.equals(serviceProperties.getCcoUnom(), newFlat.getCcoUnom().toString())
                && Objects.equals(serviceProperties.getFlatNum(), newFlat.getCcoFlatNum()));
    }
}
