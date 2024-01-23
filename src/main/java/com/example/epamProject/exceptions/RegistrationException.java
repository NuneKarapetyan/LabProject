package com.example.epamProject.exceptions;

public class RegistrationException extends RuntimeException{
    public RegistrationException() {
        super();
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}