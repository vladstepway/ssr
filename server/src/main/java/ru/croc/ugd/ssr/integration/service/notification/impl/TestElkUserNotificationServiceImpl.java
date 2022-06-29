package ru.croc.ugd.ssr.integration.service.notification.impl;

import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.Person;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.contractappointment.ContractAppointment;
import ru.croc.ugd.ssr.contractappointment.ContractAppointmentData;
import ru.croc.ugd.ssr.dto.contractappointment.ContractAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.contractdigitalsign.ContractDigitalSignFlowStatus;
import ru.croc.ugd.ssr.dto.flatappointment.FlatAppointmentFlowStatus;
import ru.croc.ugd.ssr.dto.notary.NotaryApplicationFlowStatus;
import ru.croc.ugd.ssr.dto.notarycompensation.NotaryCompensationFlowStatus;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.exception.SsrException;
import ru.croc.ugd.ssr.exception.StatusNotFoundException;
import ru.croc.ugd.ssr.flatappointment.FlatAppointmentData;
import ru.croc.ugd.ssr.guardianship.GuardianshipRequestData;
import ru.croc.ugd.ssr.integration.service.notification.ContractAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.ContractDigitalSignElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.ElkUserNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.FlatAppointmentElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.NotaryApplicationElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.PersonalDocumentRequestElkNotificationService;
import ru.croc.ugd.ssr.integration.service.notification.TestElkUserNotificationService;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.model.ShippingApplicationDocument;
import ru.croc.ugd.ssr.model.contractappointment.ContractAppointmentDocument;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.model.notary.NotaryDocument;
import ru.croc.ugd.ssr.model.personaldocument.PersonalDocumentRequestDocument;
import ru.croc.ugd.ssr.notary.Apartments;
import ru.croc.ugd.ssr.notary.Notary;
import ru.croc.ugd.ssr.notary.NotaryApplicationType;
import ru.croc.ugd.ssr.notary.NotaryPhone;
import ru.croc.ugd.ssr.notary.NotaryType;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ContractAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.ContractDigitalSignDocumentService;
import ru.croc.ugd.ssr.service.document.EgrnFlatRequestDocumentService;
import ru.croc.ugd.ssr.service.document.FlatAppointmentDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.NotaryDocumentService;
import ru.croc.ugd.ssr.service.document.PersonalDocumentRequestDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService;
import ru.croc.ugd.ssr.service.guardianship.RestGuardianshipService;
import ru.croc.ugd.ssr.service.trade.model.TradeAdditionNotificationData;
import ru.croc.ugd.ssr.shipping.Apartment;
import ru.croc.ugd.ssr.shipping.ShippingApplication;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.trade.TradeApplicationFileType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Имплементация TestElkUserNotificationService.
 */
@Service
@RequiredArgsConstructor
public class TestElkUserNotificationServiceImpl implements TestElkUserNotificationService {

    private final ElkUserNotificationService elkUserNotificationService;
    private final PersonDocumentService personDocumentService;
    private final RestGuardianshipService restGuardianshipService;
    private final NotaryApplicationElkNotificationService notaryApplicationElkNotificationService;
    private final ContractAppointmentElkNotificationService contractAppointmentElkNotificationService;
    private final ContractDigitalSignElkNotificationService contractDigitalSignElkNotificationService;
    private final FlatAppointmentElkNotificationService flatAppointmentElkNotificationService;
    private final ContractAppointmentDocumentService contractAppointmentDocumentService;
    private final FlatAppointmentDocumentService flatAppointmentDocumentService;
    private final ShippingApplicationDocumentService shippingApplicationDocumentService;
    private final NotaryApplicationDocumentService notaryApplicationDocumentService;
    private final NotaryDocumentService notaryDocumentService;
    private final PersonalDocumentRequestElkNotificationService personalDocumentRequestElkNotificationService;
    private final PersonalDocumentRequestDocumentService personalDocumentRequestDocumentService;
    private final EgrnFlatRequestDocumentService egrnFlatRequestDocumentService;
    private final EgrnFlatRequestService egrnFlatRequestService;
    private final ContractDigitalSignDocumentService contractDigitalSignDocumentService;

    @Override
    public void testStartRenovationElkRequest(String ssoId, String personId, String cipId) {
        elkUserNotificationService.sendNotificationStartRenovation(retrievePerson(ssoId, personId),
            !StringUtils.isEmpty(personId), cipId);
    }

