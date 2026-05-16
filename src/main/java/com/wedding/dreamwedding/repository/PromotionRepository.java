package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    List<Promotion> findByStatus(String status);
}
