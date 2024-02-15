package com.example.epamProject.controller;

import com.example.epamProject.service.AdminReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/receipts")
public class AdminReceiptController {

    private final AdminReceiptService adminReceiptService;


    public AdminReceiptController(AdminReceiptService adminReceiptService) {
        this.adminReceiptService = adminReceiptService;
    }


    @PutMapping("/validate")
    public ResponseEntity<String> validateReceipt(@RequestBody ReceiptValidationRequest request) {
        String medicineName = request.getMedicineName();
        String username = request.getUsername();
        boolean isValid = request.isValid();
        // Call the service layer to validate the receipt
        return  adminReceiptService.validateReceipt(medicineName, username, isValid);

    }

}
