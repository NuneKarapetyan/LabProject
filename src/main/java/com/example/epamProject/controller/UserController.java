package com.example.epamProject.controller;


import com.example.epamProject.csv.ResponseMessage;
import com.example.epamProject.dto.AddressDto;
import com.example.epamProject.service.StorageService;
import com.example.epamProject.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final StorageService storageService;

    @Autowired
    public UserController(UserService userService, StorageService storageService) {
        this.userService = userService;
        this.storageService = storageService;
    }


    @PostMapping("/import-csv")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestBody MultipartFile file) {

        try {
            userService.save(file);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/csv/download/")
                    .path(file.getName())
                    .toUriString();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseMessage("csv data has uploaded", fileDownloadUri));
        } catch (Exception e) {
            String message = e.getMessage() + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message, ""));
        }
    }

    @GetMapping("/getProfile")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<?> getUserProfileByUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserProfileResponse userProfileResponse = userService.getUserByUsername(username);
        if (userProfileResponse != null) {
            return ResponseEntity.ok(userProfileResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }
    }

    @PatchMapping("/uploadPhoto")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<?> uploadProfilePicture(
            @RequestBody MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        return userService.uploadProfilePicture(username, file);

    }

    @GetMapping("/userImages/{fileName:.+}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Resource file = storageService.loadAsResource(fileName);
        // Determine the media type based on the file extension
        MediaType mediaType = MediaType.IMAGE_JPEG; // Default to JPEG
        if (fileName.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        }

        // Set the Content-Type header to the appropriate media type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }

    @PostMapping("/changePassword")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            return userService.changePassword(username, changePasswordRequest.oldPassword, changePasswordRequest.getNewPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to change password: " + e.getMessage());
        }
    }

   /* @PatchMapping("/changeEmail")
    @CrossOrigin("http://localhost:63342/")
    public ResponseEntity<?> changeEmailAddress(@RequestBody String changeEmailRequest) {
        // Get the username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.changeEmailAddress(username, changeEmailRequest);
    }*/

    @DeleteMapping("/deleteAccount")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<?> deleteAccount() {
        // Get the username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            userService.deleteAccount(username);
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete account: " + e.getMessage());
        }
    }

    @PatchMapping("/setAddress")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))

    public ResponseEntity<?> setAddress(@RequestBody AddressDto addressDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
       return userService.setAddress(username, addressDTO.getCountry(), addressDTO.getCity(),
                addressDTO.getStreet(), addressDTO.getBuilding(), addressDTO.getPostalCode());

    }

        @PostMapping("/setPhoneNumber")
        @CrossOrigin("http://localhost:63342/")
        @Operation(security = @SecurityRequirement(name = "bearerAuth"))

        public ResponseEntity<?> setPhoneNumber(@RequestBody String phoneNumber) {
            // Get the username from the JWT token
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            return userService.setPhoneNumber(username, phoneNumber);

        }

}
