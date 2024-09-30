package org.example.courzelo.services;

import org.example.courzelo.dto.responses.PaginatedInvitationsResponse;
import org.example.courzelo.models.Role;
import org.example.courzelo.models.institution.InvitationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public interface IInvitationService {
    void createInvitation(String institutionID, String email, Role role, String code, LocalDateTime expiryDate);
    void updateInvitationStatus(String email,String institutionID, InvitationStatus status);
    void updateExpiredInvitations();
    ResponseEntity<HttpStatus> resendInvitation(String invitationID);
    ResponseEntity<HttpStatus> deleteInvitation(String invitationID);
    ResponseEntity<HttpStatus>  deleteExpiredInvitations();
    ResponseEntity<PaginatedInvitationsResponse> getInvitations(int page, int sizePerPage, String keyword, String institutionID);

}
