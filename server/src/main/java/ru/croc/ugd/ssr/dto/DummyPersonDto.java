package ru.croc.ugd.ssr.dto;

import lombok.Builder;
import lombok.Value;
import ru.croc.ugd.ssr.model.PersonDocument;

@Value
@Builder
public class DummyPersonDto {

    private final String personId;
    private final PersonDocument dummyPerson;
}
