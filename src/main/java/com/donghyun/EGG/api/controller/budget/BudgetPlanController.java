package com.donghyun.EGG.api.controller.budget;

import com.donghyun.EGG.api.controller.budget.dto.PlanDtos;
import com.donghyun.EGG.api.service.budget.BudgetPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget/plans")
public class BudgetPlanController {
    private final BudgetPlanService planService;
    Long memberId = 1l;

    @PostMapping("/create")
    public ResponseEntity<PlanDtos.PlanRes> create(@Valid @RequestBody PlanDtos.CreateReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(planService.create(memberId, req));
    }

    @PostMapping("/get")
    public ResponseEntity<PlanDtos.PlanRes> get(@Valid @RequestBody PlanDtos.GetReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(planService.get(memberId, req));
    }

    @PatchMapping("/update")
    public ResponseEntity<PlanDtos.PlanRes> update(@Valid @RequestBody PlanDtos.UpdateReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(planService.update(memberId, req));
    }
}
