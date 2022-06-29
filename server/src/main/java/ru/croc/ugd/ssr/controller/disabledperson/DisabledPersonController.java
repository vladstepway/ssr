package ru.croc.ugd.ssr.controller.disabledperson;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDetailsDto;
import ru.croc.ugd.ssr.dto.disabledperson.RestDisabledPersonDto;
import ru.croc.ugd.ssr.service.disabledperson.DisabledPersonService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/disabled-persons")
public class DisabledPersonController {

    private final DisabledPersonService disabledPersonService;

    @GetMapping
    public RestDisabledPersonDetailsDto fetchDisabledPersonDetailsByUnomAndFlatNumber(
        @RequestParam("unom") final String unom,
        @RequestParam("flatNumber") final String flatNumber,
        @RequestParam("personDocumentId") final String personDocumentId
    ) {
        return disabledPersonService.fetchDisabledPersonDetailsByUnomAndFlatNumber(
            unom, flatNumber, personDocumentId
        );
    }

    @PostMapping
    public void saveDisabledPersons(
        @RequestBody final List<RestDisabledPersonDto> disabledPersonDtos,
        @RequestParam(required = false) final String disabledPersonImportDocumentId
    ) {
        disabledPersonService.saveDisabledPersons(disabledPersonDtos, disabledPersonImportDocumentId);
    }
}
