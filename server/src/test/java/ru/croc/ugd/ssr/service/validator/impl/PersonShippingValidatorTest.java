package ru.croc.ugd.ssr.service.validator.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.RECORD_ADDED;
import static ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus.SHIPPING_COMPLETE;
import static ru.croc.ugd.ssr.service.validator.impl.PersonShippingValidator.NOT_RELOCATING;
import static ru.croc.ugd.ssr.service.validator.impl.person.IsAdultPerson16.NOT_ADULT_MESSAGE;
import static ru.croc.ugd.ssr.service.validator.impl.person.IsOwnerOrTenantOrLiver.NOT_OWNER_MESSAGE;
import static ru.croc.ugd.ssr.service.validator.impl.person.RequestLessThenBeforeThreeDaysOfShipping.TOO_LATE_FOR_SHIPPING;
import static ru.croc.ugd.ssr.service.validator.impl.person.ShippingAlreadyRequestedAndServiceProvided.SERVICE_PROVIDED_MESSAGE;
import static ru.croc.ugd.ssr.service.validator.impl.person.ShippingAlreadyRequestedAndServiceProvided.SHIPPING_SERVICE_REQUESTED;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.document.PersonTypeHelper;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.model.PersonDocument;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.document.TradeAdditionDocumentService;
import ru.croc.ugd.ssr.service.validator.impl.person.IsAdultPerson16;
import ru.croc.ugd.ssr.service.validator.impl.person.IsOwnerOrTenantOrLiver;
import ru.croc.ugd.ssr.service.validator.impl.person.RequestLessThenBeforeThreeDaysOfShipping;
import ru.croc.ugd.ssr.service.validator.impl.person.ShippingAlreadyRequestedAndServiceProvided;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;
import ru.croc.ugd.ssr.trade.TradeAdditionType;
import ru.croc.ugd.ssr.utils.PersonDocumentUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


public class PersonShippingValidatorTest {

    @InjectMocks
    private PersonShippingValidator<ValidatablePersonData> personShippingValidator;

    @Mock
    private MessageSource messageSource;
    @Mock
    private PersonDocumentService personDocumentService;
    @Mock
    private TradeAdditionDocumentService tradeAdditionDocumentService;
    @Mock
    private PersonDocumentUtils personDocumentUtils;
    @Mock
    private ShippingApplicationDocumentService shippingApplicationDocumentService;

    private final PersonTypeHelper personTypeHelper = new PersonTypeHelper();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNoPerson() {
        assertThatExceptionOfType(PersonNotValidForShippingException.class).isThrownBy(
            () -> personShippingValidator.validate(null));
        verify(messageSource).getMessage(NOT_RELOCATING, null, Locale.getDefault());
    }

    @Test
    public void testIsAdultPersonNotValid() {

        assertThatExceptionOfType(PersonNotValidForShippingException.class).isThrownBy(
            () -> new IsAdultPerson16<>(messageSource)
                .validate(new ValidatablePersonData(null, personTypeHelper.getPersonTypeUnder18())));
        verify(messageSource).getMessage(NOT_ADULT_MESSAGE, null, Locale.getDefault());
    }

