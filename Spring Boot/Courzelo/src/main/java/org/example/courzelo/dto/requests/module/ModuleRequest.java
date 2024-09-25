package org.example.courzelo.dto.requests.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleRequest {
    private String name;
    private String description;
    private int credit;
    private String program;

}
