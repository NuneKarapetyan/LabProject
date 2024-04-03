package com.example.Project.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {

    @NotBlank
    private String firstName;
    @Nullable
    private int age;
    @NotBlank
    private String lastName;
    @Nullable
    private String phoneNumber;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    private String image;
    private RoleDto role;
}

