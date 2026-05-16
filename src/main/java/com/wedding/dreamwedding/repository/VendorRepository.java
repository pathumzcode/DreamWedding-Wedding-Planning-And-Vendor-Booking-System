package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Vendor;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository for Vendor entity.
 */
public interface VendorRepository extends MongoRepository<Vendor, String> {
    /** Find a vendor by email to check for duplicates */
    Optional<Vendor> findByEmail(String email);
}
