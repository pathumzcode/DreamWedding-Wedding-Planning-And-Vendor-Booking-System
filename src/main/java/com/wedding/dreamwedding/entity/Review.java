package com.wedding.dreamwedding.entity;

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

    private String type; // "VENDOR" or "SITE"

    // VENDOR review fields
    private String vendorId;
    private String vendorName;
    private String bookingId;

    // SITE review fields
    private String experienceCategory;
    private String suggestion;

    // Common fields
    private Integer rating;
    private String reviewTitle;
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
    private String reviewerProfilePic;
    private String reviewerRole; // "CUSTOMER" or "VENDOR"

    private LocalDateTime reviewDate = LocalDateTime.now();

    // Features
    private boolean verified = false;
    private Integer helpfulCount = 0;
    private List<String> helpfulUserIds = new ArrayList<>();

    // Vendor reply
    private String vendorReply;
    private LocalDateTime replyDate;

    // Admin moderation
    private String status = "APPROVED"; // "PENDING", "APPROVED", "REJECTED"
}
