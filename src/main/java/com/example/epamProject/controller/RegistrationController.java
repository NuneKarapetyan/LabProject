package com.example.epamProject.controller;

import com.example.epamProject.dto.RegistrationDto;
import com.example.epamProject.service.RegistrationService;
import com.example.epamProject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final UserService userService;
    private final RegistrationService registrationService;

    public RegistrationController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }


    //@PostMapping(consumes = "application/json", produces = "application/json", path = "/register")
    @CrossOrigin(origins = "http://localhost:63342")
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationDto regDto) {
        logger.info("Received registration request for user with email: {}", regDto.getEmail());
        ResponseEntity<String> registrationResult = registrationService.registerUser(regDto);
        logger.info("Registration result: {}", registrationResult.getBody());

        return registrationResult;

    }

    @GetMapping("/failed")
    public String failed() {
        return "failed";  // Assumes "failed.html" in templates directory
    }


    @GetMapping("/success")
    public String success() {
        return "success";
    }


    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token") String confirmationToken) {
        logger.info("Received confirmation token: {}", confirmationToken);

        String result = registrationService.confirmEmail(confirmationToken);
        logger.info("Confirmation result: {}", result);

        if (result.equals("success")) {
            return "redirect:/success";
        } else {
            return "redirect:/failed";
        }

    }

}
