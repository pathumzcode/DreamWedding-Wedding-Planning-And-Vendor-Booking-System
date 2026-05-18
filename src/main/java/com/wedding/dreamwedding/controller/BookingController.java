package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.BookingRequest;
import com.wedding.dreamwedding.entity.Booking;
import com.wedding.dreamwedding.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingRepository bookingRepository;
    private final com.wedding.dreamwedding.repository.VendorRepository vendorRepository;
    private final com.wedding.dreamwedding.repository.HotelRepository hotelRepository;
    private final com.wedding.dreamwedding.service.FileService fileService;
    private final BudgetController budgetController;
    
    // Create booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Booking booking = new Booking();
        booking.setVendorId(request.getVendorId());
        booking.setCustomerId(request.getCustomerId());
        booking.setEventDate(request.getEventDate());
        booking.setGuestCount(request.getGuestCount());
        booking.setPackageName(request.getPackageName());
        booking.setPackagePrice(request.getPackagePrice());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setVendorName(request.getVendorName());
        booking.setVendorCategory(request.getVendorCategory());

        // Reliability: Fetch name if missing or to verify
        if (booking.getVendorName() == null || booking.getVendorName().isEmpty()) {
            vendorRepository.findById(request.getVendorId()).ifPresent(v -> {
                booking.setVendorName(v.getBusinessName() != null ? v.getBusinessName() : v.getFirstName());
                if (booking.getVendorCategory() == null) booking.setVendorCategory(v.getCategory());
            });
            if (booking.getVendorName() == null) {
                hotelRepository.findById(request.getVendorId()).ifPresent(h -> {
                    booking.setVendorName(h.getHotelName());
                    if (booking.getVendorCategory() == null) booking.setVendorCategory("Hotel & Venue");
                });
            }
        }
        booking.setStatus("PENDING");
        Booking saved = bookingRepository.save(booking);

        // --- FILE HANDLING: LOGGING ACTIVITY ---
        fileService.logActivity(booking.getCustomerId(), "Created Booking #" + saved.getId());

        return ResponseEntity.ok(Map.of("message", "Booking request submitted successfully!", "booking", saved));
    }

    // Export report for a booking (Demonstrates File Creation)
    @GetMapping("/{id}/report")
    public ResponseEntity<?> exportReport(@PathVariable String id) {
        return bookingRepository.findById(id).map(b -> {
            String reportData = "Customer ID: " + b.getCustomerId() + "\n" +
                              "Vendor ID: " + b.getVendorId() + "\n" +
                              "Event Date: " + b.getEventDate() + "\n" +
                              "Status: " + b.getStatus();
            
            String fileName = fileService.exportBookingReport(b.getId(), reportData);
            return ResponseEntity.ok(Map.of("message", "Report exported to file: " + fileName));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Get all bookings for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Booking>> getCustomerBookings(@PathVariable String customerId) {
        return ResponseEntity.ok(bookingRepository.findByCustomerId(customerId));
    }

    // Get all bookings for a vendor
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Booking>> getVendorBookings(@PathVariable String vendorId) {
        return ResponseEntity.ok(bookingRepository.findByVendorId(vendorId));
    }

    // Get all bookings (admin)
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    // Update booking status (vendor: CONFIRMED / REJECTED / vendor: add note)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        return bookingRepository.findById(id).map(b -> {
            String oldStatus = b.getStatus();
            if (body.containsKey("status")) b.setStatus(body.get("status"));
            if (body.containsKey("vendorNote")) b.setVendorNote(body.get("vendorNote"));
            bookingRepository.save(b);

            // ── BUDGET SYNC ──────────────────────────────────────
            String newStatus = b.getStatus();
            try {
                if ("CONFIRMED".equals(newStatus) && !"CONFIRMED".equals(oldStatus)) {
                    // Booking just confirmed → auto-add to budget expense log
                    budgetController.syncFromBooking(b);
                } else if (("REJECTED".equals(newStatus) || "CANCELLED".equals(newStatus))
                        && "CONFIRMED".equals(oldStatus)) {
                    // Confirmed booking now rejected/cancelled → remove from budget
                    budgetController.removeBookingExpense(b.getCustomerId(), b.getId());
                }
            } catch (Exception e) {
                // Budget sync failure must never break the booking update
                System.err.println("Budget sync warning: " + e.getMessage());
            }
            // ─────────────────────────────────────────────────────

            return ResponseEntity.ok(Map.of("message", "Booking updated"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Customer edits their booking
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable String id, @RequestBody BookingRequest request) {
        return bookingRepository.findById(id).map(b -> {
            b.setEventDate(request.getEventDate());
            b.setGuestCount(request.getGuestCount());
            b.setSpecialRequests(request.getSpecialRequests());
            b.setPackageName(request.getPackageName());
            b.setPackagePrice(request.getPackagePrice());
            bookingRepository.save(b);
            return ResponseEntity.ok(Map.of("message", "Booking updated"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable String id) {
        // ── BUDGET SYNC: remove expense linked to this booking ───
        bookingRepository.findById(id).ifPresent(b -> {
            try {
                budgetController.removeBookingExpense(b.getCustomerId(), id);
            } catch (Exception e) {
                System.err.println("Budget sync on delete warning: " + e.getMessage());
            }
        });
        // ─────────────────────────────────────────────────────────
        bookingRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Booking deleted"));
    }
}
