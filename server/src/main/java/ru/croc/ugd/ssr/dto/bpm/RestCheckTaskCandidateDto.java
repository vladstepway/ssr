package ru.croc.ugd.ssr.dto.bpm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestCheckTaskCandidateDto {

    private final String taskId;
    @JsonProperty("isCandidate")
    private final boolean candidate;
}
