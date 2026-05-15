package com.wedding.dreamwedding.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ENCAPSULATION & DATA MODELING:
 * Tracks detailed financial data for wedding expenses.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "budget_items")
public class BudgetItem {
    @Id
    private String id;
    private String customerId;
    private String name; // e.g., "Photography", "Catering"
    private String category; // Logistics, Media, Entertainment, etc.
    
    private double estimatedBudget;
    private double actualCost;
    private double paidAmount;
    
    private String paymentStatus; // PENDING, PARTIALLY_PAID, FULLY_PAID
    private String notes;
    
    private List<PaymentMilestone> milestones = new ArrayList<>();

    // Logic: Calculate Balance Due
    public double getBalanceDue() {
        return Math.max(0, actualCost - paidAmount);
    }

    // Logic: Check if over budget
    public boolean isOverBudget() {
        return actualCost > estimatedBudget;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMilestone {
        private String description;
        private double amount;
        private LocalDate dueDate;
        private boolean isPaid;
    }
}
