package com.wedding.dreamwedding.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "wishlists")
public class WishlistItem {
    @Id
    private String id;
    private String customerId;
    private String vendorId;
    private String vendorName;
    private String vendorCategory;
    private String vendorLocation;
    private String vendorImage; // first gallery photo
}
