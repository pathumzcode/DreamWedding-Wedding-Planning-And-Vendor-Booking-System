package com.wedding.dreamwedding.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReviewDTO {
    private String type; // "VENDOR" or "SITE"
    
    private String vendorId;
    private String bookingId;
    
    private String experienceCategory;
    private String suggestion;
    
    private Integer rating;
    private String reviewTitle;
    private String reviewMessage;
    private List<String> photos;
    
    private String eventDate;
    private String eventType;
    private String bookedBy;
    private java.util.Map<String, Integer> serviceRatings;
    private List<String> reviewTags;
    
    private String reviewerId;
}
