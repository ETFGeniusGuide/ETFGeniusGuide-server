package com.donghyun.EGG.api.controller.budget;

import com.donghyun.EGG.api.controller.budget.dto.SpendDtos;
import com.donghyun.EGG.api.service.budget.SpendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget/spends")
public class SpendController {
    private final SpendService spendService;

    Long memberId = 1l;
    @PostMapping("/add")
    public ResponseEntity<SpendDtos.SpendRes> add(@Valid @RequestBody SpendDtos.AddReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(spendService.add(memberId, req));
    }

    @PatchMapping("/update")
    public ResponseEntity<SpendDtos.SpendRes> update(@Valid @RequestBody SpendDtos.UpdateReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(spendService.update(memberId, req));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody SpendDtos.DeleteReq req) {
//        Long memberId = Auth.currentMemberId();
        boolean deleted = spendService.delete(memberId, req);
        return ResponseEntity.ok().body("{\"deleted\":" + deleted + "}");
    }

    @PostMapping("/loadByMonth")
    public SpendDtos.loadByMonthRes loadByMonth(@RequestBody SpendDtos.loadByMonthReq req) {
        return spendService.loadByMonth(req);
    }

    @PostMapping("/getDetail")
    public SpendDtos.SpendDetailRes getSpendDetail(@RequestBody SpendDtos.getSpendDetailReq req) {
        return spendService.getSpendDetail(req);
    }
}
