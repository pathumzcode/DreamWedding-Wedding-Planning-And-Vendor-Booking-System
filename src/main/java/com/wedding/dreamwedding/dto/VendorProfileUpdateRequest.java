package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.VendorPackage;
import lombok.Data;
import java.util.List;

@Data
public class VendorProfileUpdateRequest {
    private String businessName;
    private String category;
    private String aboutUs;
    private String location;
    private String websiteLink;
    private String businessEmail;
    private String businessPhone;
    private String businessWhatsapp;
    private String instagramLink;
    private String facebookLink;
    private List<VendorPackage> packages;
    private List<String> galleryPhotos;
}
