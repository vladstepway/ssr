package ru.croc.ugd.ssr.model.api.oks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String inn;
    private String fullName;
    private String roleNumber;
    private String roleName;
}
