package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.ReviewDTO;
import com.wedding.dreamwedding.entity.Review;
import com.wedding.dreamwedding.service.ReviewService;
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

    @PostMapping("/vendor")
    public ResponseEntity<?> createVendorReview(@RequestBody ReviewDTO dto) {
        try {
            Review review = reviewService.createVendorReview(dto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/site")
    public ResponseEntity<?> createSiteReview(@RequestBody ReviewDTO dto, @RequestParam String role) {
        try {
            Review review = reviewService.createSiteReview(dto, role);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<Review>> getVendorReviews(@PathVariable String vendorId) {
        return ResponseEntity.ok(reviewService.getVendorReviews(vendorId));
    }

    @GetMapping("/site")
    public ResponseEntity<List<Review>> getSiteReviews() {
        return ResponseEntity.ok(reviewService.getSiteReviews());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getMyReviews(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getMyReviews(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PostMapping("/{reviewId}/helpful/{userId}")
    public ResponseEntity<Review> markHelpful(@PathVariable String reviewId, @PathVariable String userId) {
        return ResponseEntity.ok(reviewService.markHelpful(reviewId, userId));
    }

    @PostMapping("/{reviewId}/reply")
    public ResponseEntity<?> addVendorReply(@PathVariable String reviewId, @RequestBody Map<String, String> body) {
        try {
            String vendorId = body.get("vendorId");
            String replyMessage = body.get("replyMessage");
            return ResponseEntity.ok(reviewService.addVendorReply(reviewId, vendorId, replyMessage));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{reviewId}/status")
    public ResponseEntity<Review> updateStatus(@PathVariable String reviewId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(reviewService.updateStatus(reviewId, status));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
