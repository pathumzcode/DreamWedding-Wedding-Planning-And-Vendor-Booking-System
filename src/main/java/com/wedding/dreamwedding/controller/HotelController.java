package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.HotelProfileUpdateRequest;
import com.wedding.dreamwedding.entity.Hotel;
import com.wedding.dreamwedding.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelRepository hotelRepository;

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody HotelProfileUpdateRequest request) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Hotel not found"));
        }

        hotel.setHotelName(request.getHotelName());
        hotel.setLocation(request.getLocation());
        hotel.setAboutUs(request.getAboutUs());
        if (hotel.getContactDetails() == null) {
            hotel.setContactDetails(new com.wedding.dreamwedding.entity.ContactInfo());
        }
        hotel.getContactDetails().setEmail(request.getContactEmail());
        hotel.getContactDetails().setPhone(request.getContactPhone());
        hotel.getContactDetails().setWhatsapp(request.getWhatsappNumber());
        hotel.getContactDetails().setWebsite(request.getWebsiteLink());
        hotel.setInstagramLink(request.getInstagramLink());
        hotel.setFacebookLink(request.getFacebookLink());
        hotel.setAmenities(request.getAmenities());
        hotel.setRooms(request.getRooms());
        hotel.setHallArrangements(request.getHallArrangements());
        hotel.setGalleryPhotos(request.getGalleryPhotos());
        hotel.setProfileCompleted(true);

        hotelRepository.save(hotel);
        return ResponseEntity.ok(Map.of("message", "Hotel profile updated successfully"));
    }
    @GetMapping
    public ResponseEntity<?> getAllHotels() {
        return ResponseEntity.ok(hotelRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHotelById(@PathVariable String id) {
        return hotelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
