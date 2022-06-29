package ru.croc.ugd.ssr.service.validator.impl.person;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;
import ru.croc.ugd.ssr.PersonType;
import ru.croc.ugd.ssr.dto.shipping.ShippingFlowStatus;
import ru.croc.ugd.ssr.exception.PersonNotValidForShippingException;
import ru.croc.ugd.ssr.service.PersonDocumentService;
import ru.croc.ugd.ssr.service.document.ShippingApplicationDocumentService;
import ru.croc.ugd.ssr.service.validator.AbstractExceptionBasedValidatorUnit;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;
import ru.croc.ugd.ssr.shipping.ShippingApplicationType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Уже помогли перевезти вещи из этой квартиры.
 * Один из правообладателей квартиры уже подал заявку на помощь в переезде. Оформлять еще одну не нужно.
 * @param <T> тип валидируемого объекта
 */
@Slf4j
public class ShippingAlreadyRequestedAndServiceProvided<T extends ValidatablePersonData>
    extends AbstractExceptionBasedValidatorUnit<T> {

    /**
     * наименование проперти с сообщением.
     */
    public static final String SHIPPING_SERVICE_REQUESTED = "validate.person.shippingServiceRequested";
    public static final String SHIPPING_SERVICE_REQUESTED_CODE = "already_applied_by_another_owner";
    /**
     * наименование проперти с сообщением.
     */
    public static final String SERVICE_PROVIDED_MESSAGE = "validate.person.shippingServiceProvided";
    public static final String SERVICE_PROVIDED_MESSAGE_CODE = "service_already_provided";

    private final PersonDocumentService personDocumentService;
    private final ShippingApplicationDocumentService shippingApplicationDocumentService;

    private final boolean checkServiceProvided;

    /**
     * Конструктор.
     * @param messageSource Сервис локализации сообщений
     * @param personDocumentService Сервис работы с документами персон
     * @param shippingApplicationDocumentService Сервис работы с заявками на помощь в переезде
     */
    public ShippingAlreadyRequestedAndServiceProvided(
        MessageSource messageSource,
        PersonDocumentService personDocumentService,
        ShippingApplicationDocumentService shippingApplicationDocumentService,
        boolean checkServiceProvided
    ) {
        super(messageSource);
        this.personDocumentService = personDocumentService;
        this.shippingApplicationDocumentService = shippingApplicationDocumentService;
        this.checkServiceProvided = checkServiceProvided;
    }

    @Override
    public void validate(final T personType) {
        log.info(
            "Shipping check: start ShippingAlreadyRequestedAndServiceProvided.validate, person {}",
            personType.getPersonId()
        );
        if (personType.getPerson().getFlatID() == null) {
            throw getValidationException();
        }
        final Map<String, PersonType> allFlatLivers
            = personDocumentService.getAllSameFlatLivers(personType.getPerson());
        log.info(
            "Shipping check: ShippingAlreadyRequestedAndServiceProvided, getAllSameFlatLivers, person {}",
            personType.getPersonId()
        );
        if (StringUtils.hasText(personType.getPersonId())) {
            allFlatLivers.put(personType.getPersonId(), personType.getPerson());
        }
        validateShipping(allFlatLivers);
        log.info(
            "Shipping check: finish ShippingAlreadyRequestedAndServiceProvided.validate, person {}",
            personType.getPersonId()
        );
    }

    private void validateShipping(final Map<String, PersonType> allPersonsToCheck) {
        allPersonsToCheck
            .keySet()
            .stream()
            .map(this::retrieveValidationExceptionIfExists)
            .filter(Objects::nonNull)
            .findAny()
            .ifPresent(e -> {
                throw e;
            });

    }

    private RuntimeException retrieveValidationExceptionIfExists(final String personId) {
        final List<ShippingApplicationType> shippingApplicationOpt = shippingApplicationDocumentService
            .findShippingApplicationByPersonUid(personId);
        log.info(
            "Shipping check: ShippingAlreadyRequestedAndServiceProvided, findShippingApplicationByPersonUid, person {}",
            personId
        );
        if (isShippingRequested(shippingApplicationOpt)) {
            return getShippingAlreadyRequestedException();
        }
        if (checkServiceProvided && isShippingDone(shippingApplicationOpt)) {
            return getShippingServiceProvidedException();
        }
        return null;
    }

    private boolean isShippingRequested(final List<ShippingApplicationType> shippingApplicationOpt) {
        return shippingApplicationOpt.stream()
            .anyMatch(this::isShippingRequested);
    }

    private boolean isShippingRequested(final ShippingApplicationType shippingApplicationType) {
        return ShippingFlowStatus.RECORD_ADDED.getDescription().equals(shippingApplicationType.getStatus());
    }

    private boolean isShippingDone(final List<ShippingApplicationType> shippingApplicationOpt) {
        return shippingApplicationOpt.stream()
            .anyMatch(this::isShippingDone);
    }

    private boolean isShippingDone(final ShippingApplicationType shippingApplicationType) {
        return ShippingFlowStatus.SHIPPING_COMPLETE.getDescription().equals(shippingApplicationType.getStatus());
    }

    public RuntimeException getValidationException() {
        return getShippingAlreadyRequestedException();
    }

    private RuntimeException getShippingAlreadyRequestedException() {
        return new PersonNotValidForShippingException(getMessage(SHIPPING_SERVICE_REQUESTED),
            SHIPPING_SERVICE_REQUESTED_CODE);
    }

    private RuntimeException getShippingServiceProvidedException() {
        return new PersonNotValidForShippingException(getMessage(SERVICE_PROVIDED_MESSAGE),
            SERVICE_PROVIDED_MESSAGE_CODE);
    }

}
