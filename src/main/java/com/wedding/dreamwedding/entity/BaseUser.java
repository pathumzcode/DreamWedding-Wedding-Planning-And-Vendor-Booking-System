package com.wedding.dreamwedding.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * OOP CONCEPT: ABSTRACTION
 * BaseUser is an abstract class. It cannot be instantiated on its own.
 * It serves as a blueprint for specialized user types (Polymorphism).
 */
@Getter
@Setter
public abstract class BaseUser {

    /** MongoDB auto-generated document ID */
    @Id
    private String id;

    /** 
     * OOP CONCEPT: ENCAPSULATION & VALIDATION
     * Validations ensure state integrity before persisting.
     */
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Indexed(unique = true)
    private String email;

    /**
     * JsonIgnore prevents this from being returned in API responses.
     */
    @JsonIgnore
    private String password;

    private Role role;
    private String profilePicture; // URL or Base64 image string

    @CreatedDate
    private LocalDateTime createdAt;

    // --- OOP CONCEPT: CONSTRUCTORS ---

    /** 1. DEFAULT CONSTRUCTOR (Initialization) */
    public BaseUser() {
        this.createdAt = LocalDateTime.now();
    }

    /** 2. PARAMETERIZED CONSTRUCTOR (Overloading) */
    public BaseUser(String firstName, String lastName, String email, Role role) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.setEmail(email); // Using setter to trigger encapsulation logic
        this.role = role;
    }

    /**
     * OOP CONCEPT: POLYMORPHISM
     * This method will be overridden by subclasses to provide specific implementations.
     */
    public abstract String getAccountSummary();

    // --- OOP CONCEPT: ENCAPSULATION (Accessor/Mutator Logic) ---

    public String getEmail() { return email; }

    /**
     * ENCAPSULATION LOGIC:
     * Validation and transformation happens inside the setter to protect data integrity.
     */
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email.toLowerCase().trim();
        } else {
            // In a real app, throw a custom exception here
            this.email = email; 
        }
    }
    
    /** ENCAPSULATION: Read-only access to fullName */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