    @Override
    public void testOfferLetterElkRequest(
        final String ssoId, final String personId, final String cipId, final String letterId
    ) {
        elkUserNotificationService.sendNotificationOfferLetter(
            retrievePerson(ssoId, personId),
            !StringUtils.isEmpty(personId),
            cipId,
            letterId,
            "8fbc8b87-1c8a-4a8c-b7aa-13681f08db4c"
        );
    }

    @Override
    public void testTradeAdditionElkRequest(String eventCode, String ssoId, String personDocumentId, String cipId) {
        testTradeAdditionElkRequest(eventCode, ssoId, personDocumentId, cipId, null);
    }

    @Override
    public void testTradeAdditionElkRequest(String eventCode, String ssoId,
                                            String personDocumentId, String cipId, String chedId) {
        PersonDocument testPerson = retrievePerson(ssoId, personDocumentId);
        if (StringUtils.isEmpty(personDocumentId)) {
            testPerson.getDocument().getPersonData().setOfferLetters(new PersonType.OfferLetters());
            testPerson.getDocument().getPersonData().getOfferLetters().getOfferLetter().add(createOfferLetter(cipId));
        }
        TradeApplicationFileType tradeApplicationFileType = null;
        if (chedId != null) {
            tradeApplicationFileType = new TradeApplicationFileType();
            tradeApplicationFileType.setChedId(chedId);
        }
        elkUserNotificationService.sendTradeAdditionNotification(
            TradeAdditionNotificationData
                .findByEventCode(eventCode)
                .orElseThrow(StatusNotFoundException::new),
            tradeApplicationFileType,
            testPerson
        );
    }

    @Override
    public void testFlatInspectionElkRequest(String ssoId, String personId, String cipId) {
        elkUserNotificationService.sendNotificationFlatInspection(
            retrievePerson(ssoId, personId), !StringUtils.isEmpty(personId), cipId, "",
            "8fbc8b87-1c8a-4a8c-b7aa-13681f08db4c"
        );
    }

    @Override
    public void testFlatAgreementElkRequest(String ssoId, String personId, String cipId) {
        elkUserNotificationService.sendNotificationFlatAgreement(retrievePerson(ssoId, personId), null,
            !StringUtils.isEmpty(personId));
    }

    @Override
    public void testFlatRefusalElkRequest(String ssoId, String personId, String cipId) {
        elkUserNotificationService.sendNotificationFlatRefusal(retrievePerson(ssoId, personId),
            !StringUtils.isEmpty(personId), cipId, null);
    }

    @Override
    public void testSignedContractElkRequest(String ssoId, String personId, String cipId, String flatId) {
        PersonDocument testPerson = retrievePerson(ssoId, personId);
        if (StringUtils.isEmpty(personId)) {
            testPerson.getDocument().getPersonData().setFlatID(flatId);
        }
        elkUserNotificationService.sendNotificationSignedContract(
            testPerson,
            !StringUtils.isEmpty(personId),
            cipId,
            "Москва, улица Тестовая-Уголовная, дом №7",
            "Москва, улица Тестовая-Новая, дом №6",
            "Москва, улица Тестовая-Новая, дом №5",
            "",
            null
        );
    }

    @Override
    public void testContractReadyElkRequest(String ssoId, String personId, String cipId) {
        PersonDocument testPerson = retrievePerson(ssoId, personId);
        if (StringUtils.isEmpty(personId)) {
            populateOfferLetter(testPerson, cipId);
        }
        elkUserNotificationService.sendNotificationContractReady(
            testPerson, !StringUtils.isEmpty(personId), "", null
        );
    }

    @Override
    public void sendContractReadyForAcquaintingNotification(String ssoId,
                                                            String personId, String cipId) {
        PersonDocument testPerson = retrievePerson(ssoId, personId);
        if (StringUtils.isEmpty(personId)) {
            populateOfferLetter(testPerson, cipId);
        }
        elkUserNotificationService.sendContractReadyForAcquaintingNotification(testPerson);
    }

    @Override
    public void sendApartmentInspectionNotification(
        String ssoId,
        String personDocumentId,
        String cipId,
        String apartmentInspectionId
    ) {
        PersonDocument testPerson = retrievePerson(ssoId, personDocumentId);
        if (StringUtils.isEmpty(personDocumentId)) {
            populateOfferLetter(testPerson, cipId);
        }
        elkUserNotificationService.sendNotificationFixDefects(testPerson, cipId, apartmentInspectionId);
    }

