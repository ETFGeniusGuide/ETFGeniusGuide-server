package com.donghyun.EGG.api.service.budget;

import com.donghyun.EGG.api.controller.budget.dto.CategoryDtos;
import com.donghyun.EGG.domain.budget.Category;
import com.donghyun.EGG.domain.budget.repository.CategoryRepository;
import com.donghyun.EGG.util.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepo;

    @Transactional(readOnly = true)
    public List<CategoryDtos.CategoryRes> list(Long memberId, CategoryDtos.ListReq req) {
        var all = categoryRepo.findByMemberIdIsNullOrMemberIdOrderByNameAsc(memberId);
        return all.stream()
                .filter(c -> req.isIncludeInactive() || c.isActive())
                .map(c -> CategoryDtos.CategoryRes.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .isCustom(c.getMemberId() != null)
                        .isActive(c.isActive())
                        .build()
                ).toList();
    }

    public CategoryDtos.CategoryRes create(Long memberId, CategoryDtos.CreateReq req) {
        // 동일 사용자 내 중복명 방지: normalized_name 비교 (DB generated 칼럼 기준, 서비스 레벨에선 trim/lower)
        String normalized = req.getName().trim().toLowerCase();
        categoryRepo.findByMemberIdAndNormalizedName(memberId, normalized)
                .ifPresent(c -> { throw BizException.conflict("이미 존재하는 카테고리명입니다."); });

        var saved = categoryRepo.save(Category.builder()
                .memberId(memberId)
                .name(req.getName().trim())
                .custom(true)
                .active(true)
                .build());
        return CategoryDtos.CategoryRes.builder()
                .id(saved.getId())
                .name(saved.getName())
                .isCustom(true)
                .isActive(true)
                .build();
    }

    public CategoryDtos.CategoryRes update(Long memberId, CategoryDtos.UpdateReq req) {
        var cat = categoryRepo.findById(req.getId())
                .orElseThrow(() -> BizException.notFound("카테고리가 없습니다."));
        // 시스템 카테고리(NULL)는 이름 변경/비활성화 정책을 정해야 함. 여기서는 수정 허용 X
        if (cat.getMemberId() == null) {
            throw BizException.forbidden("시스템 카테고리는 수정할 수 없습니다.");
        }
        if (!cat.getMemberId().equals(memberId)) {
            throw BizException.forbidden("본인 소유의 카테고리만 수정할 수 있습니다.");
        }
        if (req.getName() != null && !req.getName().isBlank()) {
            String normalized = req.getName().trim().toLowerCase();
            categoryRepo.findByMemberIdAndNormalizedName(memberId, normalized)
                    .filter(other -> !other.getId().equals(cat.getId()))
                    .ifPresent(other -> { throw BizException.conflict("이미 존재하는 카테고리명입니다."); });
            cat.setName(req.getName().trim());
        }
        if (req.getIsActive() != null) {
            cat.setActive(req.getIsActive());
        }
        return CategoryDtos.CategoryRes.builder()
                .id(cat.getId())
                .name(cat.getName())
                .isCustom(true)
                .isActive(cat.isActive())
                .build();
    }
}
