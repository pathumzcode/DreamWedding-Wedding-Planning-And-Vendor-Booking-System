package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.VendorProfileUpdateRequest;
import com.wedding.dreamwedding.entity.Vendor;
import com.wedding.dreamwedding.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;

    @GetMapping
    public ResponseEntity<?> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAll().stream()
                .filter(Vendor::isProfileCompleted)
                .toList());
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopVendors() {
        return ResponseEntity.ok(vendorRepository.findAll().stream()
                .filter(Vendor::isProfileCompleted)
                .sorted((v1, v2) -> Integer.compare(v2.getReviewCount(), v1.getReviewCount()))
                .limit(6)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable String id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return ResponseEntity.ok(vendor);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody VendorProfileUpdateRequest request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setBusinessName(request.getBusinessName());
        vendor.setCategory(request.getCategory());
        vendor.setAboutUs(request.getAboutUs());
        vendor.setLocation(request.getLocation());
        if (vendor.getBusinessContact() == null) {
            vendor.setBusinessContact(new com.wedding.dreamwedding.entity.ContactInfo());
        }
        vendor.getBusinessContact().setWebsite(request.getWebsiteLink());
        vendor.getBusinessContact().setEmail(request.getBusinessEmail());
        vendor.getBusinessContact().setPhone(request.getBusinessPhone());
        vendor.getBusinessContact().setWhatsapp(request.getBusinessWhatsapp());
        vendor.setInstagramLink(request.getInstagramLink());
        vendor.setFacebookLink(request.getFacebookLink());
        vendor.setPackages(request.getPackages());
        vendor.setGalleryPhotos(request.getGalleryPhotos());
        vendor.setProfileCompleted(true);

        vendorRepository.save(vendor);

        return ResponseEntity.ok(vendor);
    }
}
