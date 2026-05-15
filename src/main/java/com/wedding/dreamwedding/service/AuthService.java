package com.wedding.dreamwedding.service;

import com.wedding.dreamwedding.dto.LoginRequest;
import com.wedding.dreamwedding.dto.LoginResponse;
import com.wedding.dreamwedding.dto.RegisterRequest;
import com.wedding.dreamwedding.dto.RegisterResponse;
import com.wedding.dreamwedding.entity.*;
import com.wedding.dreamwedding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * AuthService handles user registration and login logic.
 * Routes users to the correct collection based on their role.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final AdminRepository adminRepository;
    private final HotelRepository hotelRepository;
    private final UserActionRepository userActionRepository;

    public RegisterResponse register(RegisterRequest request) {
        if (emailExists(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        BaseUser savedUser;
        String plainPassword = request.getPassword();

        switch (request.getRole()) {
            case CUSTOMER:
                Customer customer = new Customer();
                populateBaseFields(customer, request, plainPassword);
                savedUser = customerRepository.save(customer);
                break;
            case VENDOR:
                Vendor vendor = new Vendor();
                populateBaseFields(vendor, request, plainPassword);
                savedUser = vendorRepository.save(vendor);
                break;
            case ADMIN:
                Admin admin = new Admin();
                populateBaseFields(admin, request, plainPassword);
                savedUser = adminRepository.save(admin);
                break;
            case HOTEL:
                Hotel hotel = new Hotel();
                populateBaseFields(hotel, request, plainPassword);
                savedUser = hotelRepository.save(hotel);
                break;
            default:
                throw new IllegalArgumentException("Invalid role provided");
        }

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getRole(),
                "Registration successful!"
        );
    }

    public LoginResponse login(LoginRequest request) {
        BaseUser user = findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        boolean profileCompleted = false;
        if (user instanceof Vendor) {
            profileCompleted = ((Vendor) user).isProfileCompleted();
        } else if (user instanceof Hotel) {
            profileCompleted = ((Hotel) user).isProfileCompleted();
        }

        // Log the login activity
        UserAction loginAction = new UserAction();
        loginAction.setUserId(user.getId());
        loginAction.setUserEmail(user.getEmail());
        loginAction.setAction("LOGIN");
        loginAction.setTimestamp(LocalDateTime.now());
        loginAction.setRole(user.getRole().toString());
        userActionRepository.save(loginAction);

        return new LoginResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                profileCompleted,
                user.getProfilePicture(),
                "Login successful!"
        );
    }

    public void logout(String userId, String email, String role) {
        UserAction logoutAction = new UserAction();
        logoutAction.setUserId(userId);
        logoutAction.setUserEmail(email);
        logoutAction.setAction("LOGOUT");
        logoutAction.setTimestamp(LocalDateTime.now());
        logoutAction.setRole(role);
        userActionRepository.save(logoutAction);
    }

    private boolean emailExists(String email) {
        return findByEmail(email).isPresent();
    }

    private Optional<? extends BaseUser> findByEmail(String email) {
        Optional<? extends BaseUser> user = customerRepository.findByEmail(email);
        if (user.isPresent()) return user;
        user = vendorRepository.findByEmail(email);
        if (user.isPresent()) return user;
        user = adminRepository.findByEmail(email);
        if (user.isPresent()) return user;
        return hotelRepository.findByEmail(email);
    }

    private void populateBaseFields(BaseUser user, RegisterRequest request, String plainPassword) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(plainPassword);
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
    }
}
