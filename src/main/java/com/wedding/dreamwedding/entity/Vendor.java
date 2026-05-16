package com.wedding.dreamwedding.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * OOP CONCEPT: INHERITANCE
 * Vendor extends BaseUser, inheriting common user properties.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Document(collection = "vendors")
public class Vendor extends BaseUser implements BookingTarget { // 1. INHERITANCE & POLYMORPHISM (Interface)

    @NotBlank(message = "Business name is required")
    private String businessName;
    private String category;
    private String aboutUs;
    private String location;
    
    /**
     * OOP CONCEPT: COMPOSITION & VALIDATION
     * Vendor "has-a" ContactInfo object rather than just flat fields.
     * @Valid ensures nested objects are also validated.
     */
    @Valid
    private ContactInfo businessContact;
    
    private String instagramLink;
    private String facebookLink;
    
    /** 
     * OOP CONCEPT: AGGREGATION
     * A Vendor "has-a" list of packages. These can be managed independently.
     */
    private java.util.List<VendorPackage> packages;
    
    private java.util.List<String> galleryPhotos;
    private boolean profileCompleted;

    private int reviewCount = 0;
    private double averageRating = 0.0;

    // --- OOP CONCEPT: CONSTRUCTORS ---

    public Vendor() {
        super();
        setRole(Role.VENDOR);
        this.businessContact = new ContactInfo();
    }

    public Vendor(String firstName, String lastName, String email, String businessName) {
        super(firstName, lastName, email, Role.VENDOR);
        this.businessName = businessName;
        this.businessContact = new ContactInfo();
    }

    // --- OOP CONCEPT: METHOD OVERRIDING (Implementation of Interface) ---

    @Override
    public String getDisplayName() {
        return (businessName != null && !businessName.isEmpty()) ? businessName : getFirstName() + " " + getLastName();
    }

    @Override
    public String getDisplayCategory() {
        return category != null ? category : "Vendor";
    }

    @Override
    public String getDisplayLocation() {
        return location != null ? location : "Online/Flexible";
    }

    /**
     * OOP CONCEPT: POLYMORPHISM
     * Overriding parent method to provide specialized behavior.
     */
    @Override
    public String getAccountSummary() {
        return "Vendor Profile: " + getDisplayName() + " [" + getDisplayCategory() + "]";
    }
}
