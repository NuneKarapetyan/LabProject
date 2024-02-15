package com.example.epamProject.controller;

import com.example.epamProject.dto.BasketDto;
import com.example.epamProject.requests.AddMedicineRequest;
import com.example.epamProject.service.BasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PostMapping("/add")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> addMedicineToBasket(@RequestBody AddMedicineRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        // Extract data from the request
        String medicineName = request.getMedicineName();
        boolean hasDoctorReceipt = request.isDoctorReceipt();
        System.out.println(request.isDoctorReceipt());
        // Call the service layer to add the medicine to the basket
        return basketService.addMedicineToBasket(username, medicineName, hasDoctorReceipt);

    }

    @GetMapping("/{medicineName}/download")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> downloadReceipt(@PathVariable String medicineName) {
        // Retrieve the receipt file path from the service
        return basketService.getReceiptFilePath(medicineName);

    }

    @PostMapping("/receipt/upload")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> uploadDoctorReceipt(
            @RequestParam("medicineName") String medicineName,
            @RequestBody MultipartFile doctorReceipt) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return basketService.uploadDoctorReceipt(username, medicineName, doctorReceipt);

    }

    @GetMapping("/view")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BasketDto> getUserBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        BasketDto basket = basketService.getUserBasket(username);
        if (basket != null) {
            return ResponseEntity.ok(basket);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/remove")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> removeMedicineFromBasket(
            @RequestParam("medicineName") String medicineName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return basketService.removeMedicineFromBasket(username, medicineName);

    }

    @PostMapping("/buy")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> buyMedicines() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean bought = basketService.buyMedicines(username);
        if (bought) {
            return ResponseEntity.ok("Medicines bought successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to buy medicines.");
        }
    }

}

