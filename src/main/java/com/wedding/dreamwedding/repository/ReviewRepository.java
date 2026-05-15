package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByVendorIdAndStatus(String vendorId, String status);
    List<Review> findByTypeAndStatus(String type, String status);
    List<Review> findByReviewerId(String reviewerId);
    List<Review> findByVendorId(String vendorId);
    List<Review> findByStatus(String status);
}
