package com.donghyun.EGG.domain.budget;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "spend_tx",
        indexes = {
                @Index(name = "ix_spend_member_month_date_desc", columnList = "member_id,year_month,date,id"),
                @Index(name = "ix_spend_member_month_category", columnList = "member_id,year_month,category_id"),
                @Index(name = "ix_spend_member_date", columnList = "member_id,date")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpendTx {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    // YYYY-MM-01
    @Column(name = "ym", nullable = false)
    private LocalDate yearMonth;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column
    private String memo;

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
