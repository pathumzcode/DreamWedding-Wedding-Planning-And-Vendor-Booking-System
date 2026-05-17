package com.wedding.dreamwedding.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP CONCEPT: ENCAPSULATION & DATA MODELING
 * BudgetPlan is the master document for a customer's wedding budget.
 * It holds the total budget, category allocations, expenses, and vendor estimates.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "budget_plans")
public class BudgetPlan {

    @Id
    private String id;

    @Indexed(unique = true)
    private String customerId;

    /** Master budget limit set by the customer */
    private double totalBudget = 0.0;

    /** Category allocations — each defines how much of the total is reserved for a wedding category */
    private List<CategoryAllocation> categories = new ArrayList<>();

    /** All logged expense entries */
    private List<BudgetExpense> expenses = new ArrayList<>();

    /** Vendor/hotel estimates added from booking pages (projections, not actual spend) */
    private List<VendorEstimate> estimates = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ───────────────────────────────────────────────
    // Computed helpers (not persisted, used in logic)
    // ───────────────────────────────────────────────

    /** Total spent = sum of all expense amounts */
    public double getTotalSpent() {
        return expenses.stream().mapToDouble(BudgetExpense::getAmount).sum();
    }

    /** Total remaining from the master budget (negative means over budget) */
    public double getTotalRemaining() {
        return totalBudget - getTotalSpent();
    }

    /** True if total expenses exceed the master budget */
    public boolean isOverBudget() {
        return totalBudget > 0 && getTotalSpent() > totalBudget;
    }

    // ───────────────────────────────────────────────
    // Nested: Category Allocation
    // ───────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryAllocation {
        private String categoryName;
        private double percentage;       // e.g. 40.0
        private double allocatedAmount;  // e.g. 12000.00
    }

    // ───────────────────────────────────────────────
    // Nested: Budget Expense
    // ───────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetExpense {
        private String id;
        private String categoryName;
        private String description;
        private double amount;
        private String estimateId;          // optional link to a VendorEstimate
        private String bookingId;           // link to a Booking (auto-synced)
        private String date;                // ISO date string
    }

    // ───────────────────────────────────────────────
    // Nested: Vendor Estimate (projection / forecast)
    // ───────────────────────────────────────────────

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorEstimate {
        private String id;
        private String vendorId;
        private String vendorName;
        private String packageName;
        private String categoryName;
        private double estimatedAmount;
        private String type;               // "vendor" or "hotel"
        private String addedAt;
    }
}