    @Override
    public void sendShippingNotification(
        final String shippingDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final LocalDateTime moveDateTime,
        final String oldAddress,
        final String newAddress
    ) {
        final ShippingFlowStatus status = ShippingFlowStatus.ofEventCode(eventCode)
            .orElseThrow(StatusNotFoundException::new);
        final PersonDocument personDocument = retrievePerson(ssoId, personDocumentId);

        final ShippingApplicationDocument shippingApplicationDocument =
            retrieveShippingApplication(shippingDocumentId, oldAddress, newAddress, moveDateTime);

        elkUserNotificationService.sendShippingNotification(shippingApplicationDocument, status, personDocument);
    }

    private ShippingApplicationDocument retrieveShippingApplication(
        String shippingDocumentId,
        String oldAddress,
        String newAddress,
        LocalDateTime moveDateTime
    ) {
        if (StringUtils.isNotEmpty(shippingDocumentId)) {
            return shippingApplicationDocumentService.fetchDocument(shippingDocumentId);
        }
        ShippingApplicationDocument shippingApplicationDocument = new ShippingApplicationDocument();

        ShippingApplication shippingApplication = new ShippingApplication();
        shippingApplicationDocument.setDocument(shippingApplication);

        ShippingApplicationType shippingApplicationType = new ShippingApplicationType();
        shippingApplication.setShippingApplicationData(shippingApplicationType);

        Apartment apartmentFrom = new Apartment();
        apartmentFrom.setStringAddress(oldAddress);
        shippingApplicationType.setApartmentFrom(apartmentFrom);

        Apartment apartmentTo = new Apartment();
        apartmentTo.setStringAddress(newAddress);
        shippingApplicationType.setApartmentFrom(apartmentTo);

        shippingApplicationType.setShippingDateStart(moveDateTime);

        return shippingApplicationDocument;
    }

    @Override
    public void sendGuardianshipNotification(
        final boolean isAgreement,
        final String ssoId,
        final String personDocumentId,
        final String declineReason
    ) {
        final PersonDocument personDocument = retrievePerson(ssoId, personDocumentId);

        final GuardianshipRequestData guardianshipRequestData = new GuardianshipRequestData();
        guardianshipRequestData.setDeclineReason(declineReason);

        final Map<String, String> notificationParams = restGuardianshipService
            .getNotificationTemplateParams(guardianshipRequestData);
        if (isAgreement) {
            elkUserNotificationService.sendGuardianshipAgreementNotification(personDocument, notificationParams);
        } else {
            elkUserNotificationService.sendGuardianshipDeclineNotification(personDocument, notificationParams);
        }
    }

    @Override
    public void sendNotaryNotification(
        final String notaryApplicationDocumentId,
        final String notaryDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String notaryFullName,
        final String notaryPhones,
        final String eno,
        final String addressFrom,
        final String addressesTo,
        final LocalDateTime appointmentDateTime
    ) {
        final PersonDocument requesterPersonDocument = retrievePerson(ssoId, personDocumentId);
        final PersonType requesterPerson = requesterPersonDocument.getDocument().getPersonData();

        final NotaryApplicationFlowStatus notaryApplicationStatus = NotaryApplicationFlowStatus.ofEventCode(eventCode)
            .orElseThrow(StatusNotFoundException::new);

        final NotaryApplicationType notaryApplication = retrieveNotaryApplication(
            notaryApplicationDocumentId,
            eno,
            addressFrom,
            addressesTo,
            appointmentDateTime
        );
        final NotaryType notary = retrieveNotary(notaryDocumentId, notaryFullName, notaryPhones);

        final Map<String, String> extraTemplateParams = notaryApplicationElkNotificationService
            .getExtraTemplateParams(
                notaryApplication,
                requesterPerson,
                requesterPerson,
                notary
            );

        elkUserNotificationService.sendNotaryNotification(
            requesterPersonDocument,
            notaryApplicationStatus,
            extraTemplateParams
        );
    }

    @Override
    public void sendNotaryCompensationNotification(final String ssoId, final String personDocumentId) {
        final PersonDocument personDocument = retrievePerson(ssoId, personDocumentId);

        elkUserNotificationService.sendNotaryCompensationNotification(
            personDocument, NotaryCompensationFlowStatus.COMPENSATION_OPPORTUNITY, Collections.emptyMap()
        );
    }

