package com.wedding.dreamwedding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorPackage {
    @NotBlank(message = "Package name is required")
    private String name;
    
    @NotNull(message = "Package price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;
    
    private String description;
}
