package com.example.epamProject.controller;


import com.example.epamProject.csv.ResponseMessage;
import com.example.epamProject.dto.MedicineDTO;
import com.example.epamProject.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping("/import-csv")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            medicineService.save(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/csv/download/")
                    .path(file.getName())
                    .toUriString();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage(message, fileDownloadUri));
        } catch (Exception e) {
            message = e.getMessage() + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message, ""));
        }
    }


    @GetMapping
    @Operation(summary = "/medicines", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<MedicineDTO>> getAllMedicines() {
        List<MedicineDTO> medicines = medicineService.getAllMedicines();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }
    @GetMapping("/{letter}")
    @Operation(summary = "/medicines/{letter}", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin("http://localhost:63342/")
    public List<String> getMedicinesByLetter(@PathVariable char letter) {
        // Call the service to fetch medicines based on the selected letter
        return medicineService.getMedicinesByLetter(String.valueOf(letter));
    }
    @GetMapping("name/{medicineName}")
    @Operation(summary = "/medicines/{medicineName}", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MedicineDTO> getMedicineDetailsByName(@PathVariable String medicineName) {
        MedicineDTO medicine = medicineService.getMedicineDetailsByName(medicineName);
        if (medicine != null) {
            return ResponseEntity.ok(medicine);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<MedicineDTO>> searchMedicines(@RequestParam("query") String query) {
        List<MedicineDTO> matchingMedicines = medicineService.searchMedicines(query);
        return new ResponseEntity<>(matchingMedicines, HttpStatus.OK);
    }
}

