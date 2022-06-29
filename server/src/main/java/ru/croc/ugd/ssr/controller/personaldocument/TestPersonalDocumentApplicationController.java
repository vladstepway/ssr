package ru.croc.ugd.ssr.controller.personaldocument;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.mq.listener.personaldocument.PersonalDocumentApplicationRequestListener;

/**
 * Контроллер для работы с заявлениями на предоставление документов.
 */
@RestController
@AllArgsConstructor
@ConditionalOnProperty(name = "ugd.ssr.listener.personal-document-application.enabled")
public class TestPersonalDocumentApplicationController {

    private final PersonalDocumentApplicationRequestListener personalDocumentApplicationRequestListener;

    @ApiOperation(value = "Incoming personal document application event from MPGU")
    @PostMapping(value = "/mpgu/personal-document-application-event")
    public void mpguCoordinateMessage(@ApiParam(value = "Message") @RequestBody String message) {
        personalDocumentApplicationRequestListener.handleCoordinateMessage(
            new GenericMessage<>(message)
        );
    }

}
