package com.wedding.dreamwedding.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HotelRoom {
    @NotBlank(message = "Room type is required")
    private String type;

    private String priceRange;
    private String capacity;
}