    @Override
    public void sendNotaryCompensationNotification(
        final String notaryApplicationDocumentId,
        final String notaryDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final LocalDateTime appointmentDateTime,
        final String eno
    ) {
        final PersonDocument ownerPersonDocument = retrievePerson(ssoId, personDocumentId);
        final PersonType requesterPerson = ownerPersonDocument.getDocument().getPersonData();

        final NotaryCompensationFlowStatus flowStatus = NotaryCompensationFlowStatus.ofEventCode(eventCode)
            .orElseThrow(() -> new SsrException("Некорректный код уведомления"));

        final NotaryApplicationType notaryApplication = retrieveNotaryApplication(
            notaryApplicationDocumentId,
            eno,
            appointmentDateTime
        );
        final NotaryType notary = retrieveNotary(notaryDocumentId);

        elkUserNotificationService.sendNotaryCompensationNotification(
            ownerPersonDocument,
            flowStatus,
            notaryApplicationElkNotificationService.getExtraTemplateParams(
                notaryApplication,
                requesterPerson,
                ownerPersonDocument.getDocument().getPersonData(),
                notary
            )
        );
    }

    @Override
    public void sendContractAppointmentNotification(
        final String contactAppointmentDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String cipId,
        final String cancelReason,
        final String addressTo,
        final LocalDateTime appointmentDateTime
    ) {
        final PersonDocument requesterPersonDocument = retrievePerson(ssoId, personDocumentId);
        final PersonType requesterPerson = requesterPersonDocument.getDocument().getPersonData();

        final ContractAppointmentData contractAppointmentData = retrieveContractAppointmentDocument(
            contactAppointmentDocumentId,
            cipId,
            cancelReason,
            addressTo,
            appointmentDateTime
        );

        final Map<String, String> extraTemplateParams = contractAppointmentElkNotificationService
            .getExtraTemplateParams(contractAppointmentData, requesterPerson, requesterPerson);

        final ContractAppointmentFlowStatus contractAppointmentFlowStatus = ContractAppointmentFlowStatus
            .ofEventCode(eventCode);

        elkUserNotificationService.sendContractAppointmentNotification(
            requesterPersonDocument,
            contractAppointmentFlowStatus,
            extraTemplateParams
        );
    }

    @Override
    public void sendFlatAppointmentNotification(
        final String flatAppointmentDocumentId,
        final String eventCode,
        final String ssoId,
        final String personDocumentId,
        final String cipId,
        final String cancelReason,
        final LocalDateTime appointmentDateTime
    ) {
        final PersonDocument requesterPersonDocument = retrievePerson(ssoId, personDocumentId);
        final PersonType requesterPerson = requesterPersonDocument.getDocument().getPersonData();

        final FlatAppointmentData flatAppointmentData = retrieveFlatAppointmentDocument(
            flatAppointmentDocumentId,
            cipId,
            cancelReason,
            appointmentDateTime
        );

        final Map<String, String> extraTemplateParams = flatAppointmentElkNotificationService
            .getExtraTemplateParams(flatAppointmentData, requesterPerson, requesterPerson);

        final FlatAppointmentFlowStatus flatAppointmentFlowStatus = FlatAppointmentFlowStatus
            .ofEventCode(eventCode);

        elkUserNotificationService.sendFlatAppointmentNotification(
            requesterPersonDocument,
            flatAppointmentFlowStatus,
            extraTemplateParams
        );
    }

    @Override
    public void sendPersonalDocumentRequestNotification(
        final String ssoId, final String personDocumentId, final String personalDocumentRequestDocumentId
    ) {
        final PersonDocument requesterPersonDocument = retrievePerson(ssoId, personDocumentId);
        final PersonalDocumentRequestDocument personalDocumentRequestDocument = personalDocumentRequestDocumentService
            .fetchDocument(personalDocumentRequestDocumentId);

        personalDocumentRequestElkNotificationService.sendNotification(
            requesterPersonDocument, personalDocumentRequestDocument
        );
    }

    @Override
    public void sendResidencePlaceRegistrationNotification(final String personDocumentId) {
        final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
        elkUserNotificationService.sendResidencePlaceRegistrationNotification(personDocument);
    }

    @Override
    public void sendResidencePlaceRegistrationNotificationByEgrn(final String egrnFlatRequestDocumentId) {
        final EgrnFlatRequestDocument egrnFlatRequestDocument = egrnFlatRequestDocumentService.fetchDocument(
            egrnFlatRequestDocumentId
        );
        egrnFlatRequestService.sendResidencePlaceRegistrationNotificationIfNeeded(egrnFlatRequestDocument);
    }

