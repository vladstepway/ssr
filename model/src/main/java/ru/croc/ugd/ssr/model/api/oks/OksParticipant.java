package ru.croc.ugd.ssr.model.api.oks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OksParticipant {
    private String id;
    private String role;
    private String organizationId;
    private boolean actual;
    private List<String> objectIds;
}
