package com.wedding.dreamwedding.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * OOP CONCEPT: OBJECT ASSOCIATION
 * Booking serves as an association class linking a Customer and a Vendor/Hotel.
 */
@Getter
@Setter
@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    /**
     * OOP CONCEPT: ASSOCIATION & VALIDATION
     * These IDs represent a relationship between independent objects (Customer & Vendor).
     */
    @NotBlank(message = "Vendor ID is required")
    private String vendorId;
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String vendorName;
    private String vendorCategory;
    
    @NotBlank(message = "Event date is required")
    private String eventDate;
    
    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private Integer guestCount;
    
    private String packageName;
    private String packagePrice;
    private String specialRequests;
    private String vendorNote;
    
    @Pattern(regexp = "^(PENDING|CONFIRMED|REJECTED|COMPLETED|CANCELLED)$", message = "Invalid status format")
    private String status = "PENDING"; // PENDING, CONFIRMED, REJECTED, COMPLETED

    // --- OOP CONCEPT: CONSTRUCTORS ---

    public Booking() {}

    public Booking(String vendorId, String customerId, String eventDate) {
        this.vendorId = vendorId;
        this.customerId = customerId;
        this.eventDate = eventDate;
    }

    // --- OOP CONCEPT: ENCAPSULATION (Accessor/Mutator Logic) ---

    public Integer getGuestCount() { return guestCount; }

    /**
     * ENCAPSULATION LOGIC:
     * Protects the object's state by validating input before assignment.
     */
    public void setGuestCount(Integer guestCount) {
        if (guestCount != null && guestCount < 0) {
            // Business logic to prevent negative guests
            this.guestCount = 0;
        } else {
            this.guestCount = guestCount;
        }
    }

    public String getStatus() { return status; }

    /**
     * ENCAPSULATION LOGIC:
     * Standardizes status format to ensure consistency across the system.
     */
    public void setStatus(String status) {
        if (status != null) {
            this.status = status.toUpperCase().trim();
        }
    }
}
