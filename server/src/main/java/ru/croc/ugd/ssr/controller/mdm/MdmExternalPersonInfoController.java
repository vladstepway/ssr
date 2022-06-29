package ru.croc.ugd.ssr.controller.mdm;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.mdm.request.MdmRequest;
import ru.croc.ugd.ssr.dto.mdm.response.MdmResponse;
import ru.croc.ugd.ssr.service.mdm.MdmExternalPersonInfoService;

/**
 * Контроллер для работы с сервисом МДМ.
 */
@RestController
@AllArgsConstructor
public class MdmExternalPersonInfoController {

    private final MdmExternalPersonInfoService mdmExternalPersonInfoService;

    /**
     * Выполнение произвольного запроса в МДМ с записью ответа в БД.
     *
     * @param body Запрос.
     * @return Ответ.
     */
    @PostMapping(value = "/mdm-external-person-info/find")
    public MdmResponse request(@RequestBody MdmRequest body) {
        return mdmExternalPersonInfoService.request(body);
    }
}
