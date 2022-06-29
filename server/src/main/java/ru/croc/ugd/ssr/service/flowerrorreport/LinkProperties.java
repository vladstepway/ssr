package ru.croc.ugd.ssr.service.flowerrorreport;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkProperties {
    private String domain;
    private String personId;
}
