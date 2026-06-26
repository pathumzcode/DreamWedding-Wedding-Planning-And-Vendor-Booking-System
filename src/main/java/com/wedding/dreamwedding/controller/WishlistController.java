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

    // READ Operation: Retrieve wishlist items for a specific customer
    @GetMapping("/{customerId}")
    public ResponseEntity<List<WishlistItem>> getWishlist(@PathVariable String customerId) {
        return ResponseEntity.ok(wishlistRepository.findByCustomerId(customerId));
    }

    // CREATE Operation: Add an item to the customer's wishlist
    @PostMapping
    public ResponseEntity<?> addToWishlist(@RequestBody WishlistItem item) {
        wishlistRepository.save(item);
        return ResponseEntity.ok(Map.of("message", "Added to wishlist"));
    }

    // DELETE Operation: Remove an item from the wishlist by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFromWishlist(@PathVariable String id) {
        wishlistRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Removed from wishlist"));
    }
}
