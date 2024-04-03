package com.example.Project.controller;

import java.util.UUID;

import com.example.Project.config.JwtRequest;
import com.example.Project.config.JwtResponse;
import com.example.Project.config.JwtTokUtil;
import com.example.Project.config.JwtUserDetailService;
import com.example.Project.entity.SessionEntity;
import com.example.Project.repo.SessionRepository;
import com.example.Project.service.JwtAuthenticationService;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtAuthenticationController
{

    private final JwtTokUtil jwtTokUtil;

    private final JwtUserDetailService userDetailsService;

    private final JwtAuthenticationService authenticationService;

    private final SessionRepository sessionRepository;

    public JwtAuthenticationController(
        JwtTokUtil jwtTokUtil,
        JwtUserDetailService userDetailsService,
        JwtAuthenticationService authenticationService,
        SessionRepository sessionRepository
    )
    {
        this.jwtTokUtil = jwtTokUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping(value = "/authenticate")
    @CrossOrigin(origins = "http://localhost:63342")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest, HttpServletRequest request)
    {
        System.out.println(authenticationRequest.getUsername());
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));

          return  authenticationService.authenticate(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword(),
                userAgent
            );
           /* final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());
            SessionEntity sessionEntity = new SessionEntity();
            final String token = jwtTokUtil.generateToken(userDetails.getUsername());
            sessionEntity.setToken(token);
            sessionEntity.setEmail(authenticationRequest.getUsername());
            var sessionId = UUID.randomUUID();
            sessionEntity.setSessionId(sessionId.toString());
            sessionEntity.setBrowserName(userAgent.getBrowser().getName());
            this.sessionRepository.save(sessionEntity);*/

    }
}