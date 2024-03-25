package com.example.epamProject.controller;

import jakarta.annotation.Nonnull;

public record SessionModel(

    @Nonnull int sessionid,
    @Nonnull String browser

)
{}
