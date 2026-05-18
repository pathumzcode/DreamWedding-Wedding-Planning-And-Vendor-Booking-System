package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.HallArrangement;
import com.wedding.dreamwedding.entity.HotelRoom;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class HotelProfileUpdateRequest {
    @NotBlank(message = "Hotel name is required")
    private String hotelName;
    
    private String location;
    private String aboutUs;
    
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "Phone number must start with +94 followed by 9 digits")
    private String contactPhone;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "WhatsApp number must start with +94 followed by 9 digits")
    private String whatsappNumber;
    
    private String websiteLink;
    private String instagramLink;
    private String facebookLink;
    private List<String> amenities;
    
    @Valid
    @Size(max = 6, message = "Maximum of 6 rooms allowed")
    private List<HotelRoom> rooms;
    
    @Valid
    @Size(max = 6, message = "Maximum of 6 hall arrangements allowed")
    private List<HallArrangement> hallArrangements;
    
    private List<String> galleryPhotos;
}
