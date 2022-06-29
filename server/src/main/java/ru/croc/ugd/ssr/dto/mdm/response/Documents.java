package ru.croc.ugd.ssr.dto.mdm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Documents {
    @JsonProperty("doc_passport_rf")
    private final List<DocPassportRf> passports;
    @JsonProperty("doc_snils")
    private final List<DocSnils> snils;
}
