package org.example.courzelo.models.institution;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "modules")
public class Module {
    @Id
    private String id;
    private String name;
    private String description;
    private String duration;
    private int credit;
    private String institutionID;
    private String program;
}

