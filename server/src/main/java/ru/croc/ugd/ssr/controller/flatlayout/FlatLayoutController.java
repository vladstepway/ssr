package ru.croc.ugd.ssr.controller.flatlayout;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.integration.service.flows.FlatLayoutFlowService;
import ru.croc.ugd.ssr.model.integration.dgi.SuperServiceDGPFlatLayoutType;

import java.util.List;

@RestController
@AllArgsConstructor
public class FlatLayoutController {

    private final FlatLayoutFlowService flatLayoutFlowService;

    /**
     * Отправка сведений о квартирографии.
     *
     * @param superServiceDgpFlatLayoutList список данных по квартирам
     * @return status
     */
    @ApiOperation(value = "Отправка сведений о квартирографии")
    @PostMapping("/send-flats-layout")
    public ResponseEntity<Void> sendFlatLayout(
        @RequestBody final List<SuperServiceDGPFlatLayoutType> superServiceDgpFlatLayoutList
    ) {
        flatLayoutFlowService.sendFlatsLayout(superServiceDgpFlatLayoutList);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
