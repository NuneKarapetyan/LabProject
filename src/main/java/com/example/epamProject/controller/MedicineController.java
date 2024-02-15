package com.example.epamProject.controller;


import com.example.epamProject.csv.ResponseMessage;
import com.example.epamProject.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping("/import-csv")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestBody MultipartFile file) {

        try {
            medicineService.save(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/csv/download/")
                    .path(file.getName())
                    .toUriString();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage("csv data uploaded", fileDownloadUri));
        } catch (Exception e) {
          String  message = e.getMessage() + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message, ""));
        }
    }


    @GetMapping
    @Operation(summary = "/medicines", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin("http://localhost:63342/")
    public ResponseEntity<?> getAllMedicines(
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "name,asc") String[] sort
                                             )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return medicineService.getAllMedicines(pageable,username);

    }

    @GetMapping("/{letter}")
    @Operation(summary = "/medicines/{letter}", security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin("http://localhost:63342/")
    public ResponseEntity<?> getMedicinesByLetter(@PathVariable char letter,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "30") int size
                                                 ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        // Call the service to fetch medicines based on the selected letter
        return medicineService.getMedicinesByLetter(pageable,String.valueOf(letter), username);
    }

    @GetMapping("name/{medicineName}")
    @Operation( security = @SecurityRequirement(name = "bearerAuth"))
    @CrossOrigin("http://localhost:63342/")
    public ResponseEntity<?> getMedicineDetailsByName(@PathVariable String medicineName) {


    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    return medicineService.getMedicineDetailsByName(medicineName, username);


    }

    @GetMapping("/search")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> searchMedicines(@RequestParam("query") String query,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size
                                             ) {
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return medicineService.searchMedicines(query,pageable ,username);

    }
}

