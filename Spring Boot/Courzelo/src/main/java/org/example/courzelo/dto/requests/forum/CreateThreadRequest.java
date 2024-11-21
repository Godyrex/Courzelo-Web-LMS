package org.example.courzelo.dto.requests.forum;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CreateThreadRequest {
    private String name;
    private String description;
    private String institutionID;

}