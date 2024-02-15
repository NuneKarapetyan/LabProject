package com.example.epamProject.service;

import com.example.epamProject.dto.RegistrationDto;
import com.example.epamProject.entity.RoleEntity;
import com.example.epamProject.repo.ConfirmationTokenRepository;
import com.example.epamProject.repo.RoleRepository;
import com.example.epamProject.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    public void testRegisterUser_Success() {
        // Mocking
        RegistrationDto registrationDto = new RegistrationDto("John", "Doe", "john@example.com",
                "Password12.", "Password12.");

        RoleEntity roleEntity = new RoleEntity("ROLE_USER");

        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(roleRepository.findByName(Mockito.anyString())).thenReturn(java.util.Optional.of(roleEntity));
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        // Testing
        ResponseEntity<String> result = registrationService.registerUser(registrationDto);

        // Assertion
        assertEquals("Verify email by the link sent on your email address", result.getBody());
    }

    @Test
    public void testRegisterUser_EmailInUse() {
        // Mocking
        RegistrationDto registrationDto = new RegistrationDto("John", "Doe", "john@example.com",
                "password", "password");
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        // Testing
        ResponseEntity<String> result = registrationService.registerUser(registrationDto);

        // Assertion
        assertEquals("Email is already in use", result.getBody());
    }

    @Test
    public void testRegisterUser_WeakPassword() {
        // Mocking
        RegistrationDto registrationDto = new RegistrationDto("John", "Doe", "john@example.com",
                "password12.", "password12.");
        Mockito.when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        // Testing
        ResponseEntity<String> result = registrationService.registerUser(registrationDto);

        // Assertion
        assertEquals("Password must contain at least one uppercase letter.", result.getBody());
    }

    // Add more test methods for other scenarios...


    @Test
    public void testConfirmEmail_Failed() {
        // Mocking
        Mockito.when(confirmationTokenRepository.findByConfirmationToken(Mockito.anyString()))
                .thenReturn(null);

        // Testing
        String result = registrationService.confirmEmail("invalidToken");

        // Assertion
        assertEquals("failed", result);
    }
}
