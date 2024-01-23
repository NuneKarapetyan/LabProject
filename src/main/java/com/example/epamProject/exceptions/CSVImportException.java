package com.example.epamProject.exceptions;

public class CSVImportException extends RuntimeException{
    public CSVImportException(String errorMessage){
        super(errorMessage);
    }
}