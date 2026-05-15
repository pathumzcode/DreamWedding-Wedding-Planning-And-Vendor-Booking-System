package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.LoginRequest;
import com.wedding.dreamwedding.dto.LoginResponse;
import com.wedding.dreamwedding.dto.RegisterRequest;
import com.wedding.dreamwedding.dto.RegisterResponse;
import com.wedding.dreamwedding.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user registration.
     * @param request Validated registration details
     * @return ResponseEntity with created user details
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint for user login.
     * @param request Email + password
     * @return ResponseEntity with authenticated user details
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody java.util.Map<String, String> userInfo) {
        authService.logout(
            userInfo.get("id"),
            userInfo.get("email"),
            userInfo.get("role")
        );
        return ResponseEntity.ok().build();
    }
}
