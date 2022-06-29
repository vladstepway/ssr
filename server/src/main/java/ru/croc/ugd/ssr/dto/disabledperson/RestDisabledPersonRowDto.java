package ru.croc.ugd.ssr.dto.disabledperson;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestDisabledPersonRowDto {
    private final String personDocumentId;
    private final String lastName;
    private final String firstName;
    private final String middleName;
    private final LocalDate birthDate;
    private final String fullAddressFrom;
    private final String addressFrom;
    private final String flatNumber;
    private final String phone;
    private final boolean usingWheelchair;
    private final String unom;
    private final String area;
    private final String district;
    private final String uniqueExcelRecordId;
    private final String disabledPersonDocumentId;
}
