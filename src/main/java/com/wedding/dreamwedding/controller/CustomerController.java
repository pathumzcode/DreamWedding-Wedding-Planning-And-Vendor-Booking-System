package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.dto.CustomerProfileUpdateRequest;
import com.wedding.dreamwedding.entity.Customer;
import com.wedding.dreamwedding.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    // READ Operation: Retrieve a customer by their unique ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ResponseEntity.ok(customer);
    }

    // UPDATE Operation: Modify an existing customer's profile details
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable String id, @RequestBody CustomerProfileUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            customer.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            customer.setLastName(request.getLastName().trim());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            customer.setPassword(request.getPassword().trim());
        }

        customerRepository.save(customer);
        return ResponseEntity.ok(customer);
    }
}
