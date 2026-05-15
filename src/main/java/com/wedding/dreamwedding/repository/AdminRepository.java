package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository for Admin entity.
 */
public interface AdminRepository extends MongoRepository<Admin, String> {
    /** Find an admin by email to check for duplicates */
    Optional<Admin> findByEmail(String email);
}
