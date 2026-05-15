package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.WishlistItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface WishlistRepository extends MongoRepository<WishlistItem, String> {
    List<WishlistItem> findByCustomerId(String customerId);
    void deleteByCustomerIdAndVendorId(String customerId, String vendorId);
}
