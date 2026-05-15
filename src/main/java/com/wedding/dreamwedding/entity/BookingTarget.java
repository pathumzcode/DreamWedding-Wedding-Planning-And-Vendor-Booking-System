package com.wedding.dreamwedding.entity;

/**
 * OOP CONCEPT: INTERFACE (Abstraction)
 * This interface defines the contract for any entity that can be booked on the platform.
 * It allows for Polymorphism where different types (Vendors, Hotels) can be treated as a single 'BookingTarget'.
 */
public interface BookingTarget {
    
    /** Get the display name of the business */
    String getDisplayName();
    
    /** Get the primary category (e.g., 'Caterer', 'Hotel & Venue') */
    String getDisplayCategory();
    
    /** Get the location for display */
    String getDisplayLocation();
    
    /** 
     * OOP CONCEPT: DEFAULT METHOD 
     * Provides a default implementation that can be optionally overridden.
     */
    default boolean isBookable() {
        return true;
    }
}
