package com.wedding.dreamwedding.service;

import com.wedding.dreamwedding.dto.LoginRequest;
import com.wedding.dreamwedding.dto.LoginResponse;
import com.wedding.dreamwedding.dto.RegisterRequest;
import com.wedding.dreamwedding.dto.RegisterResponse;
import com.wedding.dreamwedding.entity.*;
import com.wedding.dreamwedding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomerRepository customerRepository;
    private final VendorRepository vendorRepository;
    private final AdminRepository adminRepository;
    private final HotelRepository hotelRepository;
    private final UserActionRepository userActionRepository;
    private final PasswordEncoder passwordEncoder;

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
        BaseUser user = findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (isBcryptHash(user.getPassword())) {
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Invalid email or password");
            }
        } else {
            if (!user.getPassword().equals(request.getPassword())) {
                throw new BadCredentialsException("Invalid email or password");
            }
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.setPassword(encodedPassword);
            saveUser(user);
        }

        boolean profileCompleted = false;
        if (user instanceof Vendor) {
            profileCompleted = ((Vendor) user).isProfileCompleted();
        } else if (user instanceof Hotel) {
            profileCompleted = ((Hotel) user).isProfileCompleted();
        }

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

    private Optional<? extends BaseUser> findById(String id) {
        Optional<? extends BaseUser> user = customerRepository.findById(id);
        if (user.isPresent()) return user;
        user = vendorRepository.findById(id);
        if (user.isPresent()) return user;
        user = adminRepository.findById(id);
        if (user.isPresent()) return user;
        return hotelRepository.findById(id);
    }

    private void saveUser(BaseUser user) {
        if (user instanceof Customer) {
            customerRepository.save((Customer) user);
        } else if (user instanceof Vendor) {
            vendorRepository.save((Vendor) user);
        } else if (user instanceof Admin) {
            adminRepository.save((Admin) user);
        } else if (user instanceof Hotel) {
            hotelRepository.save((Hotel) user);
        }
    }

    public void deleteProfile(String id, String password) {
        BaseUser user = findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        if (password == null || password.isBlank()) {
            throw new BadCredentialsException("Password is required");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        
        switch (user.getRole()) {
            case CUSTOMER:
                customerRepository.deleteById(id);
                break;
            case VENDOR:
                vendorRepository.deleteById(id);
                break;
            case ADMIN:
                adminRepository.deleteById(id);
                break;
            case HOTEL:
                hotelRepository.deleteById(id);
                break;
        }
    }

    public void updatePassword(String userId, String currentPassword, String newPassword) {
        BaseUser user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new BadCredentialsException("Current password is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new BadCredentialsException("New password cannot be empty");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        saveUser(user);
    }

    public java.util.List<java.util.Map<String, String>> getDebugInfo() {
        return adminRepository.findAll().stream().map(a -> java.util.Map.of(
            "email", a.getEmail(),
            "role", a.getRole().toString()
        )).toList();
    }

    private boolean isBcryptHash(String password) {
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$") || password.startsWith("$2x$");
    }

    private void populateBaseFields(BaseUser user, RegisterRequest request, String plainPassword) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(plainPassword));
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());
        if (request.getProfilePicture() != null && !request.getProfilePicture().isEmpty()) {
            user.setProfilePicture(request.getProfilePicture());
        }
    }
}
