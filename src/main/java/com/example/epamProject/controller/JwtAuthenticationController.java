package com.example.epamProject.controller;

import com.example.epamProject.config.JwtRequest;
import com.example.epamProject.config.JwtResponse;
import com.example.epamProject.config.JwtTokUtil;
import com.example.epamProject.config.JwtUserDetailService;
import com.example.epamProject.service.JwtAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class JwtAuthenticationController {


    private final JwtTokUtil jwtTokUtil;
    private final JwtUserDetailService userDetailsService;
    private final JwtAuthenticationService authenticationService;

    public JwtAuthenticationController(JwtTokUtil jwtTokUtil,
                                       JwtUserDetailService userDetailsService, JwtAuthenticationService authenticationService) {
        this.jwtTokUtil = jwtTokUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }


    @PostMapping(value = "/authenticate")
    @CrossOrigin(origins = "http://localhost:63342")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest)  {
        try {
            authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getUsername());

            final String token = jwtTokUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return  ResponseEntity.badRequest().body("Invalid login or password");
        }


    }


}