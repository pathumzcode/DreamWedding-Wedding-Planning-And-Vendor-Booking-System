package com.wedding.dreamwedding.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * OOP CONCEPT: INHERITANCE
 * Admin entity inherits from BaseUser, specializing as a system manager.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Document(collection = "admins")
public class Admin extends BaseUser {

    // --- OOP CONCEPT: CONSTRUCTORS ---

    public Admin() {
        super();
        setRole(Role.ADMIN);
    }

    public Admin(String firstName, String lastName, String email) {
        super(firstName, lastName, email, Role.ADMIN);
    }

    /**
     * OOP CONCEPT: POLYMORPHISM
     * Implementing the abstract method from BaseUser.
     */
    @Override
    public String getAccountSummary() {
        return "System Administrator: " + getFirstName() + " [" + getEmail() + "]";
    }
}
