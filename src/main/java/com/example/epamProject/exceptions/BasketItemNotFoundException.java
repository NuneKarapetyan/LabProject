package com.example.epamProject.exceptions;

public class BasketItemNotFoundException extends RuntimeException{
    public BasketItemNotFoundException() {
        super();
    }

    public BasketItemNotFoundException(String message) {
        super(message);
    }

    public BasketItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}