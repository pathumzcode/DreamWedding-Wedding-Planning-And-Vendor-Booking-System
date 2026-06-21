package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.Promotion;
import com.wedding.dreamwedding.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionRepository promotionRepository;

    // READ Operation: Get all active (APPROVED + not expired) promotions for homepage
    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions() {
        String today = LocalDate.now().toString();
        List<Promotion> active = promotionRepository.findByStatus("APPROVED")
            .stream()
            .filter(p -> p.getEndDate() != null && p.getEndDate().compareTo(today) >= 0)
            .collect(Collectors.toList());
        return ResponseEntity.ok(active);
    }

    // READ Operation: Get all promotions (admin view)
    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionRepository.findAll());
    }

    // CREATE Operation: Vendor/Hotel submits a promotion
    @PostMapping
    public ResponseEntity<?> submitPromotion(@RequestBody Promotion promotion) {
        promotion.setStatus("PENDING");
        promotionRepository.save(promotion);
        return ResponseEntity.ok(Map.of("message", "Promotion submitted for review"));
    }

    // UPDATE Operation: Admin approves a promotion
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approvePromotion(@PathVariable String id) {
        return promotionRepository.findById(id).map(p -> {
            p.setStatus("APPROVED");
            promotionRepository.save(p);
            return ResponseEntity.ok(Map.of("message", "Promotion approved"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // CREATE Operation: Admin creates a promotion directly
    @PostMapping("/admin")
    public ResponseEntity<?> createAdminPromotion(@RequestBody Promotion promotion) {
        promotion.setStatus("APPROVED");
        Promotion saved = promotionRepository.save(promotion);
        return ResponseEntity.ok(saved);
    }

    // UPDATE Operation: Update an existing promotion
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable String id, @RequestBody Promotion promotion) {
        return promotionRepository.findById(id).map(p -> {
            p.setTitle(promotion.getTitle());
            p.setDescription(promotion.getDescription());
            p.setDiscountDetails(promotion.getDiscountDetails());
            p.setCategory(promotion.getCategory());
            p.setButtonText(promotion.getButtonText());
            p.setSubmitterName(promotion.getSubmitterName());
            p.setStartDate(promotion.getStartDate());
            p.setEndDate(promotion.getEndDate());
            if (promotion.getImageBase64() != null) {
                p.setImageBase64(promotion.getImageBase64());
            }
            p.setStatus(promotion.getStatus());
            promotionRepository.save(p);
            return ResponseEntity.ok(Map.of("message", "Promotion updated successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE Operation: Delete a promotion
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable String id) {
        promotionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Promotion deleted"));
    }
}
