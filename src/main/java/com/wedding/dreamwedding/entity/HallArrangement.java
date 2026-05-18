package com.wedding.dreamwedding.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HallArrangement {
    @NotBlank(message = "Hall name is required")
    private String hallName;

    @NotBlank(message = "Seating capacity is required")
    private String seatingCapacity;

    private String pricePerPlate;

    private String description;
}
