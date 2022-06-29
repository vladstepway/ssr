package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PersonDetail {
    @JsonProperty("first_name")
    private final String firstName;
    @JsonProperty("middle_name")
    private final String middleName;
    @JsonProperty("last_name")
    private final String lastName;
    private final String gender;
    @JsonProperty("birth_date")
    private final String birthDate;
    @JsonProperty("rip_date")
    private final String ripDate;
    private final Ids ids;
    private final Documents documents;
    private final Contacts contacts;
    private final Addresses addresses;
}
