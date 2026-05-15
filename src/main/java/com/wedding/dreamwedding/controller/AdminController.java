package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.SiteSettings;
import com.wedding.dreamwedding.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;

    // --- Stats ---
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of(
            "totalVendors", vendorRepository.count(),
            "totalCustomers", customerRepository.count(),
            "totalHotels", hotelRepository.count(),
            "totalBookings", bookingRepository.count()
        ));
    }

    // --- Users Lists ---
    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAll());
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        return ResponseEntity.ok(hotelRepository.findAll());
    }

    // --- Delete Users ---
    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        vendorRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Vendor deleted"));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Customer deleted"));
    }

    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable String id) {
        hotelRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Hotel deleted"));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {
        bookingRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Booking deleted"));
    }

    @GetMapping("/business-names")
    public ResponseEntity<?> getBusinessNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        vendorRepository.findAll().forEach(v -> {
            if (v.getBusinessName() != null) names.add(v.getBusinessName());
        });
        hotelRepository.findAll().forEach(h -> {
            if (h.getHotelName() != null) names.add(h.getHotelName());
        });
        return ResponseEntity.ok(names.stream().distinct().sorted().toList());
    }
}
