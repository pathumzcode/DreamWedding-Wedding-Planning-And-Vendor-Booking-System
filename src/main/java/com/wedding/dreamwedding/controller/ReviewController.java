package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.ReviewDTO;
import com.wedding.dreamwedding.entity.Review;
import com.wedding.dreamwedding.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // CREATE Operation: Create a new review for a vendor
    @PostMapping("/vendor")
    public ResponseEntity<?> createVendorReview(@Valid @RequestBody ReviewDTO dto) {
        try {
            Review review = reviewService.createVendorReview(dto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // CREATE Operation: Create a new site review
    @PostMapping("/site")
    public ResponseEntity<?> createSiteReview(@Valid @RequestBody ReviewDTO dto, @RequestParam String role) {
        // Validate role parameter value
        if (!"CUSTOMER".equals(role) && !"VENDOR".equals(role)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role must be CUSTOMER or VENDOR"));
        }
        try {
            Review review = reviewService.createSiteReview(dto, role);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // READ Operation: Retrieve reviews for a specific vendor
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Review>> getVendorReviews(@PathVariable String vendorId) {
        return ResponseEntity.ok(reviewService.getVendorReviews(vendorId));
    }

    // READ Operation: Retrieve all site reviews
    @GetMapping("/site")
    public ResponseEntity<List<Review>> getSiteReviews() {
        return ResponseEntity.ok(reviewService.getSiteReviews());
    }

    // READ Operation: Retrieve reviews created by a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getMyReviews(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getMyReviews(userId));
    }

    // READ Operation: Retrieve all reviews (admin view)
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // UPDATE Operation: Mark a review as helpful
    @PostMapping("/{reviewId}/helpful/{userId}")
    public ResponseEntity<Review> markHelpful(@PathVariable String reviewId, @PathVariable String userId) {
        return ResponseEntity.ok(reviewService.markHelpful(reviewId, userId));
    }

    // UPDATE Operation: Add a vendor's reply to a review
    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<?> addVendorReply(@PathVariable String reviewId, @RequestBody Map<String, String> body) {
        try {
            String vendorId = body.get("vendorId");
            String replyMessage = body.get("replyMessage");
            // Validate reply content
            if (vendorId == null || vendorId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "vendorId is required"));
            }
            if (replyMessage == null || replyMessage.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Reply message cannot be empty"));
            }
            return ResponseEntity.ok(reviewService.addVendorReply(reviewId, vendorId, replyMessage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE Operation: Update the status of a review (e.g., approve/reject)
    @PutMapping("/{reviewId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String reviewId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        // Validate status value before passing to service
        if (status == null || !status.matches("^(PENDING|APPROVED|REJECTED)$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Status must be PENDING, APPROVED, or REJECTED"));
        }
        return ResponseEntity.ok(reviewService.updateStatus(reviewId, status));
    }

    // DELETE Operation: Delete a review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
