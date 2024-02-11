package com.example.epamProject.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMedicineRequest {
    private String username;
    private String medicineName;
    private boolean hasDoctorReceipt;


}