package ru.croc.ugd.ssr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ugd.ssr.service.SystemAddressesService;

import java.math.BigInteger;

/**
 * SystemAddressesController.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys-address")
public class SystemAddressesController {

    private final SystemAddressesService systemAddressesService;

    @GetMapping(value = "/unom-by-address")
    public BigInteger getSysUnomByAddress(
        @RequestParam(required = true) final String address
    ) {
        return systemAddressesService.getSysUnomByAddress(address);
    }
}
