package com.example.jwtvalidation.controller;

import com.example.jwtvalidation.dto.TokenRequest;
import com.example.jwtvalidation.dto.ValidationResponse;
import com.example.jwtvalidation.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class JwtController {
    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validate(@Valid @RequestBody TokenRequest request) {
        try {
            Map<String, Object> claims = jwtService.validateAndExtractClaims(request.token());
            Object subject = claims.get("sub");
            return ResponseEntity.ok(new ValidationResponse(
                    true, "Token is valid.", subject == null ? null : subject.toString(), claims));
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidationResponse(false, ex.getMessage(), null, Map.of()));
        }
    }

    // Demo helper to create a signed token for Swagger testing. Protect/remove in production.
    @PostMapping("/demo-token")
    public Map<String, Object> demoToken(@RequestParam(defaultValue = "ramsai") String subject) {
        return jwtService.createDemoToken(subject);
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedResource(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Provide Authorization: Bearer <token>."));
        }
        try {
            Map<String, Object> claims = jwtService.validateAndExtractClaims(authorization);
            return ResponseEntity.ok(Map.of(
                    "message", "You accessed the protected resource.",
                    "subject", String.valueOf(claims.get("sub")),
                    "claims", claims));
        } catch (JwtException | IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid token: " + ex.getMessage()));
        }
    }
}
