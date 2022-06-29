package ru.croc.ugd.ssr.controller.egrn;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.egrn.CreateEgrnBuildingRequestDto;
import ru.croc.ugd.ssr.service.egrn.EgrnBuildingRequestService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/egrn-building-requests")
public class EgrnBuildingRequestController {

    private final EgrnBuildingRequestService egrnBuildingRequestService;

    @PostMapping
    public void create(
        @RequestBody final List<CreateEgrnBuildingRequestDto> createEgrnBuildingRequestDtoList
    ) {
        egrnBuildingRequestService.create(createEgrnBuildingRequestDtoList);
    }
}
