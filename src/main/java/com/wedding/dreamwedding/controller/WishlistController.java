package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.WishlistItem;
import com.wedding.dreamwedding.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WishlistItem>> getWishlist(@PathVariable String customerId) {
        return ResponseEntity.ok(wishlistRepository.findByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody WishlistItem item) {
        wishlistRepository.save(item);
        return ResponseEntity.ok(Map.of("message", "Added to wishlist"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable String id) {
        wishlistRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }
}
