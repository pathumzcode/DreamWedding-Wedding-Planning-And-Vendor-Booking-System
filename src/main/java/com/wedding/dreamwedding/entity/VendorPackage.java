package com.wedding.dreamwedding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorPackage {
    private String name;
    private String priceRange;
    private String description;
}
