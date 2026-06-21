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
    // READ Operation: Get system statistics
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
    // READ Operation: Get all vendors
    @GetMapping("/vendors")
    public ResponseEntity<?> getAllVendors() {
        return ResponseEntity.ok(vendorRepository.findAll());
    }

    // READ Operation: Get all customers
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    // READ Operation: Get all hotels
    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        return ResponseEntity.ok(hotelRepository.findAll());
    }

    // --- Delete Users ---
    // DELETE Operation: Delete a vendor
    @DeleteMapping("/vendors/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable String id) {
        vendorRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Vendor deleted"));
    }

    // DELETE Operation: Delete a customer
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        customerRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Customer deleted"));
    }

    // DELETE Operation: Delete a hotel
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<?> deleteHotel(@PathVariable String id) {
        hotelRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Hotel deleted"));
    }

    // DELETE Operation: Delete a booking
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {
        bookingRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Booking deleted"));
    }

    // READ Operation: Get all business names
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

    /**
     * Returns all registered vendors and hotels as structured objects with id, name, and type.
     * Used by the admin promotion form for smart autocomplete + navigation linking.
     * Demonstrates OOP: each result is a polymorphic map representing either a Vendor or Hotel.
     */
    // READ Operation: Returns all registered vendors and hotels as structured objects
    @GetMapping("/business-entities")
    public ResponseEntity<?> getBusinessEntities() {
        java.util.List<java.util.Map<String, String>> entities = new java.util.ArrayList<>();
        vendorRepository.findAll().forEach(v -> {
            if (v.getBusinessName() != null && v.getId() != null) {
                java.util.Map<String, String> m = new java.util.HashMap<>();
                m.put("id",       v.getId());
                m.put("name",     v.getBusinessName());
                m.put("type",     "VENDOR");
                m.put("category", v.getCategory() != null ? v.getCategory() : "");
                entities.add(m);
            }
        });
        hotelRepository.findAll().forEach(h -> {
            if (h.getHotelName() != null && h.getId() != null) {
                java.util.Map<String, String> m = new java.util.HashMap<>();
                m.put("id",       h.getId());
                m.put("name",     h.getHotelName());
                m.put("type",     "HOTEL");
                m.put("category", "Hotels");
                entities.add(m);
            }
        });
        entities.sort(java.util.Comparator.comparing(e -> e.get("name")));
        return ResponseEntity.ok(entities);
    }
}