    @Override
    public void sendContractDigitalSignRequestNotificationToApplicant(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String contractDigitalSignDocumentId
    ) {
        final PersonDocument notifiablePersonDocument = retrievePerson(ssoId, notifiablePersonDocumentId);

        sendContractDigitalSignNotification(
            notifiablePersonDocument,
            null,
            contractDigitalSignDocumentId,
            ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_APPLICANT
        );
    }

    @Override
    public void sendContractDigitalSignRequestNotificationToOwner(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String requesterPersonDocumentId,
        final String contractDigitalSignDocumentId
    ) {
        final PersonDocument notifiablePersonDocument = retrievePerson(ssoId, notifiablePersonDocumentId);

        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(requesterPersonDocumentId);

        sendContractDigitalSignNotification(
            notifiablePersonDocument,
            requesterPersonDocument,
            contractDigitalSignDocumentId,
            ContractDigitalSignFlowStatus.COLLECT_SIGNATURES_OWNER
        );
    }

    @Override
    public void sendContractDigitalSignRequestNotification(
        final String ssoId,
        final String notifiablePersonDocumentId,
        final String requesterPersonDocumentId,
        final String contractDigitalSignDocumentId,
        final String eventCode
    ) {
        final PersonDocument notifiablePersonDocument = retrievePerson(ssoId, notifiablePersonDocumentId);

        final PersonDocument requesterPersonDocument = personDocumentService.fetchDocument(requesterPersonDocumentId);

        final ContractDigitalSignFlowStatus contractDigitalSignFlowStatus =
            ContractDigitalSignFlowStatus.ofEventCode(eventCode);

        sendContractDigitalSignNotification(
            notifiablePersonDocument,
            requesterPersonDocument,
            contractDigitalSignDocumentId,
            contractDigitalSignFlowStatus
        );
    }

    private void sendContractDigitalSignNotification(
        final PersonDocument notifiablePersonDocument,
        final PersonDocument requesterPersonDocument,
        final String contractDigitalSignDocumentId,
        final ContractDigitalSignFlowStatus status
    ) {
        ofNullable(contractDigitalSignDocumentId)
            .map(contractDigitalSignDocumentService::fetchDocument)
            .flatMap(contractAppointmentDocumentService::findByContractDigitalSign)
            .map(ContractAppointmentDocument::getDocument)
            .map(ContractAppointment::getContractAppointmentData)
            .ifPresent(contractAppointmentData ->
                elkUserNotificationService.sendContractDigitalSignNotification(
                    notifiablePersonDocument,
                    status,
                    contractDigitalSignElkNotificationService.getExtraTemplateParams(
                        contractAppointmentData,
                        ofNullable(requesterPersonDocument)
                            .map(PersonDocument::getDocument)
                            .map(Person::getPersonData)
                            .orElse(null)
                    )
                )
            );
    }

    private FlatAppointmentData retrieveFlatAppointmentDocument(
        final String flatAppointmentDocumentId,
        final String cipId,
        final String cancelReason,
        final LocalDateTime appointmentDateTime
    ) {
        if (StringUtils.isNotEmpty(flatAppointmentDocumentId)) {
            return flatAppointmentDocumentService
                .fetchDocument(flatAppointmentDocumentId)
                .getDocument()
                .getFlatAppointmentData();
        }
        final FlatAppointmentData flatAppointmentData = new FlatAppointmentData();
        flatAppointmentData.setCipId(cipId);
        flatAppointmentData.setCancelReason(cancelReason);
        flatAppointmentData.setAppointmentDateTime(appointmentDateTime);
        return flatAppointmentData;
    }

    private ContractAppointmentData retrieveContractAppointmentDocument(
        final String contactAppointmentDocumentId,
        final String cipId,
        final String cancelReason,
        final String addressTo,
        final LocalDateTime appointmentDateTime
    ) {
        if (StringUtils.isNotEmpty(contactAppointmentDocumentId)) {
            return contractAppointmentDocumentService
                .fetchDocument(contactAppointmentDocumentId)
                .getDocument()
                .getContractAppointmentData();
        }
        final ContractAppointmentData contractAppointmentData = new ContractAppointmentData();
        contractAppointmentData.setCipId(cipId);
        contractAppointmentData.setCancelReason(cancelReason);
        contractAppointmentData.setAddressTo(addressTo);
        contractAppointmentData.setAppointmentDateTime(appointmentDateTime);
        return contractAppointmentData;
    }

