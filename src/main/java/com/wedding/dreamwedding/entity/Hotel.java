package com.wedding.dreamwedding.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * OOP CONCEPT: INHERITANCE
 * Hotel extends BaseUser, inheriting basic user attributes.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Document(collection = "hotels")
public class Hotel extends BaseUser implements BookingTarget { // INHERITANCE & POLYMORPHISM (Interface)

    @NotBlank(message = "Hotel name is required")
    private String hotelName;
    private String location;
    private String aboutUs;
    
    /**
     * OOP CONCEPT: COMPOSITION & VALIDATION
     * The Hotel "owns" its contact details.
     * @Valid ensures nested objects are also validated.
     */
    @Valid
    private ContactInfo contactDetails;
    
    private String instagramLink;
    private String facebookLink;
    private java.util.List<String> amenities;

    /**
     * OOP CONCEPT: COMPOSITION (Stronger form)
     * These rooms strictly belong to this hotel.
     * In a university context: "Lifetime of the part depends on the whole."
     */
    private java.util.List<HotelRoom> rooms;

    private java.util.List<HallArrangement> hallArrangements;
    
    /**
     * OOP CONCEPT: AGGREGATION
     * The gallery photos can exist outside the context of this specific hotel.
     */
    private java.util.List<String> galleryPhotos;

    private boolean profileCompleted;
    private int reviewCount = 0;
    private double averageRating = 0.0;

    // --- OOP CONCEPT: CONSTRUCTORS ---

    public Hotel() {
        super();
        setRole(Role.HOTEL);
        this.contactDetails = new ContactInfo();
    }

    public Hotel(String firstName, String lastName, String email, String hotelName) {
        super(firstName, lastName, email, Role.HOTEL);
        this.hotelName = hotelName;
        this.contactDetails = new ContactInfo();
    }

    // --- OOP CONCEPT: METHOD OVERRIDING (Interface Implementation) ---

    @Override
    public String getDisplayName() {
        return (hotelName != null && !hotelName.isEmpty()) ? hotelName : getFirstName() + " Hotel";
    }

    @Override
    public String getDisplayCategory() {
        return "Hotel & Venue";
    }

    @Override
    public String getDisplayLocation() {
        return location != null ? location : "Location Pending";
    }

    /**
     * OOP CONCEPT: METHOD OVERRIDING
     * Specializing the behavior of getAccountSummary() for Hotels.
     */
    @Override
    public String getAccountSummary() {
        return "Hotel Profile: " + getDisplayName() + " located in " + getDisplayLocation();
    }
}
