package com.donghyun.EGG.domain.budget;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "budget_plan",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_budget_plan_member_month", columnNames = {"member_id", "year_month"})
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BudgetPlan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // YYYY-MM-01
    @Column(name = "ym", nullable = false)
    private LocalDate yearMonth;

    @Column(nullable = false, precision = 14, scale = 2)
    private Integer amount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now; updatedAt = now;
    }

    @PreUpdate
    void preUpdate() { updatedAt = OffsetDateTime.now(); }
}
