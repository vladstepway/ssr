package ru.croc.ugd.ssr.controller.guardianship;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.guardianship.CreateGuardianshipRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipHandleRequestDto;
import ru.croc.ugd.ssr.dto.guardianship.GuardianshipRequestDto;
import ru.croc.ugd.ssr.service.guardianship.RestGuardianshipService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/guardianship")
public class GuardianshipController {

    private final RestGuardianshipService restGuardianshipService;

    @GetMapping
    public List<GuardianshipRequestDto> find(
        @RequestParam final String affairId,
        @RequestParam(defaultValue = "true") final boolean skipInactive
    ) {
        return restGuardianshipService.find(affairId, skipInactive);
    }

    @GetMapping(value = "/{id}")
    public GuardianshipRequestDto findById(
        @PathVariable("id") final String id
    ) {
        return restGuardianshipService.findById(id);
    }

    @PostMapping
    public void create(@RequestBody final CreateGuardianshipRequestDto body) {
        restGuardianshipService.create(body);
    }

    @PostMapping(value = "/{id}/handle")
    public void handleRequest(
        @PathVariable("id") final String guardianshipRequestId,
        @RequestBody final GuardianshipHandleRequestDto body
    ) {
        restGuardianshipService.handleRequest(guardianshipRequestId, body);
    }
}
