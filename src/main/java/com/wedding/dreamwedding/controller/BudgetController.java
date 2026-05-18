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
 * All budget data (categories, expenses, estimates) lives in one document.
 *
 * Endpoints:
 *  GET    /api/budget/{customerId}           — fetch full plan
 *  PUT    /api/budget/{customerId}/setup     — set/update total budget + category allocations
 *  POST   /api/budget/{customerId}/expenses  — add an expense
 *  PUT    /api/budget/{customerId}/expenses/{expenseId} — update an expense
 *  DELETE /api/budget/{customerId}/expenses/{expenseId} — delete an expense
 *  POST   /api/budget/{customerId}/estimates  — add a vendor estimate
 *  DELETE /api/budget/{customerId}/estimates/{estimateId} — remove an estimate
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
    @GetMapping("/{customerId}")
    public ResponseEntity<?> getPlan(@PathVariable String customerId) {
        BudgetPlan plan = getOrCreate(customerId);
        return ResponseEntity.ok(enrichedPlan(plan));
    }

    // ─────────────────────────────────────────────────────────────
    // PUT setup: set totalBudget + category allocations
    // ─────────────────────────────────────────────────────────────
    @PutMapping("/{customerId}/setup")
    public ResponseEntity<?> setupBudget(@PathVariable String customerId, @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);

        if (body.containsKey("totalBudget")) {
            plan.setTotalBudget(((Number) body.get("totalBudget")).doubleValue());
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
    @PostMapping("/{customerId}/expenses")
    public ResponseEntity<?> addExpense(@PathVariable String customerId, @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);

        BudgetPlan.BudgetExpense expense = new BudgetPlan.BudgetExpense();
        expense.setId(UUID.randomUUID().toString());
        expense.setCategoryName((String) body.get("categoryName"));
        expense.setDescription((String) body.get("description"));
        expense.setAmount(((Number) body.get("amount")).doubleValue());
        expense.setEstimateId((String) body.getOrDefault("estimateId", null));
        expense.setDate(body.getOrDefault("date", LocalDateTime.now().toLocalDate().toString()).toString());

        plan.getExpenses().add(expense);
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense added", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // PUT update expense
    // ─────────────────────────────────────────────────────────────
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
                    if (body.containsKey("estimateId"))   expense.setEstimateId((String) body.get("estimateId"));
                });
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense updated", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE expense
    // ─────────────────────────────────────────────────────────────
    @DeleteMapping("/{customerId}/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable String customerId, @PathVariable String expenseId) {
        BudgetPlan plan = getOrCreate(customerId);
        plan.getExpenses().removeIf(e -> expenseId.equals(e.getId()));
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Expense deleted", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // POST add vendor estimate (from vendor-details / hotel-details)
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/{customerId}/estimates")
    public ResponseEntity<?> addEstimate(@PathVariable String customerId, @RequestBody Map<String, Object> body) {
        BudgetPlan plan = getOrCreate(customerId);

        BudgetPlan.VendorEstimate estimate = new BudgetPlan.VendorEstimate();
        estimate.setId(UUID.randomUUID().toString());
        estimate.setVendorId((String) body.getOrDefault("vendorId", ""));
        estimate.setVendorName((String) body.getOrDefault("vendorName", ""));
        estimate.setPackageName((String) body.getOrDefault("packageName", ""));
        estimate.setCategoryName((String) body.getOrDefault("categoryName", ""));
        estimate.setEstimatedAmount(((Number) body.getOrDefault("estimatedAmount", 0)).doubleValue());
        estimate.setType((String) body.getOrDefault("type", "vendor"));
        estimate.setAddedAt(LocalDateTime.now().toLocalDate().toString());

        plan.getEstimates().add(estimate);
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Estimate added", "plan", enrichedPlan(plan)));
    }

    // ─────────────────────────────────────────────────────────────
    // DELETE estimate (unlink from budget; does not delete the booking)
    // ─────────────────────────────────────────────────────────────
    @DeleteMapping("/{customerId}/estimates/{estimateId}")
    public ResponseEntity<?> deleteEstimate(@PathVariable String customerId, @PathVariable String estimateId) {
        BudgetPlan plan = getOrCreate(customerId);
        // Also nullify the estimateId link in any expenses that referenced it
        plan.getExpenses().stream()
                .filter(e -> estimateId.equals(e.getEstimateId()))
                .forEach(e -> e.setEstimateId(null));
        plan.getEstimates().removeIf(e -> estimateId.equals(e.getId()));
        plan.setUpdatedAt(LocalDateTime.now());
        budgetPlanRepository.save(plan);
        return ResponseEntity.ok(Map.of("message", "Estimate removed", "plan", enrichedPlan(plan)));
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
            double catEstimated = plan.getEstimates().stream()
                    .filter(e -> cat.getCategoryName().equals(e.getCategoryName()))
                    .mapToDouble(BudgetPlan.VendorEstimate::getEstimatedAmount)
                    .sum();
            boolean catOver = cat.getAllocatedAmount() > 0 && catSpent > cat.getAllocatedAmount();

            Map<String, Object> c = new LinkedHashMap<>();
            c.put("categoryName", cat.getCategoryName());
            c.put("percentage", cat.getPercentage());
            c.put("allocatedAmount", cat.getAllocatedAmount());
            c.put("spentAmount", catSpent);
            c.put("remainingAmount", catRemaining);
            c.put("estimatedAmount", catEstimated);
            c.put("isOverBudget", catOver);
            c.put("percentUsed", cat.getAllocatedAmount() > 0 ? Math.round((catSpent / cat.getAllocatedAmount()) * 1000.0) / 10.0 : 0);
            return c;
        }).toList();

        // Plan-level isOverBudget: total spent exceeds total budget
        boolean planOver = plan.getTotalBudget() > 0 && plan.getTotalSpent() > plan.getTotalBudget();
        result.put("isOverBudget", planOver);

        result.put("categories", enrichedCats);
        result.put("expenses", plan.getExpenses());
        result.put("estimates", plan.getEstimates());
        return result;
    }
}