    @Test
    public void testIsAdultPersonValid() {
        new IsAdultPerson16<>(messageSource).validate(new ValidatablePersonData(null, personTypeHelper.getPersonTypeAdult()));
        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsRequestLessThenBeforeThreeDaysOfShippingNotValidDueToDate() {
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/withAgreementAndContract.json");
        when(personDocumentService
            .getContractSignDateAndNumber(inputData))
            .thenReturn(Optional.of(Pair.of(inputData.getContracts().getContract().get(0).getContractNum(),
                inputData.getContracts().getContract().get(0).getContractSignDate())));
        when(personDocumentService
            .getPersonMovementDate(inputData.getContracts().getContract().get(0).getContractSignDate()))
            .thenReturn(LocalDate.now().plusDays(2));

        when(messageSource.getMessage(any(), any(), any())).thenReturn("message");

        assertThatExceptionOfType(PersonNotValidForShippingException.class)
            .isThrownBy(
                () -> new RequestLessThenBeforeThreeDaysOfShipping<>(messageSource, personDocumentService, tradeAdditionDocumentService)
                    .validate(new ValidatablePersonData(null, inputData))
            );
        verify(messageSource).getMessage(TOO_LATE_FOR_SHIPPING, null, Locale.getDefault());
    }

    @Test
    public void testIsRequestLessThenBeforeThreeDaysOfShippingValid() {
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/withAgreementAndContract.json");

        when(personDocumentService
            .getContractSignDateAndNumber(inputData))
            .thenReturn(Optional.of(Pair.of(inputData.getContracts().getContract().get(0).getContractNum(),
                inputData.getContracts().getContract().get(0).getContractSignDate())));

        when(personDocumentService
            .getPersonMovementDate(inputData.getContracts().getContract().get(0).getContractSignDate()))
            .thenReturn(LocalDate.now().plusDays(5));

        new RequestLessThenBeforeThreeDaysOfShipping<>(messageSource, personDocumentService, tradeAdditionDocumentService)
            .validate(new ValidatablePersonData(null, inputData));

        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsRequestLessThenBeforeThreeDaysOfShippingTradeNotValidDueToDate() {
        final TradeAdditionType tradeAdditionType = new TradeAdditionType();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/withAgreementAndContract.json");
        when(tradeAdditionDocumentService
            .getMoveDateFromTradeAddition(tradeAdditionType))
            .thenReturn(LocalDate.now().plusDays(2));

        when(messageSource.getMessage(any(), any(), any())).thenReturn("message");

        assertThatExceptionOfType(PersonNotValidForShippingException.class)
            .isThrownBy(
                () -> new RequestLessThenBeforeThreeDaysOfShipping<>(messageSource, personDocumentService, tradeAdditionDocumentService)
                    .validate(ValidatablePersonData.builder()
                        .tradeAddition(tradeAdditionType)
                        .person(inputData)
                        .build())
            );
        verify(messageSource).getMessage(TOO_LATE_FOR_SHIPPING, null, Locale.getDefault());
    }

    @Test
    public void testIsRequestLessThenBeforeThreeDaysOfShippingTradeValid() {
        final TradeAdditionType tradeAdditionType = new TradeAdditionType();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/withAgreementAndContract.json");
        when(tradeAdditionDocumentService
            .getMoveDateFromTradeAddition(tradeAdditionType))
            .thenReturn(LocalDate.now().plusDays(5));

        new RequestLessThenBeforeThreeDaysOfShipping<>(messageSource, personDocumentService, tradeAdditionDocumentService)
            .validate(ValidatablePersonData.builder()
                .tradeAddition(tradeAdditionType)
                .person(inputData)
                .build());

        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsOwnerOrTenantOrLiverNotValid() {
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/notAnOwnerOfFlat.json");


        assertThatExceptionOfType(PersonNotValidForShippingException.class).isThrownBy(
            () -> new IsOwnerOrTenantOrLiver<>(messageSource).validate(new ValidatablePersonData(null, inputData)));
        verify(messageSource).getMessage(NOT_OWNER_MESSAGE, null, Locale.getDefault());
    }

    @Test
    public void testIsOwnerOrTenantOrLiverValid() {
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/ownerOfFlat.json");

        new IsOwnerOrTenantOrLiver<>(messageSource).validate(new ValidatablePersonData(null, inputData));

        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsServiceProvidedValid() {
        final String personId = UUID.randomUUID().toString();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/ownerOfFlat.json");
        final PersonDocument personDocument = personTypeHelper.wrapToDocument(inputData);
        final List<PersonDocument> listOfDocs = Arrays.asList(personDocument);

        when(personDocumentService.fetchByFlatId(inputData.getFlatID())).thenReturn(listOfDocs);
        when(shippingApplicationDocumentService.findShippingApplicationByPersonUid(personId))
            .thenReturn(Collections.emptyList());
        when(personDocumentUtils.getPersonsDataFromDocuments(listOfDocs)).thenReturn(Arrays.asList(inputData));

        new ShippingAlreadyRequestedAndServiceProvided<>(
            messageSource, personDocumentService, shippingApplicationDocumentService, true
        )
            .validate(new ValidatablePersonData(personId, inputData));

        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsServiceProvidedNotValid() {
        final String personId = UUID.randomUUID().toString();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/ownerOfFlat.json");
        final PersonDocument personDocument = personTypeHelper.wrapToDocument(inputData);
        final List<PersonDocument> listOfDocs = Arrays.asList(personDocument);
        final ShippingApplicationType completedShippingApplication = new ShippingApplicationType();
        completedShippingApplication.setStatus(SHIPPING_COMPLETE.getDescription());

        when(personDocumentService.fetchByFlatId(inputData.getFlatID())).thenReturn(listOfDocs);
        when(shippingApplicationDocumentService.findShippingApplicationByPersonUid(personId))
            .thenReturn(Collections.singletonList(completedShippingApplication));
        when(personDocumentUtils.getPersonsDataFromDocuments(listOfDocs)).thenReturn(Arrays.asList(inputData));

        when(personDocumentService.getAllSameFlatLivers(Mockito.any())).thenReturn(new HashMap<String, PersonType>() {{
            put("personId1", inputData);
        }});

        assertThatExceptionOfType(PersonNotValidForShippingException.class)
            .isThrownBy(
                () -> new ShippingAlreadyRequestedAndServiceProvided<>(
                    messageSource, personDocumentService, shippingApplicationDocumentService, true
                )
                    .validate(new ValidatablePersonData(personId, inputData)));

        verify(messageSource).getMessage(SERVICE_PROVIDED_MESSAGE, null, Locale.getDefault());
    }

    @Test
    public void testIsShippingAlreadyRequestedValid() {
        final String personId = UUID.randomUUID().toString();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/ownerOfFlat.json");
        final PersonDocument personDocument = personTypeHelper.wrapToDocument(inputData);
        final List<PersonDocument> listOfDocs = Arrays.asList(personDocument);

        when(personDocumentService.fetchByFlatId(inputData.getFlatID())).thenReturn(listOfDocs);
        when(shippingApplicationDocumentService.findShippingApplicationByPersonUid(personId))
            .thenReturn(Collections.emptyList());
        when(personDocumentUtils.getPersonsDataFromDocuments(listOfDocs)).thenReturn(Arrays.asList(inputData));

        new ShippingAlreadyRequestedAndServiceProvided<>(
            messageSource, personDocumentService, shippingApplicationDocumentService, false
        )
            .validate(new ValidatablePersonData(personId, inputData));

        verifyNoMoreInteractions(messageSource);
    }

    @Test
    public void testIsShippingAlreadyRequestedNotValid() {
        final String personId = UUID.randomUUID().toString();
        final PersonType inputData = personTypeHelper.readPersonTypeFromFile("person/ownerOfFlat.json");
        final PersonDocument personDocument = personTypeHelper.wrapToDocument(inputData);
        final List<PersonDocument> listOfDocs = Arrays.asList(personDocument);
        final ShippingApplicationType completedShippingApplication = new ShippingApplicationType();
        completedShippingApplication.setStatus(RECORD_ADDED.getDescription());

        when(personDocumentService.fetchByFlatId(inputData.getFlatID())).thenReturn(listOfDocs);
        when(shippingApplicationDocumentService.findShippingApplicationByPersonUid(personId))
            .thenReturn(Collections.singletonList(completedShippingApplication));
        when(personDocumentUtils.getPersonsDataFromDocuments(listOfDocs)).thenReturn(Arrays.asList(inputData));

        when(personDocumentService.getAllSameFlatLivers(Mockito.any())).thenReturn(new HashMap<String, PersonType>() {{
            put("personId1", inputData);
        }});

        assertThatExceptionOfType(PersonNotValidForShippingException.class)
            .isThrownBy(
                () -> new ShippingAlreadyRequestedAndServiceProvided<>(
                    messageSource, personDocumentService, shippingApplicationDocumentService, false
                )
                    .validate(new ValidatablePersonData(personId, inputData)));

        verify(messageSource).getMessage(SHIPPING_SERVICE_REQUESTED, null, Locale.getDefault());
    }
}
