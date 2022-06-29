package ru.croc.ugd.ssr.service.bus.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum BusRequestType {

    EGRN_BUILDING(1, "getEgrnExtractRealty", "UGD_V6"),
    EGRN_FLAT(2, "getEgrnExtractRealty", "UGD_V6"),
    PFR_SNILS_EXTENDED(3, "getSNILSExtended", "UGD_V6");

    private final int code;
    private final String documentType;
    private final String orgProfile;

    public static Optional<BusRequestType> ofCode(final int code) {
        return Arrays.stream(values())
            .filter(source -> source.getCode() == code)
            .findFirst();
    }
}
