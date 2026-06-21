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
    // CREATE Operation: Endpoint for user registration
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
    // CREATE Operation (Session): Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // CREATE Operation (Session): Endpoint for user logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody java.util.Map<String, String> userInfo) {
        authService.logout(
            userInfo.get("id"),
            userInfo.get("email"),
            userInfo.get("role")
        );
        return ResponseEntity.ok().build();
    }

    // DELETE Operation: Delete a user profile
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id, @RequestBody java.util.Map<String, String> request) {
        try {
            String password = request.get("password");
            authService.deleteProfile(id, password);
            return ResponseEntity.ok().build();
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // READ Operation: Debug endpoint to get admin info
    @GetMapping("/debug")
    public ResponseEntity<?> debugAdmins() {
        try {
            return ResponseEntity.ok(authService.getDebugInfo());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
