package com.example.epamProject.controller;

import java.util.List;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse
{

    private String photo;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String address;

    private String email;

    private List<SessionModel> activeSessions;
}

