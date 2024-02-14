package com.example.epamProject.controller;

public class UploadResponse {
    private final String fileName;


    public UploadResponse(String fileName) {
        this.fileName = fileName;

    }


    public String getFileName() {
        return fileName;
    }


}
