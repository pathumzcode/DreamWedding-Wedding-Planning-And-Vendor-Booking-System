package com.wedding.dreamwedding.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Document(collection = "promotions")
public class Promotion {
    @Id
    private String id;
    private String title;
    private String description;
    private String imageBase64;
    private String discountDetails;
    private String category;
    private String buttonText;
    private String startDate;
    private String endDate;
    private String submittedBy;   // vendorId or hotelId
    private String status = "PENDING"; // PENDING, APPROVED
}
