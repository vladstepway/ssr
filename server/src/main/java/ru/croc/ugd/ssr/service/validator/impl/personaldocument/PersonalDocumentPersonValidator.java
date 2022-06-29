package ru.croc.ugd.ssr.service.validator.impl.personaldocument;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.croc.ugd.ssr.service.validator.ChainedValueValidator;
import ru.croc.ugd.ssr.service.validator.impl.person.value.HasResettlementHistoryWithoutSignedContract;
import ru.croc.ugd.ssr.service.validator.impl.person.value.IsAdultPerson;
import ru.croc.ugd.ssr.service.validator.model.ValidatablePersonData;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor
public class PersonalDocumentPersonValidator<T extends ValidatablePersonData> extends ChainedValueValidator<T> {

    public static final String NOT_ADULT_MESSAGE = "validate.personal.document.person.notAdult18";
    public static final String NO_RESETTLEMENT_NOTIFICATION_MESSAGE =
        "validate.personal.document.person.noResettlementNotification";

    private final MessageSource messageSource;

    @PostConstruct
    public void init() {
        this.registerValidatorInChain(new IsAdultPerson<>(NOT_ADULT_MESSAGE, 18L));
        this.registerValidatorInChain(
            new HasResettlementHistoryWithoutSignedContract<>(NO_RESETTLEMENT_NOTIFICATION_MESSAGE)
        );
    }

    @Override
    protected MessageSource getMessageSource() {
        return messageSource;
    }
}
