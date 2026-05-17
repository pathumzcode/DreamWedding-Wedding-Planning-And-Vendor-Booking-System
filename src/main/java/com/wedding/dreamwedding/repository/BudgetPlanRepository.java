package com.wedding.dreamwedding.repository;

import com.wedding.dreamwedding.entity.BudgetPlan;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for BudgetPlan.
 * Each customer has exactly one BudgetPlan document.
 */
public interface BudgetPlanRepository extends MongoRepository<BudgetPlan, String> {
    Optional<BudgetPlan> findByCustomerId(String customerId);
}
