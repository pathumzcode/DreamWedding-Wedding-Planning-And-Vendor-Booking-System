package com.wedding.dreamwedding.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookingRequest {

    @NotBlank(message = "Vendor ID is required")
    private String vendorId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    private String vendorName;
    private String vendorCategory;
    private String customerName;

    @NotBlank(message = "Event date is required")
    private String eventDate;

    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private Integer guestCount;

    private String packageName;
    private String packagePrice;
    private String specialRequests;
}
