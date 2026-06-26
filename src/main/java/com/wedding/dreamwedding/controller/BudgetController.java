package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.BudgetPlan;
import com.wedding.dreamwedding.repository.BudgetPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * REST Controller for the Wedding Budget Planner.
 *
 * Design: One BudgetPlan document per customer (upsert pattern).
 * All budget data (categories and expenses) lives in one document.
 *
 * Endpoints:
 *  GET    /api/budget/{customerId}           — fetch full plan
 *  PUT    /api/budget/{customerId}/setup     — set/update total budget + category allocations
 *  POST   /api/budget/{customerId}/expenses  — add an expense
 *  PUT    /api/budget/{customerId}/expenses/{expenseId} — update an expense
 *  DELETE /api/budget/{customerId}/expenses/{expenseId} — delete an expense
 */
@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetPlanRepository budgetPlanRepository;

    // ─────────────────────────────────────────────────────────────
    // HELPER: get or create a customer's budget plan
    // ─────────────────────────────────────────────────────────────
    private BudgetPlan getOrCreate(String customerId) {
        return budgetPlanRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    BudgetPlan plan = new BudgetPlan();
                    plan.setCustomerId(customerId);
                    return budgetPlanRepository.save(plan);
                });
    }

    // ─────────────────────────────────────────────────────────────
    // GET full budget plan
    // ─────────────────────────────────────────────────────────────
    // READ Operation: Retrieve the full budget plan for a customer
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getPlan(@PathVariable String customerId) {
        BudgetPlan plan = getOrCreate(customerId);
        return ResponseEntity.ok(enrichedPlan(plan));
    }

    // ─────────────────────────────────────────────────────────────
    // PUT setup: set totalBudget + category allocations
    // ─────────────────────────────────────────────────────────────
    // UPDATE Operation: Set up or update the total budget and category allocations
    @PutMapping("/{customerId}/setup")
    public ResponseEntity<?> setupBudget(@PathVariable String customerId, @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);

        if (body.containsKey("totalBudget")) {
            double newBudget = ((Number) body.get("totalBudget")).doubleValue();
            if (newBudget <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Total budget must be greater than 0"));
            }
            plan.setTotalBudget(newBudget);
        }

        if (body.containsKey("categories")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cats = (List<Map<String, Object>>) body.get("categories");
            List<BudgetPlan.CategoryAllocation> allocations = cats.stream().map(c -> {
                BudgetPlan.CategoryAllocation a = new BudgetPlan.CategoryAllocation();
                a.setCategoryName((String) c.get("categoryName"));
                a.setPercentage(((Number) c.getOrDefault("percentage", 0)).doubleValue());
                a.setAllocatedAmount(((Number) c.getOrDefault("allocatedAmount", 0)).doubleValue());
                return a;
            }).toList();
            plan.setCategories(allocations);
        }

        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(enrichedPlan(plan));
    }

    // ─────────────────────────────────────────────────────────────
    // POST add expense
    // ─────────────────────────────────────────────────────────────
    // CREATE Operation: Add a new expense to a customer's budget plan
    @PostMapping("/{customerId}/expenses")
    public ResponseEntity<?> addExpense(@PathVariable String customerId, @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);

        BudgetPlan.BudgetExpense expense = new BudgetPlan.BudgetExpense();
        expense.setId(UUID.randomUUID().toString());
        expense.setCategoryName((String) body.get("categoryName"));
        expense.setDescription((String) body.get("description"));
        // Guard: amount is required and must be positive
        Object amountRaw = body.get("amount");
        if (amountRaw == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "amount is required"));
        }
        double amountValue = ((Number) amountRaw).doubleValue();
        if (amountValue <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "amount must be greater than 0"));
        }
        expense.setAmount(amountValue);
        expense.setDate(body.getOrDefault("date", LocalDateTime.now().toLocalDate().toString()).toString());

        plan.getExpenses().add(expense);
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense added", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // PUT update expense
    // ─────────────────────────────────────────────────────────────
    // UPDATE Operation: Modify an existing expense in a customer's budget plan
    @PutMapping("/{customerId}/expenses/{expenseId}")
    public ResponseEntity<?> updateExpense(@PathVariable String customerId,
                                           @PathVariable String expenseId,
                                           @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);
        plan.getExpenses().stream()
                .filter(e -> expenseId.equals(e.getId()))
                .findFirst()
                .ifPresent(expense -> {
                    if (body.containsKey("categoryName")) expense.setCategoryName((String) body.get("categoryName"));
                    if (body.containsKey("description"))  expense.setDescription((String) body.get("description"));
                    if (body.containsKey("amount"))       expense.setAmount(((Number) body.get("amount")).doubleValue());
                });
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense updated", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE expense
    // ─────────────────────────────────────────────────────────────
    // DELETE Operation: Remove an expense from a customer's budget plan
    @DeleteMapping("/{customerId}/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable String customerId, @PathVariable String expenseId) {
        BudgetPlan plan = getOrCreate(customerId);
        plan.getExpenses().removeIf(e -> expenseId.equals(e.getId()));
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense deleted", "plan", enrichedPlan(plan)));
    }



    // ─────────────────────────────────────────────────────────────
    // BOOKING SYNC: called internally from BookingController
    // ─────────────────────────────────────────────────────────────

    /**
     * Called when a booking status becomes CONFIRMED.
     * Creates an expense entry linked to this booking (idempotent — won't duplicate).
     */
    public void syncFromBooking(com.wedding.dreamwedding.entity.Booking booking) {
        if (booking.getCustomerId() == null) return;
        BudgetPlan plan = getOrCreate(booking.getCustomerId());

        // Check if expense for this booking already exists
        boolean alreadyExists = plan.getExpenses().stream()
                .anyMatch(e -> booking.getId().equals(e.getBookingId()));
        if (alreadyExists) return;

        // Parse numeric price from packagePrice string
        double amount = 0;
        if (booking.getPackagePrice() != null) {
            String cleaned = booking.getPackagePrice().replace(",", "");
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\d+(\\.\\d+)?").matcher(cleaned);
            if (m.find()) amount = Double.parseDouble(m.group());
        }
        if (amount <= 0) return; // No price — skip auto-logging

        BudgetPlan.BudgetExpense expense = new BudgetPlan.BudgetExpense();
        expense.setId(java.util.UUID.randomUUID().toString());
        expense.setBookingId(booking.getId());
        expense.setCategoryName(guessCategoryFromVendor(booking.getVendorCategory()));
        expense.setDescription((booking.getVendorName() != null ? booking.getVendorName() : "Vendor")
                + (booking.getPackageName() != null ? " — " + booking.getPackageName() : ""));
        expense.setAmount(amount);
        expense.setDate(java.time.LocalDate.now().toString());

        plan.getExpenses().add(expense);
        plan.setUpdatedAt(java.time.LocalDateTime.now());
        budgetPlanRepository.save(plan);
    }

    /**
     * Called when a booking is REJECTED, cancelled, or deleted.
     * Removes the auto-synced expense for that booking.
     */
    public void removeBookingExpense(String customerId, String bookingId) {
        if (customerId == null || bookingId == null) return;
        budgetPlanRepository.findByCustomerId(customerId).ifPresent(plan -> {
            boolean removed = plan.getExpenses().removeIf(e -> bookingId.equals(e.getBookingId()));
            if (removed) {
                plan.setUpdatedAt(java.time.LocalDateTime.now());
                budgetPlanRepository.save(plan);
            }
        });
    }

    /** Maps vendor category string to one of the 7 budget categories */
    private String guessCategoryFromVendor(String vendorCategory) {
        if (vendorCategory == null) return "Gifts";
        String cat = vendorCategory.toLowerCase();
        if (cat.contains("hotel") || cat.contains("venue"))                                                      return "Hotels and Venue";
        if (cat.contains("photo"))                                                                               return "Photography";
        if (cat.contains("food") || cat.contains("cater"))                                                      return "Food and catering";
        if (cat.contains("florist") || cat.contains("flower"))                                                  return "Florist";
        if (cat.contains("music") || cat.contains("danc") || cat.contains("dj"))                                return "Wedding music and dancing";
        if (cat.contains("jewel") || cat.contains("attire") || cat.contains("dress") || cat.contains("beauty")) return "Jewellery and Attire";
        return "Gifts";
    }


    // ─────────────────────────────────────────────────────────────
    // Enrich the plan with computed totals per category
    // ─────────────────────────────────────────────────────────────
    private Map<String, Object> enrichedPlan(BudgetPlan plan) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", plan.getId());
        result.put("customerId", plan.getCustomerId());
        result.put("totalBudget", plan.getTotalBudget());
        result.put("totalSpent", plan.getTotalSpent());
        result.put("totalRemaining", plan.getTotalRemaining());
        result.put("updatedAt", plan.getUpdatedAt());

        // Enrich categories with spent/remaining per category
        List<Map<String, Object>> enrichedCats = plan.getCategories().stream().map(cat -> {
            double catSpent = plan.getExpenses().stream()
                    .filter(e -> cat.getCategoryName().equals(e.getCategoryName()))
                    .mapToDouble(BudgetPlan.BudgetExpense::getAmount)
                    .sum();
            double catRemaining = cat.getAllocatedAmount() - catSpent;
            boolean catOver = cat.getAllocatedAmount() > 0 && catSpent > cat.getAllocatedAmount();

            Map<String, Object> c = new LinkedHashMap<>();
            c.put("categoryName", cat.getCategoryName());
            c.put("percentage", cat.getPercentage());
            c.put("allocatedAmount", cat.getAllocatedAmount());
            c.put("spentAmount", catSpent);
            c.put("remainingAmount", catRemaining);
            c.put("isOverBudget", catOver);
            c.put("percentUsed", cat.getAllocatedAmount() > 0 ? Math.round((catSpent / cat.getAllocatedAmount()) * 1000.0) / 10.0 : 0);
            return c;
        }).toList();

        // Plan-level isOverBudget: total spent exceeds total budget
        boolean planOver = plan.getTotalBudget() > 0 && plan.getTotalSpent() > plan.getTotalBudget();
        result.put("isOverBudget", planOver);

        result.put("categories", enrichedCats);
        result.put("expenses", plan.getExpenses());
        return result;
    }
}
