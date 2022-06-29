package ru.croc.ugd.ssr.dto.personupload;

import lombok.Value;

@Value
public class PersonUploadDto {

    private final String fileName;
    private final String fileId;
    private final String password;
}
