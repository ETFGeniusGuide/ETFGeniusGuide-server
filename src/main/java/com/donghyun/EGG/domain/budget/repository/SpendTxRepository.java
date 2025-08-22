package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.SpendTx;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SpendTxRepository extends JpaRepository<SpendTx, Long> {
    Optional<SpendTx> findByIdAndMemberId(Long id, Long memberId);

    long countByMemberIdAndYearMonth(Long memberId, LocalDate yearMonth);
}
