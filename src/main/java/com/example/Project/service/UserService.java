package com.example.Project.service;

import com.example.Project.controller.SessionModel;
import com.example.Project.controller.UserProfileResponse;
import com.example.Project.dto.UserDto;
import com.example.Project.entity.ConfirmationTokenEntity;
import com.example.Project.entity.UserEntity;
import com.example.Project.repo.ConfirmationTokenRepository;
import com.example.Project.repo.SessionRepository;
import com.example.Project.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@EnableTransactionManagement
public class UserService {


    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final ConfirmationTokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final SessionRepository sessionRepository;

    private final String DIRECTORY = "C:\\Users\\User\\Downloads\\Project\\Project\\src\\main\\resources\\userImages";


    public UserService(
        UserRepository userRepository,
        ModelMapper modelMapper, PasswordEncoder encoder,
        ConfirmationTokenRepository tokenRepository,
        SessionRepository sessionRepository
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.tokenRepository = tokenRepository;
        this.sessionRepository = sessionRepository;
    }

    public UserDto getByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        return convertToDto(userEntity);
    }

    private UserDto convertToDto(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    public UserProfileResponse getUserByUsername(String username) {
        UserEntity user = userRepository.findByEmail(username);
        var sessions = sessionRepository.findSessionEntitiesByEmail(username);
        var sessionModels = sessions.stream().map(s -> new SessionModel(s.getId(), s.getBrowserName())).toList();
        if (user != null) {
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setFirstName(user.getFirstName());
            userProfileResponse.setLastName(user.getLastName());
            userProfileResponse.setPhoneNumber(user.getPhoneNumber());
            userProfileResponse.setEmail(user.getEmail());
            userProfileResponse.setActiveSessions(sessionModels);
            return userProfileResponse;
        } else {
            return null;
        }
    }

    public ResponseEntity<String> uploadProfilePicture(String username, MultipartFile file) {
        try {
            UserEntity user = userRepository.findByEmail(username);
            if (user != null) {
                if (file != null && !file.isEmpty()) {
                    String fileName = file.getOriginalFilename();

                    Path filePath = Paths.get(DIRECTORY, fileName);
                    Files.write(filePath, file.getBytes());
                    user.setImage("http://localhost:8080/users/userImages/" + fileName);
                    userRepository.save(user);
                    return ResponseEntity.ok().body("Profile picture has set successfully");
                } else {
                    return ResponseEntity.badRequest().body("file is empty");
                }
            } else {
                return ResponseEntity.badRequest().body("Username doesn't exist: " + username);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("bad");

        }
    }

    public ResponseEntity<String> changePassword(String username, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findByEmail(username);
        if (user != null) {
            // Check if the old password matches the user's current password
            if (encoder.matches(oldPassword, user.getPassword())) {
                if(oldPassword.equals(newPassword)){
                    return ResponseEntity.badRequest().body("Old password matches with new password");
                }
                // Validate the new password (add your validation logic here)
                if (isValidPassword(newPassword)) {
                    // Set the new password and save the user entity
                    user.setPassword(encoder.encode(newPassword));
                    userRepository.save(user);
                    return ResponseEntity.ok().body("Password changed successfully");
                } else {
                    return ResponseEntity.badRequest().body("Invalid new password");
                }
            } else {
                return ResponseEntity.badRequest().body("Incorrect old password");
            }
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    private boolean isValidPassword(String newPassword) {
        // Password must be at least 8 characters long
        if (newPassword.length() < 8) {
            return false;
        }

        // Check for presence of uppercase letters
        if (!Pattern.compile("[A-Z]").matcher(newPassword).find()) {
            return false;
        }

        // Check for presence of lowercase letters
        if (!Pattern.compile("[a-z]").matcher(newPassword).find()) {
            return false;
        }

        // Check for presence of numbers
        if (!Pattern.compile("[0-9]").matcher(newPassword).find()) {
            return false;
        }

        // Check for presence of symbols
        return Pattern.compile("[^A-Za-z0-9]").matcher(newPassword).find();
    }

    @Transactional
    public void deleteAccount(String username) throws Exception {
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            throw new Exception("User not found");
        }
        ConfirmationTokenEntity confirmationToken = tokenRepository.findByUserEmail(username);
        // Delete the confirmation token if it exists
        if (confirmationToken != null) {
            tokenRepository.delete(confirmationToken);
        }
        // Delete the user
        userRepository.delete(user);

    }


    @Transactional
    public ResponseEntity<String> setPhoneNumber(String username, String phoneNumber) {
        UserEntity user = userRepository.findByEmail(username);
        if (user == null) {
            logger.error("User not found with username:" + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with username:" + username);
        }
        if (phoneNumber.isEmpty()) {
            logger.info("Phone number cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number cannot be empty");
        }

        phoneNumber = phoneNumber.substring(1, 13);

        if (!phoneNumber.startsWith("+374") || phoneNumber.length() != 12 || !phoneNumber.substring(1).matches("\\d+")) {
            logger.info("Phone number is not in the correct format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number is not in the correct format");
        }
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return ResponseEntity.ok().body("Phone Number has changed successfully");
    }
}
