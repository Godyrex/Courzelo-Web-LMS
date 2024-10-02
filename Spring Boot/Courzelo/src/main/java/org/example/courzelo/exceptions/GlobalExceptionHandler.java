package org.example.courzelo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(InstitutionNotFoundException.class)
    public ResponseEntity<String> handleInstitutionNotFoundException(InstitutionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<String> handleCourseNotFoundException(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(InvitationNotFoundException.class)
    public ResponseEntity<String> handleInvitationNotFoundException(InvitationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<String> handleGroupNotFoundException(GroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(InstitutionHasNoCalendarException.class)
    public ResponseEntity<String> handleInstitutionHasNoCalendarException(InstitutionHasNoCalendarException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UserNotPartOfInstitutionException.class)
    public ResponseEntity<String> handleUserNotPartOfInstitutionException(UserNotPartOfInstitutionException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UserAlreadyHasRoleException.class)
    public ResponseEntity<String> handleUserAlreadyHasRoleException(UserAlreadyHasRoleException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(CalendarGenerationException.class)
    public ResponseEntity<String> handleCalendarGenerationException(CalendarGenerationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
    @ExceptionHandler(UserAlreadyPartOfInstitutionException.class)
    public ResponseEntity<String> handleUserAlreadyPartOfInstitutionException(UserAlreadyPartOfInstitutionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
