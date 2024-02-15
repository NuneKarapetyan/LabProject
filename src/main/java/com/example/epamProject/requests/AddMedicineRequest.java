package com.example.epamProject.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMedicineRequest {
    private String medicineName;
    private boolean doctorReceipt;


}