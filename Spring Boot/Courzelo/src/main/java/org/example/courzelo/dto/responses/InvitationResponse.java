package org.example.courzelo.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InvitationResponse {
    private String id;
    private String email;
    private String code;
    private String status;
    private String role;
    private LocalDateTime expiryDate;
}
