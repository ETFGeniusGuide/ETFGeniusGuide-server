package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.BudgetPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BudgetPlanRepository extends JpaRepository<BudgetPlan, Long> {
    Optional<BudgetPlan> findByMemberIdAndYearMonth(Long memberId, LocalDate yearMonth);

    boolean existsByMemberIdAndYearMonth(Long memberId, LocalDate yearMonth);
}
