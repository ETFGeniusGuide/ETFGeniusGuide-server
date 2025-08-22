package com.donghyun.EGG.domain.budget.repository;

import com.donghyun.EGG.domain.budget.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 시스템(공용) + 내 카테고리
    List<Category> findByMemberIdIsNullOrMemberIdOrderByNameAsc(Long memberId);
    // 내 소유 카테고리 중 이름(정규화) 중복 방지용
    Optional<Category> findByMemberIdAndNormalizedName(Long memberId, String normalizedName);
}
