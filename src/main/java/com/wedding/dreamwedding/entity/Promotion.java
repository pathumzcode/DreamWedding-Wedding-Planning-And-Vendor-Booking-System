package com.wedding.dreamwedding.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Promotion entity — represents a marketing promotion linked to a Vendor or Hotel.
 * Applies OOP Encapsulation: all fields are private with Lombok-generated accessors.
 * The targetType field enforces polymorphic behaviour at the data layer (VENDOR | HOTEL).
 */
@Data
@Document(collection = "promotions")
public class Promotion {

    @Id
    private String id;

    @NotBlank(message = "Promotion title is required")
    private String title;

    private String description;
    private String imageBase64;

    @NotBlank(message = "Discount details are required")
    private String discountDetails;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Button text is required")
    private String buttonText;

    @NotBlank(message = "Start date is required")
    private String startDate;

    @NotBlank(message = "End date is required")
    private String endDate;

    private String submittedBy;   // vendorId or hotelId (for legacy compat.)
    private String submitterName; // display name of vendor/hotel/admin

    /**
     * The MongoDB ID of the linked Vendor or Hotel.
     * Enables direct navigation from the promotion card to the correct detail page.
     */
    @NotBlank(message = "A linked vendor or hotel must be selected")
    private String targetId;

    /**
     * Discriminator field — either "VENDOR" or "HOTEL".
     * Used by the frontend to construct the correct detail page URL.
     */
    @NotBlank(message = "Target type (VENDOR/HOTEL) is required")
    private String targetType;

    private String status = "PENDING"; // PENDING, APPROVED
}
