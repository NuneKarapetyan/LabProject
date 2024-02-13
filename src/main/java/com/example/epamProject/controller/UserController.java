package com.example.epamProject.controller;


import com.example.epamProject.csv.ResponseMessage;
import com.example.epamProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/import-csv")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            userService.save(file);
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

    @GetMapping("/getProfile")
    @CrossOrigin("http://localhost:63342/")
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
    public ResponseEntity<?> uploadProfilePicture(
            @RequestBody MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
            try {
                userService.uploadProfilePicture(username, file);
                return ResponseEntity.ok("Profile picture uploaded successfully.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to upload profile picture: " + e.getMessage());
            }
    }

    @PostMapping("/changePassword")
    @CrossOrigin("http://localhost:63342/")
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

    @PatchMapping("/changeEmail")
    @CrossOrigin("http://localhost:63342/")
    public ResponseEntity<?> changeEmailAddress(@RequestBody String changeEmailRequest) {
        // Get the username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.changeEmailAddress(username, changeEmailRequest);
    }
    @DeleteMapping("/deleteAccount")
    @CrossOrigin("http://localhost:63342/")
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

}
