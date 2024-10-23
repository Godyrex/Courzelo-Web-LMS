package org.example.courzelo.dto.requests.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


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
    private Boolean isFinished;
    private List<String> skills;
    private int credit;
    private String program;
    private Map<String,Long> moduleParts;

}
