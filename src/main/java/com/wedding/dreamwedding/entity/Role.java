package com.wedding.dreamwedding.entity;

/**
 * Enum representing the possible roles a user can have.
 * Each role maps to a different MongoDB collection.
 */
public enum Role {
    CUSTOMER,  // Saved to 'customers' collection
    VENDOR,    // Saved to 'vendors' collection
    ADMIN,     // Saved to 'admins' collection
    HOTEL      // Saved to 'hotels' collection
}
