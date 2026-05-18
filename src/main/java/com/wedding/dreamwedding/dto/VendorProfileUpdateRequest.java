package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.VendorPackage;
import lombok.Data;
import java.util.List;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class VendorProfileUpdateRequest {
    @NotBlank(message = "Business name is required")
    private String businessName;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String aboutUs;
    private String location;
    private String websiteLink;
    
    @Email(message = "Invalid email format")
    private String businessEmail;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "Phone number must start with +94 followed by 9 digits")
    private String businessPhone;
    
    @Pattern(regexp = "^\\+94\\d{9}$", message = "WhatsApp number must start with +94 followed by 9 digits")
    private String businessWhatsapp;
    
    private String instagramLink;
    private String facebookLink;
    
    @Valid
    @Size(max = 6, message = "Maximum of 6 packages allowed")
    private List<VendorPackage> packages;
    
    private List<String> galleryPhotos;
}
