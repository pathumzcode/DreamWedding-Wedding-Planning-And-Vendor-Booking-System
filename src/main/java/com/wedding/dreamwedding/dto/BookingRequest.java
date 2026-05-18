package com.wedding.dreamwedding.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private String vendorId;
    private String vendorName;
    private String vendorCategory;
    private String customerId;
    private String eventDate;
    private Integer guestCount;
    private String packageName;
    private String packagePrice;
    private String specialRequests;
}
