package com.wedding.dreamwedding.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OOP CONCEPT: COMPOSITION
 * This class encapsulates contact details into a single object.
 * Vendors and Hotels will "have-a" ContactInfo instead of duplicating fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {
    
    @Email(message = "Invalid email format in contact info")
    private String email;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "Phone number must start with +94 followed by 9 digits (e.g., +94771234567)")
    private String phone;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "WhatsApp number must start with +94 followed by 9 digits")
    private String whatsapp;
    
    private String website;
}
