package com.example.epamProject.exceptions;

public class RoleNotFoundException extends  RuntimeException{
    public RoleNotFoundException(String errorMes) {
        super(errorMes);
    }
}
