package org.example.courzelo.exceptions;

public class CourseAlreadyCreatedException extends RuntimeException {
    public CourseAlreadyCreatedException(String message) {
        super(message);
    }
}
