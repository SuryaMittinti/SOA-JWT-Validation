package com.example.jwtvalidation.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank(message = "token is required") String token) {}
