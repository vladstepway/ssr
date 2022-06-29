package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.flatdemo.RestCreateFlatDemoDto;
import ru.croc.ugd.ssr.dto.flatdemo.RestFlatDemoDto;
import ru.croc.ugd.ssr.service.FlatDemoService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/flat-demo")
public class FlatDemoController {

    private final FlatDemoService flatDemoService;

    @GetMapping
    public List<RestFlatDemoDto> getAll(
        @RequestParam("personDocumentId") final String personDocumentId
    ) {
        return flatDemoService.getAll(personDocumentId);
    }

    @PostMapping
    public RestFlatDemoDto create(@RequestBody final RestCreateFlatDemoDto createFlatDemoDto) {
        return flatDemoService.create(createFlatDemoDto);
    }
}
