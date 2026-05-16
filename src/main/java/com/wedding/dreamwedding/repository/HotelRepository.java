package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Hotel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository for Hotel entity.
 */
public interface HotelRepository extends MongoRepository<Hotel, String> {
    /** Find a hotel by email to check for duplicates */
    Optional<Hotel> findByEmail(String email);
}
