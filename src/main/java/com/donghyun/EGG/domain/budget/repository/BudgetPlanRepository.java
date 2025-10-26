package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.BudgetPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface BudgetPlanRepository extends JpaRepository<BudgetPlan, Long> {
    Optional<BudgetPlan> findByMemberIdAndYm(Long memberId, YearMonth yearMonth);

    boolean existsByMemberIdAndYm(Long memberId, YearMonth yearMonth);
}
