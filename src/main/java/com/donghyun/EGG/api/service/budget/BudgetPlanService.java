package com.donghyun.EGG.api.service.budget;

import com.donghyun.EGG.api.controller.budget.dto.PlanDtos;
import com.donghyun.EGG.domain.budget.BudgetPlan;
import com.donghyun.EGG.domain.budget.repository.BudgetPlanRepository;
import com.donghyun.EGG.util.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service @RequiredArgsConstructor
@Transactional
public class BudgetPlanService {
    private final BudgetPlanRepository planRepo;

    public PlanDtos.PlanRes create(Long memberId, PlanDtos.CreateReq req) {
        var ym = req.getYm();
        if (planRepo.existsByMemberIdAndYm(memberId, ym)) {
            var plan = planRepo.findByMemberIdAndYm(memberId, ym)
                    .orElseThrow(() -> BizException.notFound("월 계획이 없습니다."));
            plan.setAmount(req.getAmount());
            // JPA dirty checking
            return PlanDtos.PlanRes.builder()
                    .planId(plan.getId())
                    .ym(req.getYm())
                    .amount(plan.getAmount())
                    .createdAt(plan.getCreatedAt().toString())
                    .build();
        }
        var plan = BudgetPlan.builder()
                .memberId(memberId)
                .ym(ym)
                .amount(req.getAmount())
                .build();
        plan = planRepo.save(plan);
        return PlanDtos.PlanRes.builder()
                .planId(plan.getId())
                .ym(req.getYm())
                .amount(plan.getAmount())
                .createdAt(plan.getCreatedAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .build();
    }

    @Transactional(readOnly = true)
    public PlanDtos.PlanRes get(Long memberId, PlanDtos.GetReq req) {
        var ym = req.getYm();
        var plan = planRepo.findByMemberIdAndYm(memberId, ym)
                .orElseThrow(() -> BizException.notFound("월 계획이 없습니다."));
        return PlanDtos.PlanRes.builder()
                .planId(plan.getId())
                .ym(req.getYm())
                .amount(plan.getAmount())
                .createdAt(plan.getCreatedAt().toString())
                .build();
    }

    public PlanDtos.PlanRes update(Long memberId, PlanDtos.UpdateReq req) {
        var ym = req.getYm();
        var plan = planRepo.findByMemberIdAndYm(memberId, ym)
                .orElseThrow(() -> BizException.notFound("월 계획이 없습니다."));
        plan.setAmount(req.getAmount());
        // JPA dirty checking
        return PlanDtos.PlanRes.builder()
                .planId(plan.getId())
                .ym(req.getYm())
                .amount(plan.getAmount())
                .createdAt(plan.getCreatedAt().toString())
                .build();
    }
}
