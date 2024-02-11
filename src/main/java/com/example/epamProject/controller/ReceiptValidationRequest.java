package com.example.epamProject.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptValidationRequest {
    private String medicineName;
    private String username;
    private boolean valid;
}
