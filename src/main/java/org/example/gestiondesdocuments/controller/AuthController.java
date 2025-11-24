package org.example.gestiondesdocuments.controller;

import jakarta.validation.Valid;
import org.example.gestiondesdocuments.dto.ErrorResponse;
import org.example.gestiondesdocuments.dto.Auth.LoginRequest;
import org.example.gestiondesdocuments.dto.Auth.LoginResponse;
import org.example.gestiondesdocuments.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login endpoint - Note: This is now primarily handled by LoginAuthenticationFilter
     * This controller method serves as documentation and fallback if filter is bypassed
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.authenticate(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("INVALID_CREDENTIALS", "Email ou mot de passe incorrect"));
        }
    }

}
