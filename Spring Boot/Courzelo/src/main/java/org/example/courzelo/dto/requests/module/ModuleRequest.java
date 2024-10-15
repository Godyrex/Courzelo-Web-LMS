package org.example.courzelo.dto.requests.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleRequest {
    private String name;
    private String description;
    private String duration;
    private String semester;
    private Double scoreToPass;
    private List<String> skills;
    private int credit;
    private String program;

}
