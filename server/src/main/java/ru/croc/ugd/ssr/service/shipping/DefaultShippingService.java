package ru.croc.ugd.ssr.service.shipping;

import static java.util.Optional.ofNullable;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECALL_NOT_POSSIBLE;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECORD_RESCHEDULED;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_DECLINED;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_DECLINED_BY_PREFECTURE;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.TECHNICAL_CRASH_RECALL;
import static ru.croc.ugd.ssr.utils.ShippingApplicationUtils.toShippingDateTimeInfo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.croc.ugd.ssr.FlatType;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.RealEstate;
import ru.croc.ugd.ssr.RealEstateDataType;
import ru.croc.ugd.ssr.db.dao.ShippingApplicationDao;
import ru.croc.ugd.ssr.dto.PersonDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.RealEstateDataAndFlatInfoDto;
import ru.croc.ugd.ssr.dto.TradeAdditionFlatInfoDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentFromDto;
import ru.croc.ugd.ssr.dto.shipping.ApartmentToDto;
import ru.croc.ugd.ssr.dto.shipping.BookingInformation;
import ru.croc.ugd.ssr.dto.shipping.CheckResponseApplicantDto;
import ru.croc.ugd.ssr.dto.shipping.MoveShippingDateRequestDto;
import ru.croc.ugd.ssr.exception.InvalidDataInput;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.integration.service.notification.ShippingElkNotificationService;
import ru.croc.ugd.ssr.mapper.CheckBookingInformationMapper;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.RealEstateDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.trade.TradeAdditionDocument;
import ru.croc.ugd.ssr.service.BookingSlotService;
import ru.croc.ugd.ssr.service.DocumentConverterService;
import ru.croc.ugd.ssr.service.FlatService;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.RealEstateDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.trade.TradeAdditionValueDecoder;
import ru.croc.ugd.ssr.service.validator.impl.PersonInternalShippingValidator;
import ru.croc.ugd.ssr.service.validator.impl.PersonShippingValidator;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.service.validator.model.ValidationObjectResult;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.trade.EstateInfoType;
import ru.croc.ugd.ssr.trade.TradeAddition;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;
import ru.croc.ugd.ssr.utils.RealEstateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shipping service.
 */
@Service
@AllArgsConstructor
@Slf4j
public class DefaultShippingService implements ShippingService {
    public static final int WORKING_DAYS_BEFORE_SHIPPING = 14;

    private final PersonDocumentService personDocumentService;
    private final FlatService flatService;
    private final PersonShippingValidator<ValidatablePersonData> personValidator;
    private final PersonInternalShippingValidator<ValidatablePersonData> personInternalValidator;
    private final CheckBookingInformationMapper checkBookingInformationMapper;
    private final ShippingApplicationDao shippingApplicationDao;
    private final DocumentConverterService documentConverterService;
    private final ShippingApplicationDocumentService shippingApplicationDocumentService;
    private final ShippingElkNotificationService shippingElkNotificationService;
    private final PersonDocumentUtils personDocumentUtils;
    private final BookingSlotService bookingSlotService;
    private final TradeAdditionDocumentService tradeAdditionDocumentService;
    private final RealEstateDocumentService realEstateDocumentService;
    private final TradeAdditionValueDecoder tradeAdditionValueDecoder;
    private final ShippingApplicationPdfTransformer shippingApplicationXlsPdfTransformer;

    @Override
    public BookingInformation fetchBookingInformationIfValidForPerson(final String snils, final String ssoId) {
        log.info("Shipping check: start, snils {}, ssoId {}", snils, ssoId);
        final Map<String, PersonType> personTypeIdMap = retrievePersonTypeIdMap(snils, ssoId);
        log.info("Shipping check: retrievePersonTypeIdMap, snils {}, ssoId {}", snils, ssoId);

        final BookingInformation bookingInformation = validatePersonTypesAndGetBookingInformation(
            personTypeIdMap,
            personValidator::validate,
            personValidator.getNoObjectExceptionSupplier()
        );
        log.info("Shipping check: finish, snils {}, ssoId {}", snils, ssoId);
        return bookingInformation;
    }

