package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.Spend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpendRepository extends JpaRepository<Spend, Long> {
    Optional<Spend> findByIdAndMemberId(Long id, Long memberId);

    Optional<Spend> findById(Long id);

    List<Spend> findAllByMemberIdAndDateBetweenOrderByDateAscIdAsc(Long memberId, LocalDate start, LocalDate end);
}
