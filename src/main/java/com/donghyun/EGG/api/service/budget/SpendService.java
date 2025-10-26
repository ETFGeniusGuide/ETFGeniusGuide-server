package com.donghyun.EGG.api.service.budget;

import com.donghyun.EGG.api.controller.budget.dto.SpendDtos;
import com.donghyun.EGG.domain.budget.Category;
import com.donghyun.EGG.domain.budget.Spend;
import com.donghyun.EGG.domain.budget.repository.BudgetPlanRepository;
import com.donghyun.EGG.domain.budget.repository.CategoryRepository;
import com.donghyun.EGG.domain.budget.repository.SpendRepository;
import com.donghyun.EGG.util.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
@Transactional
@Slf4j
public class SpendService {
    private final SpendRepository spendRepo;
    private final BudgetPlanRepository planRepo;
    private final CategoryRepository categoryRepo;

    public SpendDtos.SpendRes add(Long memberId, SpendDtos.AddReq req) {
        // 카테고리 존재 + 소유권(시스템 or 본인)
        var cat = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> BizException.notFound("카테고리가 없습니다."));
        if (cat.getMemberId() != null && !cat.getMemberId().equals(memberId)) {
            throw BizException.forbidden("본인 소유 카테고리만 사용할 수 있습니다.");
        }

        LocalDate date = req.getDate();

        var saved = spendRepo.save(Spend.builder()
                .memberId(memberId)
                .date(date)
                .categoryId(req.getCategoryId())
                .amount(req.getAmount())
                .memo(req.getMemo())
                .build());

        return SpendDtos.SpendRes.builder()
                .id(saved.getId())
                .date(req.getDate())
                .amount(saved.getAmount())
                .memo(saved.getMemo())
                .build();
    }

    public SpendDtos.SpendRes update(Long memberId, SpendDtos.UpdateReq req) {
        var spend = spendRepo.findByIdAndMemberId(req.getId(), memberId)
                .orElseThrow(() -> BizException.notFound("지출 내역이 없습니다."));

        if (req.getCategoryId() != null) {
            var cat = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> BizException.notFound("카테고리가 없습니다."));
            if (cat.getMemberId() != null && !cat.getMemberId().equals(memberId)) {
                throw BizException.forbidden("본인 소유 카테고리만 사용할 수 있습니다.");
            }
            spend.setCategoryId(req.getCategoryId());
        }
        if (req.getAmount() != null) {
            if (req.getAmount() <= 0) throw BizException.invalid("금액은 0보다 커야 합니다.");
            spend.setAmount(req.getAmount());
        }
        if (req.getMemo() != null) {
            spend.setMemo(req.getMemo());
        }

        return SpendDtos.SpendRes.builder()
                .id(spend.getId())
                .date(spend.getDate())
                .amount(spend.getAmount())
                .memo(spend.getMemo())
                .build();
    }

    public boolean delete(Long memberId, SpendDtos.DeleteReq req) {
        var spend = spendRepo.findByIdAndMemberId(req.getId(), memberId)
                .orElseThrow(() -> BizException.notFound("지출 내역이 없습니다."));
        spendRepo.delete(spend);
        return true;
    }

    public SpendDtos.loadByMonthRes loadByMonth(SpendDtos.loadByMonthReq req) {
//        Long memberId = authHolder.getCurrentMemberId(); // 또는 파라미터로 받는 구조면 그걸 사용
        Long memberId = 1L;

        YearMonth ym = req.getYm();

        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // 1) 해당 월 지출 전체 조회 (멤버 + 날짜범위), 정렬 포함
        var spends = spendRepo.findAllByMemberIdAndDateBetweenOrderByDateAscIdAsc(memberId, start, end);

        // 2) 카테고리 한번에 로딩 (N+1 방지)
        Set<Long> catIds = spends.stream()
                .map(Spend::getCategoryId)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNameById = categoryRepo.findAllById(catIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 3) DTO 매핑
        var list = spends.stream()
                .map(s -> SpendDtos.SpendRes.builder()
                        .id(s.getId())
                        .date(s.getDate())
                        .categoryName(categoryNameById.get(s.getCategoryId())) // null 가능성 OK
                        .amount(s.getAmount())
                        .memo(s.getMemo())
                        .build())
                .toList();

        return SpendDtos.loadByMonthRes.builder()
                .spendDetailResList(list)
                .build();
    }

    public SpendDtos.SpendDetailRes getSpendDetail(SpendDtos.getSpendDetailReq req) {
        Long id = req.getId();

        var spend = spendRepo.findById(id)
                .orElseThrow(() -> BizException.notFound("지출 내역이 없습니다."));

        // 카테고리명 조회 (단건이므로 N+1 걱정 없음)
        String categoryName = categoryRepo.findById(spend.getCategoryId())
                .map(Category::getName)
                .orElse(null);

        return SpendDtos.SpendDetailRes.builder()
                .id(spend.getId())
                .date(spend.getDate())
                .categoryId(spend.getCategoryId())
                .categoryName(categoryName)
                .amount(spend.getAmount())
                .memo(spend.getMemo())
                .createdAt(spend.getCreatedAt().toLocalDateTime())
                .updatedAt(spend.getUpdatedAt().toLocalDateTime())
                .build();
    }
}
