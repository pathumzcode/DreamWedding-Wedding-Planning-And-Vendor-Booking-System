package com.wedding.dreamwedding.entity;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;

    @NotBlank(message = "Review type is required")
    @Pattern(regexp = "^(VENDOR|SITE)$", message = "Type must be VENDOR or SITE")
    private String type; // "VENDOR" or "SITE"

    // VENDOR review fields
    private String vendorId;
    private String vendorName;
    private String bookingId;

    // SITE review fields
    private String experienceCategory;
    private String suggestion;

    // Common fields
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    private String reviewTitle;
    @NotBlank(message = "Review message is required")
    @Size(min = 10, message = "Review message must be at least 10 characters")
    private String reviewMessage;
    private List<String> photos = new ArrayList<>();
    
    // Detailed Review fields
    private String eventDate;
    private String eventType;
    private String bookedBy;
    private java.util.Map<String, Integer> serviceRatings = new java.util.HashMap<>();
    private List<String> reviewTags = new ArrayList<>();

    private String reviewerId;
    private String reviewerName;
    @Pattern(regexp = "^(CUSTOMER|VENDOR)$", message = "Reviewer role must be CUSTOMER or VENDOR")
    private String reviewerRole; // "CUSTOMER" or "VENDOR"
    private String reviewerProfilePic; // Added to show user profile in comment section

    private LocalDateTime reviewDate = LocalDateTime.now();

    // Features
    private boolean verified = false;
    private Integer helpfulCount = 0;
    private List<String> helpfulUserIds = new ArrayList<>();

    // Vendor reply
    private String vendorReply;
    private LocalDateTime replyDate;

    // Admin moderation
    @Pattern(regexp = "^(PENDING|APPROVED|REJECTED)$", message = "Status must be PENDING, APPROVED, or REJECTED")
    private String status = "APPROVED"; // "PENDING", "APPROVED", "REJECTED"
}