    @Override
    public BookingInformation internalCheck(final String personDocumentId) {
        log.info("Internal shipping check: personDocumentId {}", personDocumentId);
        final Map<String, PersonType> personTypeIdMap = retrievePersonTypeIdMapByDocumentId(personDocumentId);

        return validatePersonTypesAndGetBookingInformation(
            personTypeIdMap,
            personInternalValidator::validate,
            personInternalValidator.getNoObjectExceptionSupplier()
        );
    }

    private Map<String, PersonType> retrievePersonTypeIdMap(final String snils, final String ssoId) {
        if (snils == null) {
            throw new InvalidDataInput();
        }

        final List<PersonDocument> personDocumentDataList = personDocumentService
            .findPersonDocumentsWithUniqueId(snils, ssoId);

        return personDocumentUtils.mapPersonIdToPerson(personDocumentDataList);
    }

    private Map<String, PersonType> retrievePersonTypeIdMapByDocumentId(final String personDocumentId) {
        if (personDocumentId == null) {
            throw new InvalidDataInput();
        }
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        return personDocumentUtils.mapPersonIdToPerson(Collections.singletonList(personDocument));
    }

    private BookingInformation validatePersonTypesAndGetBookingInformation(
        final Map<String, PersonType> personTypeIdMap,
        final Consumer<ValidatablePersonData> personDataValidator,
        final Supplier<RuntimeException> noObjectExceptionSupplier
    ) {
        final List<ValidatablePersonData> validatablePersonDataList = personTypeIdMap
            .entrySet()
            .stream()
            .map(e -> collectApartmentToData(e.getKey(), e.getValue()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        final List<ValidationObjectResult<ValidatablePersonData>> validPersonList = validateList(
            validatablePersonDataList, personDataValidator, noObjectExceptionSupplier
        );

        final List<ValidatablePersonData> validatableTradePersonData = getValidatablePersonDataStream(validPersonList)
            .filter(validatedObject -> validatedObject.getTradeAddition() != null)
            .collect(Collectors.toList());
        final List<PersonType> personDataList = getValidatablePersonDataStream(validPersonList)
            .filter(validatedObject -> validatedObject.getTradeAddition() == null)
            .map(ValidatablePersonData::getPerson)
            .collect(Collectors.toList());

        if (validatableTradePersonData.isEmpty() && personDataList.isEmpty()) {
            throw new PersonNotValidForShippingException(
                validPersonList.get(0).getFirstValidationMessage(),
                validPersonList.get(0).getCode()
            );
        }

        final List<BookingInformation> tradeBookingInformation =
            getTradeAdditionBookingInformation(validatableTradePersonData);
        final List<BookingInformation> personBookingInformation = collectBookingInformationForAllFlats(personDataList);

        final List<BookingInformation> bookingInformationList = Stream.concat(
            personBookingInformation.stream(),
            tradeBookingInformation.stream()
        ).collect(Collectors.toList());

        return mergeBookingInformation(bookingInformationList);
    }

    private Stream<ValidatablePersonData> getValidatablePersonDataStream(
        final List<ValidationObjectResult<ValidatablePersonData>> validPersonList
    ) {
        return validPersonList.stream()
            .filter(ValidationObjectResult::isValid)
            .map(ValidationObjectResult::getValidatedObject);
    }

    private List<ValidatablePersonData> collectApartmentToData(
        final String personId,
        final PersonType personData
    ) {
        final List<TradeAdditionDocument> tradeAdditionDocuments = tradeAdditionDocumentService
            .findReceivedApplicationsWithContract(
                personData.getPersonID(), personData.getAffairId()
            );
        log.info("Shipping check: findReceivedApplicationsWithContract, person {}", personId);
        if (CollectionUtils.isEmpty(tradeAdditionDocuments)) {
            return Collections.singletonList(ValidatablePersonData
                .builder()
                .personId(personId)
                .person(personData)
                .build());
        }
        return tradeAdditionDocuments
            .stream()
            .map(TradeAdditionDocument::getDocument)
            .map(TradeAddition::getTradeAdditionTypeData)
            .map(tradeAddition -> ValidatablePersonData
                .builder()
                .personId(personId)
                .person(personData)
                .tradeAddition(tradeAddition)
                .build())
            .collect(Collectors.toList());
    }

    private List<ValidationObjectResult<ValidatablePersonData>> validateList(
        final List<ValidatablePersonData> validatableObject,
        final Consumer<ValidatablePersonData> personDataValidator,
        final Supplier<RuntimeException> noObjectExceptionSupplier
    ) {
        if (validatableObject.isEmpty()) {
            throw noObjectExceptionSupplier.get();
        }

        final List<ValidationObjectResult<ValidatablePersonData>> validObjects =
            new ArrayList<>(validatableObject.size());
        for (ValidatablePersonData item : validatableObject) {
            try {
                personDataValidator.accept(item);
                validObjects.add(ValidationObjectResult.ok(item));
            } catch (SsrException e) {
                log.debug("Not valid object: {}, reason: {}", validatableObject, e.getMessage());
                validObjects.add(ValidationObjectResult.fail(e.getMessage(), item, e.getErrorCode()));
            }
        }
        return validObjects;
    }

    private List<BookingInformation> collectBookingInformationForAllFlats(final List<PersonType> personDataList) {
        return personDataList
            .stream()
            .map(this::getBookingInformation)
            .collect(Collectors.toList());
    }

    private BookingInformation mergeBookingInformation(final List<BookingInformation> bookingInformationList) {
        final BookingInformation firstBookingInformation = bookingInformationList.get(0);
        if (bookingInformationList.size() > 1) {
            /*
             * Merge flat infos from all flats to the first one.
             */
            for (int i = 1; i < bookingInformationList.size(); i++) {
                final BookingInformation nextBookingInformation = bookingInformationList.get(i);
                firstBookingInformation.getFromApartment().addAll(nextBookingInformation.getFromApartment());
                firstBookingInformation.getToApartment().addAll(nextBookingInformation.getToApartment());
            }
        }
        return firstBookingInformation;
    }

    private BookingInformation getBookingInformation(final PersonType personData) {
        flatService.loadCcoAddressForNewFlats(personData);

        final RealEstateDataAndFlatInfoDto realEstateDataAndFlatInfoDto
            = flatService.fetchRealEstateAndFlat(personData.getFlatID());

        final FlatType flatOld = ofNullable(realEstateDataAndFlatInfoDto)
            .map(RealEstateDataAndFlatInfoDto::getFlat)
            .orElse(null);
        if (flatOld != null) {
            flatOld.setAddress(RealEstateUtils.getRealEstateAddress(realEstateDataAndFlatInfoDto));
        }
        final LocalDate moveDate = personDocumentService.getPersonMovementDate(personData);
        final PersonDataAndFlatInfoDto infoDto = PersonDataAndFlatInfoDto.builder()
            .personData(personData)
            .flat(flatOld)
            .moveDate(moveDate)
            .build();
        log.info(String.format("Extracting booking information for ssoId: %s, snils: %s",
            infoDto.getPersonData().getSsoID(), infoDto.getPersonData().getSNILS()));
        return checkBookingInformationMapper.toBookingInformationFromPersonDataAndFlat(infoDto);
    }

    private List<BookingInformation> getTradeAdditionBookingInformation(
        final List<ValidatablePersonData> validatablePersonData
    ) {
        return validatablePersonData.stream()
            .map(this::mapTradeAdditionToBookingInformationWithApplicant)
            .collect(Collectors.toList());
    }

    private BookingInformation mapTradeAdditionToBookingInformationWithApplicant(
        final ValidatablePersonData validatablePersonData
    ) {
        final LocalDate moveDate = tradeAdditionDocumentService
            .getMoveDateFromTradeAddition(validatablePersonData.getTradeAddition());
        log.info("Shipping check: getMoveDateFromTradeAddition, person {}", validatablePersonData.getPersonId());
        final PersonDataAndFlatInfoDto infoDto = PersonDataAndFlatInfoDto.builder()
            .personData(validatablePersonData.getPerson())
            .build();
        final CheckResponseApplicantDto applicantDto = checkBookingInformationMapper
            .toCheckResponseApplicantDto(infoDto);
        final BookingInformation tradeAdditionBookingInfo = mapTradeAdditionToBookingInformation(
            validatablePersonData.getTradeAddition(),
            moveDate);
        tradeAdditionBookingInfo.setApplicant(applicantDto);
        tradeAdditionBookingInfo.setIsPossible(true);
        return tradeAdditionBookingInfo;
    }

    private BookingInformation mapTradeAdditionToBookingInformation(
        final TradeAdditionType tradeAdditionType,
        final LocalDate moveDate
    ) {
        final ApartmentFromDto apartmentFromDto =
            Optional.ofNullable(tradeAdditionType.getOldEstate())
                .map(estateInfoType -> extractApartmentFrom(estateInfoType, moveDate))
                .orElse(null);
        final List<ApartmentToDto> apartmentToDtos =
            tradeAdditionType.getNewEstates()
                .stream()
                .map(this::extractApartmentTo)
                .collect(Collectors.toList());
        return BookingInformation
            .builder()
            .fromApartment(Collections.singletonList(apartmentFromDto))
            .toApartment(apartmentToDtos)
            .build();
    }

    private ApartmentToDto extractApartmentTo(final EstateInfoType estateInfoType) {
        if (StringUtils.isEmpty(estateInfoType.getCadNumber())) {
            final String cadNum = tradeAdditionValueDecoder.extractCadNumber(estateInfoType);
            estateInfoType.setCadNumber(cadNum);
        }
        return checkBookingInformationMapper.getApartmentToByEstateInfoType(estateInfoType);
    }

    private ApartmentFromDto extractApartmentFrom(final EstateInfoType estateInfoType, final LocalDate moveDate) {
        final String unom = Optional.ofNullable(estateInfoType)
            .map(EstateInfoType::getUnom)
            .orElse(null);
        final Optional<RealEstateDataType> realEstateDataType = ofNullable(unom)
            .map(realEstateDocumentService::fetchDocumentByUnom)
            .map(RealEstateDocument::getDocument)
            .map(RealEstate::getRealEstateData);
        log.info("ShippingCheck: fetchDocumentByUnom, unom {}", unom);
        final FlatType flatByNumber = realEstateDataType
            .map(RealEstateDataType::getFlats)
            .map(RealEstateDataType.Flats::getFlat)
            .orElse(Collections.emptyList())
            .stream()
            .filter(flatType ->
                StringUtils.equals(flatType.getApartmentL4VALUE(), estateInfoType.getFlatNumber())
                    || StringUtils.equals(flatType.getFlatNumber(), estateInfoType.getFlatNumber())
            )
            .findFirst()
            .orElse(null);
        final TradeAdditionFlatInfoDto tradeAdditionFlatInfoDto = TradeAdditionFlatInfoDto
            .builder()
            .realEstateData(realEstateDataType.orElse(null))
            .flat(flatByNumber)
            .moveDate(moveDate)
            .estateInfoType(estateInfoType)
            .build();

        return checkBookingInformationMapper.getApartmentFromByTradeAdditionData(tradeAdditionFlatInfoDto);
    }

    @Override
    public void declineApplicationById(
        final String applicationId, final String declineReason, final LocalDateTime declineDateTime
    ) {
        final ShippingApplicationDocument shippingApplicationDocument =
            shippingApplicationDocumentService.fetchDocument(applicationId);
        ofNullable(shippingApplicationDocument.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .ifPresent(shippingApplication -> {
                declineApplication(
                    shippingApplication,
                    declineReason,
                    declineDateTime,
                    SHIPPING_DECLINED_BY_PREFECTURE.getDescription()
                );

                shippingApplicationDocumentService
                    .updateDocument(applicationId, shippingApplicationDocument, true, true, null);

                shippingElkNotificationService.sendStatus(SHIPPING_DECLINED_BY_PREFECTURE, shippingApplicationDocument);

                bookingSlotService.removeBooking(shippingApplication.getBookingId());
            });
    }

    @Override
    public void moveShippingDate(
        final String applicationId, final MoveShippingDateRequestDto moveShippingDateRequestDto
    ) {
        final ShippingApplicationDocument shippingApplicationDocument =
            shippingApplicationDocumentService.fetchDocument(applicationId);

        ofNullable(shippingApplicationDocument.getDocument())
            .map(ShippingApplication::getShippingApplicationData)
            .ifPresent(shippingApplication -> {
                changeShippingDate(shippingApplication, moveShippingDateRequestDto);

                shippingApplicationDocumentService
                    .updateDocument(applicationId, shippingApplicationDocument, true, true, null);

                shippingElkNotificationService.sendStatus(RECORD_RESCHEDULED, shippingApplicationDocument);

                bookingSlotService.removeBookedAndBookPreBooked(shippingApplication.getBookingId());
            });
    }

    private void changeShippingDate(
        final ShippingApplicationType shippingApplication, final MoveShippingDateRequestDto moveShippingDateRequestDto
    ) {
        shippingApplication.setBookingId(moveShippingDateRequestDto.getBookingId());
        shippingApplication.setShippingDateStart(moveShippingDateRequestDto.getShippingDateStart());
        shippingApplication.setShippingDateEnd(moveShippingDateRequestDto.getShippingDateEnd());
        shippingApplication.setShippingDateTimeInfo(
            toShippingDateTimeInfo(
                moveShippingDateRequestDto.getShippingDateStart(),
                moveShippingDateRequestDto.getShippingDateEnd()
            )
        );
    }

    @Override
    public byte[] generateShippingApplicationPdfReport(final String documentId) {
        final ShippingApplication shippingApplication = shippingApplicationDocumentService
            .fetchDocument(documentId)
            .getDocument();

        return shippingApplicationXlsPdfTransformer.transformToPdf(shippingApplication);
    }

    @Override
    public void declineApplicationByApplicant(final String eno, final String declineReason) {
        Optional<ShippingApplicationDocument> optionalShippingApplicationDocument = shippingApplicationDao
            .findByEno(eno)
            .map(documentData -> documentConverterService
                .parseDocumentData(
                    documentData,
                    ShippingApplicationDocument.class
                )
            );

        if (optionalShippingApplicationDocument.isPresent()) {
            declineApplicationByApplicant(optionalShippingApplicationDocument.get(), declineReason);
        } else {
            shippingElkNotificationService.sendStatus(TECHNICAL_CRASH_RECALL);
        }
    }

    private void declineApplicationByApplicant(
        final ShippingApplicationDocument shippingApplicationDocument, final String declineReason
    ) {
        final ShippingApplicationType shippingApplication = shippingApplicationDocument
            .getDocument()
            .getShippingApplicationData();
        declineApplication(
            shippingApplication,
            declineReason,
            LocalDateTime.now(),
            SHIPPING_DECLINED.getDescription()
        );

        shippingApplicationDocumentService.updateDocument(
            shippingApplicationDocument.getId(),
            shippingApplicationDocument,
            true,
            true,
            null
        );

        shippingElkNotificationService.sendStatus(RECALL_NOT_POSSIBLE, shippingApplicationDocument);
        shippingElkNotificationService.sendStatus(SHIPPING_DECLINED, shippingApplicationDocument);

        bookingSlotService.removeBooking(shippingApplication.getBookingId());
    }

    private void declineApplication(
        final ShippingApplicationType shippingApplication,
        final String declineReason,
        final LocalDateTime declineDateTime,
        final String status
    ) {
        shippingApplication.setDeclineReason(declineReason);
        shippingApplication.setDeclineDateTime(declineDateTime);
        shippingApplication.setStatus(status);
    }
}
