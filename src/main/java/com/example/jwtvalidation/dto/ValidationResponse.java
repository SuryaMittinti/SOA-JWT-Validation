package com.example.jwtvalidation.dto;

import java.util.Map;

public record ValidationResponse(
        boolean valid,
        String message,
        String subject,
        Map<String, Object> claims
) {}
