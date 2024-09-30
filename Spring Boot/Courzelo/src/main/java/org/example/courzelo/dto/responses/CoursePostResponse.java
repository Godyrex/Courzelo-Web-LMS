package org.example.courzelo.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CoursePostResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime created;
    private List<String> files;
}
