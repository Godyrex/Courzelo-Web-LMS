package org.example.courzelo.dto.requests;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
}
