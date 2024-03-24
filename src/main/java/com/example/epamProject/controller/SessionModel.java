package com.example.epamProject.controller;

import jakarta.annotation.Nonnull;

public record SessionModel(
    @Nonnull String id,
    @Nonnull String browser
)
{}
