package ru.croc.ugd.ssr.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.dto.SignContractDto;
import ru.croc.ugd.ssr.service.ContractService;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/contracts")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/{contractOrderId}/sign")
    public void signContract(
        @PathVariable(value = "contractOrderId") String contractOrderId,
        @RequestBody final SignContractDto signContractDto
    ) {
        contractService.signContract(contractOrderId, signContractDto);
    }

}
