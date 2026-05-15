package com.wedding.dreamwedding.dto;

import com.wedding.dreamwedding.entity.HallArrangement;
import com.wedding.dreamwedding.entity.HotelRoom;
import lombok.Data;
import java.util.List;

@Data
public class HotelProfileUpdateRequest {
    private String hotelName;
    private String location;
    private String aboutUs;
    private String contactEmail;
    private String contactPhone;
    private String whatsappNumber;
    private String websiteLink;
    private String instagramLink;
    private String facebookLink;
    private List<String> amenities;
    private List<HotelRoom> rooms;
    private List<HallArrangement> hallArrangements;
    private List<String> galleryPhotos;
}
