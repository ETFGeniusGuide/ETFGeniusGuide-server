package com.donghyun.EGG.domain.budget;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "category",
        indexes = {
                @Index(name = "ix_category_member_active", columnList = "member_id,is_active")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NULL이면 시스템(공용) 카테고리
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false)
    private String name;

    // DB에서 generated always 칼럼이라면 읽기전용 매핑
    @Column(name = "normalized_name", insertable = false, updatable = false)
    private String normalizedName;

    @Column(name = "is_custom", nullable = false)
    private boolean custom;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

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
