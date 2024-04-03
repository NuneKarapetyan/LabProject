package com.example.Project.controller;

import com.example.Project.entity.TokenBlackListEntity;
import com.example.Project.repo.SessionRepository;
import com.example.Project.repo.TokenBlackListRepository;
import com.example.Project.service.StorageService;
import com.example.Project.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController
{

    private final UserService userService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final StorageService storageService;

    private final SessionRepository sessionRepository;

    private final TokenBlackListRepository blackListRepository;

    @Autowired
    public UserController(
        UserService userService,
        StorageService storageService,
        SessionRepository sessionRepository,
        TokenBlackListRepository blackListRepository
    )
    {
        this.userService = userService;
        this.storageService = storageService;
        this.sessionRepository = sessionRepository;
        this.blackListRepository = blackListRepository;
    }


    @GetMapping("/getProfile")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> getUserProfileByUsername(@RequestHeader(name = "Authorization") String token)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(token);
        UserProfileResponse userProfileResponse = userService.getUserByUsername(username);
        if (userProfileResponse != null)
        {
            return ResponseEntity.ok(userProfileResponse);
        } else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username: " + username);
        }
    }

    @PatchMapping("/uploadPhoto")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> uploadProfilePicture(
        @RequestBody MultipartFile file
    )
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println(username);
        return userService.uploadProfilePicture(username, file);
    }

    @GetMapping("/userImages/{fileName:.+}")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName)
    {
        Resource file = storageService.loadAsResource(fileName);
        // Determine the media type based on the file extension
        MediaType mediaType = MediaType.IMAGE_JPEG; // Default to JPEG
        if (fileName.endsWith(".png"))
        {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileName.endsWith(".gif"))
        {
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
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try
        {
            return userService.changePassword(
                username,
                changePasswordRequest.oldPassword,
                changePasswordRequest.getNewPassword()
            );
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to change password: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAccount")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteAccount()
    {
        // Get the username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try
        {
            userService.deleteAccount(username);
            return ResponseEntity.ok("Account deleted successfully.");
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to delete account: " + e.getMessage());
        }
    }


    @PostMapping("/setPhoneNumber")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> setPhoneNumber(@RequestBody String phoneNumber)
    {
        // Get the username from the JWT token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.setPhoneNumber(username, phoneNumber);
    }

    @PutMapping("/delete-session")
    @CrossOrigin("http://localhost:63342/")
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<?> deleteSessionById(@RequestParam Integer sessionId)
    {

            var jwtToken = sessionRepository.getById(sessionId).getToken();
            System.out.println(jwtToken);
            this.sessionRepository.deleteById(Long.valueOf(sessionId));
            var tokenBlackListEntity = new TokenBlackListEntity();
            tokenBlackListEntity.setToken(jwtToken);
            this.blackListRepository.save(tokenBlackListEntity);
            return ResponseEntity.accepted().body("Session deleted");

    }

    @PostConstruct
    void deleteSessionsOnRuntime()
    {
        this.sessionRepository.deleteAll();
    }
}
