package com.example.epamProject.controller;

import com.example.epamProject.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/basket")
public class BasketController {
    @Autowired
    private BasketService basketService;

    @PostMapping("/add")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> addMedicineToBasket(@RequestBody AddMedicineRequest request) {
        // Extract data from the request
        String username = request.getUsername();
        String medicineName = request.getMedicineName();
        boolean hasDoctorReceipt = request.isHasDoctorReceipt();

        // Call the service layer to add the medicine to the basket
        return basketService.addMedicineToBasket(username, medicineName, hasDoctorReceipt);

    }

    @GetMapping("/{medicineName}/download")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Resource> downloadReceipt(@PathVariable String medicineName) {
        // Retrieve the receipt file path from the service
        String filePath = basketService.getReceiptFilePath(medicineName);

        if (filePath == null || filePath.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            // Load the file as a resource
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Set up HTTP headers for the response
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF); // Set content type as binary data
                headers.setContentDispositionFormData("attachment", resource.getFilename()); // Set the file name

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/receipt/upload")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> uploadDoctorReceipt(
            @RequestParam("username") String username,
            @RequestParam("medicineName") String medicineName,
            @RequestParam("doctorReceipt") MultipartFile doctorReceipt) {

        return basketService.uploadDoctorReceipt(username, medicineName, doctorReceipt);
    }
}

//upload file tvecinq file dexi anuny u usernamey db um pahum enq heto erb knopken aktivanum a add enq anum baketum