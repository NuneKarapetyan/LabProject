package com.example.epamProject.service;

import com.example.epamProject.controller.UserProfileResponse;
import com.example.epamProject.csv.Parser;
import com.example.epamProject.dto.UserDto;
import com.example.epamProject.entity.AddressEntity;
import com.example.epamProject.entity.ConfirmationTokenEntity;
import com.example.epamProject.entity.UserEntity;
import com.example.epamProject.exceptions.CSVImportException;
import com.example.epamProject.repo.BasketRepository;
import com.example.epamProject.repo.ConfirmationTokenRepository;
import com.example.epamProject.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private final Parser parser;
    private final ModelMapper modelMapper;
    private final PasswordEncoder encoder;
    private final RegistrationService registrationService;
    private final ConfirmationTokenRepository tokenRepository;
    private final BasketRepository basketRepository;

    private final String DIRECTORY = "C:\\Users\\User\\Downloads\\epamProject\\epamProject\\src\\main\\resources\\userImages";

    @Autowired
    public UserService(
            UserRepository userRepository,
            Parser parser,
            ModelMapper modelMapper, PasswordEncoder encoder, RegistrationService registrationService, ConfirmationTokenRepository tokenRepository, BasketRepository basketRepository) {
        this.userRepository = userRepository;
        this.parser = parser;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
        this.registrationService = registrationService;
        this.tokenRepository = tokenRepository;
        this.basketRepository = basketRepository;
    }

    public void save(MultipartFile file) {
        try {
            System.out.printf(">>>>>>>>>>>>>Starting the CSV import %s%n", new Date());
            List<UserEntity> users = parser.csvToUserEntity(file.getInputStream());
            // Remove users with email addresses that already exist in the database
            List<String> existingUserEmails = userRepository.getAllUserEmails();
            users.removeIf(user -> existingUserEmails.contains(user.getEmail()));

            // Extract unique addresses from the users and create a map of address IDs to AddressEntity objects
            Map<Integer, AddressEntity> addresses = users.stream()
                    .map(UserEntity::getAddress)
                    .distinct()
                    .collect(Collectors.toMap(AddressEntity::getId, x -> x, (a1, a2) -> a1));

            System.out.println(addresses.size());

            // Replace the address objects in the users with the corresponding address objects from the map
            for (UserEntity user : users) {
                AddressEntity userAddress = user.getAddress();
                user.setAddress(addresses.get(userAddress.getId()));
            }

            userRepository.saveAll(users);
            System.out.printf(">>>>>>>>>>>>>Ending the CSV import %s%n", new Date());
        } catch (IOException e) {
            throw new CSVImportException("Failed to store CSV data: " + e.getMessage());
        }
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
        if (user != null) {
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setPhoto(user.getImage());
            userProfileResponse.setFirstName(user.getFirstName());
            userProfileResponse.setLastName(user.getLastName());
            userProfileResponse.setPhoneNumber(user.getPhoneNumber());
            userProfileResponse.setAddress(String.valueOf(user.getAddress())); // Assuming getAddressAsString() returns a formatted address string
            userProfileResponse.setEmail(user.getEmail());
            return userProfileResponse;
        } else {
            // Handle case when user is not found
            return null;
        }
    }

    public ResponseEntity<String> uploadProfilePicture(String username, MultipartFile file) throws IOException {
        try {
            UserEntity user = userRepository.findByEmail(username);
            if (user != null) {
                if (!file.isEmpty() && file!=null) {
                    // Check if the directory exists, if not, create it
                    File directory = new File(DIRECTORY);

                    // Get the file name and save the uploaded file to the directory
                    String fileName = file.getOriginalFilename();
                    Path filePath = Paths.get(DIRECTORY, fileName);

                    Files.write(filePath, file.getBytes());

                    // Update the user's profile picture path in the database
                    String imagePath = DIRECTORY + File.separator + fileName;
                    user.setImage(imagePath);
                    userRepository.save(user);
                    return ResponseEntity.ok().body("Profile picture has set successfully");
                } else {
                    return ResponseEntity.badRequest().body("file is empty");
                }
            } else {
                return ResponseEntity.badRequest().body("Username doesn't exist: " + username);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("bad");

        }
    }

    public ResponseEntity<String> changePassword(String username, String oldPassword, String newPassword) {
        UserEntity user = userRepository.findByEmail(username);
        if (user != null) {
            // Check if the old password matches the user's current password
            if (encoder.matches(oldPassword, user.getPassword())) {
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
        if (!Pattern.compile("[^A-Za-z0-9]").matcher(newPassword).find()) {
            return false;
        }

        return true;
    }

    public ResponseEntity<String> changeEmailAddress(String username, String newEmailAddress) {
        // Retrieve the user from the database
        UserEntity user = userRepository.findByEmail(username);

        if (user == null) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with username: " + username);
        }

        // Update the email address
        user.setEmail(newEmailAddress);

        // Save the updated user
        userRepository.save(user);
        return ResponseEntity.ok("Email address changed successfully.");
    }

    @Transactional
    public void deleteAccount(String username) throws Exception {
        // Retrieve the user from the database

            UserEntity user = userRepository.findByEmail(username);

            if (user == null) {
                throw new Exception("User not found");
            }
            ConfirmationTokenEntity confirmationToken = tokenRepository.findByUserEmail(username);
            basketRepository.deleteAllByUserEmail(username);
            // Delete the confirmation token if it exists
            if (confirmationToken != null) {
                tokenRepository.delete(confirmationToken);
            }
            // Delete the user

            userRepository.delete(user);

    }
}
