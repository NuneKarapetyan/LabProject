package com.example.Project.exceptions;

public class RoleNotFoundException extends  RuntimeException{
    public RoleNotFoundException(String errorMes) {
        super(errorMes);
    }
}
