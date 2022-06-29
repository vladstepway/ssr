package ru.croc.ugd.ssr.controller.egrn;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnFlatRequestDto;
import ru.croc.ugd.ssr.model.egrn.EgrnFlatRequestDocument;
import ru.croc.ugd.ssr.service.egrn.EgrnFlatRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/egrn-flat-requests")
public class EgrnFlatRequestController {

    private final EgrnFlatRequestService egrnFlatRequestService;

    @GetMapping("/last")
    public EgrnFlatRequestDocument fetchLast(
        @RequestParam final String unom,
        @RequestParam final String flatNumber,
        @RequestParam(defaultValue = "1004") final String statusCode
    ) {
        return egrnFlatRequestService.fetchLast(unom, flatNumber, statusCode);
    }

    @PostMapping
    public void create(
        @RequestBody final List<CreateEgrnFlatRequestDto> createEgrnFlatRequestDtoList
    ) {
        egrnFlatRequestService.create(createEgrnFlatRequestDtoList);
    }
}
