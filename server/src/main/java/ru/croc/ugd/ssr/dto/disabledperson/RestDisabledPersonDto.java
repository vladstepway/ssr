package ru.croc.ugd.ssr.dto.disabledperson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class RestDisabledPersonDto {
    private final String lastName;
    private final String firstName;
    private final String middleName;
    private final LocalDate birthDate;
    private final String addressFrom;
    private final String unom;
    private final String snils;
    private final String area;
    private final String district;
    private final String flatNumber;
    private final String personDocumentId;
    private final String disabledPersonDocumentId;
    @JsonProperty("isDeleted")
    private final boolean deleted;
    private final boolean usingWheelchair;
    private final String uniqueExcelRecordId;
}
