package com.example.Project.service;

import com.example.Project.config.JwtResponse;
import com.example.Project.config.JwtTokUtil;
import com.example.Project.entity.SessionEntity;
import com.example.Project.repo.SessionRepository;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtAuthenticationService {
    private static final int MAX_ATTEMPTS_ALLOWED = 5;
    private static final int BASE_BLOCK_DURATION_SECONDS = 60; // Base block duration in seconds
    private static final int BLOCK_DURATION_MULTIPLIER = 60; // Block duration multiplier

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokUtil jwtTokUtil;

    public ResponseEntity<?> authenticate(String username, String password, UserAgent userAgent) {
        final String token = jwtTokUtil.generateToken(username);

        SessionEntity sessionEntity = sessionRepository.findSessionEntitiesByEmailAndBrowserName(username, userAgent.getBrowser().getName());
        if (sessionEntity != null && sessionEntity.getAttempts() >= MAX_ATTEMPTS_ALLOWED) {
            long blockDuration = calculateBlockDuration(sessionEntity.getAttempts() - MAX_ATTEMPTS_ALLOWED);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many failed login attempts. Please try again after " + blockDuration + " seconds.");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (sessionEntity != null) {
                sessionEntity.setAttempts(0);
                sessionRepository.save(sessionEntity);
            }
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User has been disabled");
        } catch (BadCredentialsException e) {
            if (sessionEntity != null) {
                sessionEntity.setAttempts(sessionEntity.getAttempts() + 1);
                sessionRepository.save(sessionEntity);
            } else {
                sessionEntity = new SessionEntity();
                var sessionId = UUID.randomUUID();
                sessionEntity.setSessionId(sessionId.toString());
                sessionEntity.setBrowserName(userAgent.getBrowser().getName());
                sessionEntity.setToken(token);
                sessionEntity.setEmail(username);
                sessionEntity.setAttempts(1);
                sessionRepository.save(sessionEntity);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");
        }
        return ResponseEntity.ok(new JwtResponse(token));
    }

    private long calculateBlockDuration(int attemptsExceeded) {
        return BASE_BLOCK_DURATION_SECONDS * (attemptsExceeded + 1);
    }
}
