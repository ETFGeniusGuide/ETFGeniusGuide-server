package com.donghyun.EGG.api.controller.budget;

import com.donghyun.EGG.api.controller.budget.dto.CategoryDtos.*;
import com.donghyun.EGG.api.service.budget.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budget/categories")
public class CategoryController {
    private final CategoryService categoryService;

    Long memberId = 1l;

    @PostMapping("/list")
    public ResponseEntity<?> list(@Valid @RequestBody ListReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(
                categoryService.list(memberId, req)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryRes> create(@Valid @RequestBody CreateReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(categoryService.create(memberId, req));
    }

    @PatchMapping("/update")
    public ResponseEntity<CategoryRes> update(@Valid @RequestBody UpdateReq req) {
//        Long memberId = Auth.currentMemberId();
        return ResponseEntity.ok(categoryService.update(memberId, req));
    }
}
