package org.example.courzelo.exceptions;

public class ModuleAlreadyExistsException extends RuntimeException {
    public ModuleAlreadyExistsException(String message) {
        super(message);
    }
}
