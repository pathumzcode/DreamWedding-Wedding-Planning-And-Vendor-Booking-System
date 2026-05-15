package com.wedding.dreamwedding.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * OOP CONCEPT: INHERITANCE
 * Customer inherits core user identity from BaseUser.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Document(collection = "customers")
public class Customer extends BaseUser {

    // --- OOP CONCEPT: CONSTRUCTORS ---

    public Customer() {
        super();
        setRole(Role.CUSTOMER);
    }

    public Customer(String firstName, String lastName, String email) {
        super(firstName, lastName, email, Role.CUSTOMER);
    }

    /**
     * OOP CONCEPT: POLYMORPHISM (Implementation of abstract method)
     */
    @Override
    public String getAccountSummary() {
        return "Customer Account: " + getFullName() + " (Planning a Wedding)";
    }
}
