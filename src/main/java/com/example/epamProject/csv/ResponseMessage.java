package com.example.epamProject.csv;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResponseMessage {
    private String message;
    private String fileDownloadUri;

    public ResponseMessage(String message) {
        this.message = message;
    }
}