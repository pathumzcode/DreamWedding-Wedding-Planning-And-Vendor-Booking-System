package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Repository for Customer entity.
 */
public interface CustomerRepository extends MongoRepository<Customer, String> {
    /** Find a customer by email to check for duplicates */
    Optional<Customer> findByEmail(String email);
}