    private NotaryApplicationType retrieveNotaryApplication(
        String notaryApplicationDocumentId,
        String eno,
        String addressFrom,
        String addressesTo,
        LocalDateTime appointmentDateTime
    ) {
        final NotaryApplicationType notaryApplication =
            retrieveNotaryApplication(notaryApplicationDocumentId, eno, appointmentDateTime);

        final ru.croc.ugd.ssr.notary.Apartment apartmentFrom = new ru.croc.ugd.ssr.notary.Apartment();
        apartmentFrom.setAddress(addressFrom);
        notaryApplication.setApartmentFrom(apartmentFrom);

        if (addressesTo != null) {
            final Apartments apartments = new Apartments();
            apartments.getApartment().addAll(
                Arrays.stream(addressesTo.split(","))
                    .map(addressTo -> {
                        final ru.croc.ugd.ssr.notary.Apartment apartmentTo = new ru.croc.ugd.ssr.notary.Apartment();
                        apartmentTo.setAddress(addressTo);
                        return apartmentTo;
                    })
                    .collect(Collectors.toList())
            );
        }

        return notaryApplication;
    }

    private NotaryApplicationType retrieveNotaryApplication(
        final String notaryApplicationDocumentId,
        final String eno,
        final LocalDateTime appointmentDateTime
    ) {
        if (StringUtils.isNotEmpty(notaryApplicationDocumentId)) {
            return notaryApplicationDocumentService
                .fetchDocument(notaryApplicationDocumentId)
                .getDocument()
                .getNotaryApplicationData();
        }

        final NotaryApplicationType notaryApplication = new NotaryApplicationType();

        notaryApplication.setEno(eno);
        notaryApplication.setAppointmentDateTime(appointmentDateTime);
        return notaryApplication;
    }

    private NotaryType retrieveNotary(final String notaryDocumentId) {
        return ofNullable(notaryDocumentId)
            .filter(StringUtils::isNotEmpty)
            .map(notaryDocumentService::fetchDocument)
            .map(NotaryDocument::getDocument)
            .map(Notary::getNotaryData)
            .orElse(null);
    }

    private NotaryType retrieveNotary(String notaryDocumentId, String notaryFullName, String notaryPhones) {
        return ofNullable(retrieveNotary(notaryDocumentId))
            .orElseGet(() -> {
                final NotaryType notary = new NotaryType();
                notary.setFullName(notaryFullName);
                if (notaryPhones != null) {
                    final NotaryPhone notaryPhone = new NotaryPhone();
                    notaryPhone.getPhone().addAll(Arrays.asList(notaryPhones.split(",")));
                    notary.setPhones(notaryPhone);
                }

                return notary;
            });
    }

    private void populateOfferLetter(PersonDocument testPerson, String cipId) {
        testPerson.getDocument().getPersonData().setOfferLetters(new PersonType.OfferLetters());
        PersonType.OfferLetters.OfferLetter offerLetter = new PersonType.OfferLetters.OfferLetter();
        offerLetter.setIdCIP(cipId);
        testPerson.getDocument().getPersonData().getOfferLetters().getOfferLetter().add(offerLetter);
    }

    private PersonType.OfferLetters.OfferLetter createOfferLetter(String cipId) {
        PersonType.OfferLetters.OfferLetter offerLetter = new PersonType.OfferLetters.OfferLetter();
        offerLetter.setIdCIP(cipId);
        offerLetter.setLetterId(UUID.randomUUID().toString());
        offerLetter.setDate(LocalDate.now());
        return offerLetter;
    }

    private PersonDocument retrievePerson(String ssoId, String personDocumentId) {
        if (!StringUtils.isEmpty(personDocumentId)) {
            final PersonDocument personDocument = personDocumentService.fetchDocument(personDocumentId);
            final PersonType person = personDocument.getDocument().getPersonData();
            if (StringUtils.isNotEmpty(ssoId)) {
                person.setSsoID(ssoId);
            }

            return personDocument;
        }
        final PersonType personType = new PersonType();
        personType.setFIO("Тестова Валентина Ивановна");
        personType.setUNOM(BigInteger.valueOf(29825));
        personType.setSsoID(ssoId);

        final Person person = new Person();
        person.setPersonData(personType);

        final PersonDocument personDocument = new PersonDocument();
        personDocument.setDocument(person);

        return personDocument;
    }
}
