package com.example.epamProject.controller;


import com.example.epamProject.csv.ResponseMessage;
import com.example.epamProject.dto.DoctorDTO;
import com.example.epamProject.service.DoctorService;
import com.example.epamProject.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final EmailService emailService;


    @Autowired
    public DoctorController(DoctorService doctorService,EmailService emailService) {
        this.doctorService = doctorService;
        this.emailService=emailService;
    }

    @PostMapping("/import-csv")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            doctorService.save(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/csv/download/doctors/")
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

    @CrossOrigin(origins = "http://localhost:63342")
    @GetMapping("/all")

    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long doctorId) {
        DoctorDTO doctor = doctorService.getDoctorById(doctorId);
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    // API to search doctors by name
    @GetMapping("/search")
    public ResponseEntity<List<DoctorDTO>> searchDoctors(@RequestParam("query") String query) {
        List<DoctorDTO> doctors = doctorService.searchDoctors(query);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmailToDoctor(
            @RequestParam("doctorId") Long doctorId,
                @RequestParam("subject") String subject,
            @RequestParam ("message")String message
            ) {

        String successMessage = "Email sent successfully.";
        String errorMessage = "Failed to send email.";

        try {
            emailService.sendEmailToDoctor(doctorId, subject, message);

            return ResponseEntity.status(HttpStatus.OK).body(successMessage);
        } catch (Exception e) {
            String detailedError = errorMessage + " Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

}
