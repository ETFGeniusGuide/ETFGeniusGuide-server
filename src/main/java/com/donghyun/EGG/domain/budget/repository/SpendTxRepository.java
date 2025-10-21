package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.SpendTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpendTxRepository extends JpaRepository<SpendTx, Long> {
    Optional<SpendTx> findByIdAndMemberId(Long id, Long memberId);

    long countByMemberIdAndYearMonth(Long memberId, String yearMonth);

    @Query("""
        select s from SpendTx s
        where s.yearMonth = :ym and s.date = :date
        order by s.createdAt asc, s.id asc
    """)
    List<SpendTx> findAllByYearMonthAndDate(@Param("ym") String yearMonth,
                                            @Param("date") LocalDate date);

    @Query("""
        select s from SpendTx s
        where s.yearMonth = :ym
        order by s.date asc, s.createdAt asc, s.id asc
    """)
    List<SpendTx> findAllByYearMonth(@Param("ym") String yearMonth);

}
