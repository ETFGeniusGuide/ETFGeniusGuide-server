package com.donghyun.EGG.api.service.budget;

import com.donghyun.EGG.api.controller.budget.dto.SpendDtos;
import com.donghyun.EGG.domain.budget.SpendTx;
import com.donghyun.EGG.domain.budget.repository.BudgetPlanRepository;
import com.donghyun.EGG.domain.budget.repository.CategoryRepository;
import com.donghyun.EGG.domain.budget.repository.SpendTxRepository;
import com.donghyun.EGG.util.BizException;
import com.donghyun.EGG.util.Ym;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
@Transactional
public class SpendService {
    private final SpendTxRepository spendRepo;
    private final BudgetPlanRepository planRepo;
    private final CategoryRepository categoryRepo;

    public SpendDtos.SpendRes add(Long memberId, SpendDtos.AddReq req) {
        var ym = Ym.firstDay(req.getYearMonth());
        var date = Ym.parseDate(req.getDate());
        if (!Ym.inSameMonth(ym, date)) {
            throw BizException.invalid("date가 yearMonth 범위를 벗어났습니다.");
        }

//        // 플랜 존재 강제
//        planRepo.findByMemberIdAndYearMonth(memberId, ym)
//                .orElseThrow(() -> BizException.notFound("해당 월의 플랜이 없습니다."));

        // 카테고리 존재 + 소유권(시스템 or 본인)
        var cat = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> BizException.notFound("카테고리가 없습니다."));
        if (cat.getMemberId() != null && !cat.getMemberId().equals(memberId)) {
            throw BizException.forbidden("본인 소유 카테고리만 사용할 수 있습니다.");
        }

        var saved = spendRepo.save(SpendTx.builder()
                .memberId(memberId)
                .yearMonth(String.valueOf(ym))
                .date(date)
                .categoryId(req.getCategoryId())
                .amount(req.getAmount())
                .memo(req.getMemo())
                .build());

        return SpendDtos.SpendRes.builder()
                .id(saved.getId())
                .yearMonth(req.getYearMonth())
                .date(req.getDate())
                .categoryId(saved.getCategoryId())
                .amount(saved.getAmount())
                .memo(saved.getMemo())
                .build();
    }

    public SpendDtos.SpendRes update(Long memberId, SpendDtos.UpdateReq req) {
        var tx = spendRepo.findByIdAndMemberId(req.getId(), memberId)
                .orElseThrow(() -> BizException.notFound("지출 내역이 없습니다."));

        if (req.getCategoryId() != null) {
            var cat = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> BizException.notFound("카테고리가 없습니다."));
            if (cat.getMemberId() != null && !cat.getMemberId().equals(memberId)) {
                throw BizException.forbidden("본인 소유 카테고리만 사용할 수 있습니다.");
            }
            tx.setCategoryId(req.getCategoryId());
        }
        if (req.getAmount() != null) {
            if (req.getAmount() <= 0) throw BizException.invalid("금액은 0보다 커야 합니다.");
            tx.setAmount(req.getAmount());
        }
        if (req.getMemo() != null) {
            tx.setMemo(req.getMemo());
        }

        return SpendDtos.SpendRes.builder()
                .id(tx.getId())
                .yearMonth(tx.getYearMonth().toString().substring(0,7))
                .date(tx.getDate().toString())
                .categoryId(tx.getCategoryId())
                .amount(tx.getAmount())
                .memo(tx.getMemo())
                .build();
    }

    public boolean delete(Long memberId, SpendDtos.DeleteReq req) {
        var tx = spendRepo.findByIdAndMemberId(req.getId(), memberId)
                .orElseThrow(() -> BizException.notFound("지출 내역이 없습니다."));
        spendRepo.delete(tx);
        return true;
    }

    public SpendDtos.ListRes listByDate(String yearMonth, java.time.LocalDate date) {
        var list = spendRepo.findAllByYearMonthAndDate(yearMonth, date).stream()
                .map(s -> SpendDtos.SpendItemRes.builder()
                        .id(s.getId())
                        .yearMonth(s.getYearMonth())
                        .date(s.getDate())
                        .categoryId(s.getCategoryId())
                        .categoryName(categoryRepo.findById(s.getCategoryId())
                                .map(c -> c.getName()).orElse(null))
                        .amount(s.getAmount())
                        .memo(s.getMemo())
                        .createdAt(s.getCreatedAt().toLocalDateTime())
                        .updatedAt(s.getUpdatedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
        return new SpendDtos.ListRes(list);
    }

    public SpendDtos.ListRes listByMonth(String yearMonth) {
        var list = spendRepo.findAllByYearMonth(yearMonth).stream()
                .map(s -> SpendDtos.SpendItemRes.builder()
                        .id(s.getId())
                        .yearMonth(String.valueOf(s.getYearMonth()))
                        .date(s.getDate())
                        .categoryId(s.getCategoryId())
                        .categoryName(categoryRepo.findById(s.getCategoryId())
                                .map(c -> c.getName()).orElse(null))
                        .amount(s.getAmount())
                        .memo(s.getMemo())
                        .createdAt(s.getCreatedAt().toLocalDateTime())
                        .updatedAt(s.getUpdatedAt().toLocalDateTime())
                        .build())
                .collect(Collectors.toList());
        return new SpendDtos.ListRes(list);
    }
}
