package com.wedding.dreamwedding.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class ReviewDTO {

    @NotBlank(message = "Review type is required")
    @Pattern(regexp = "^(VENDOR|SITE)$", message = "Type must be VENDOR or SITE")
    private String type; // "VENDOR" or "SITE"

    private String vendorId;
    private String bookingId;

    private String experienceCategory;
    private String suggestion;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    private String reviewTitle;

    @NotBlank(message = "Review message is required")
    @Size(min = 10, message = "Review message must be at least 10 characters")
    private String reviewMessage;

    private List<String> photos;

    private String eventDate;
    private String eventType;
    private String bookedBy;
    private java.util.Map<String, Integer> serviceRatings;
    private List<String> reviewTags;

    @NotBlank(message = "Reviewer ID is required")
    private String reviewerId;
}
