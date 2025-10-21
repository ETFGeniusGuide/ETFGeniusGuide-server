package com.donghyun.EGG.api.controller.budget.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SpendDtos {
    @Getter @Setter
    public static class AddReq {
        @Pattern(regexp = "\\d{4}-\\d{2}") @NotNull
        private String yearMonth;
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}") @NotNull
        private String date;
        @NotNull
        private Long categoryId;
        @NotNull @DecimalMin("0.01")
        private Integer amount;
        @Size(max = 200)
        private String memo;
    }

    @Getter @Setter
    public static class UpdateReq {
        @NotNull
        private Long id;
        private Long categoryId;
        @DecimalMin("0.01")
        private Integer amount;
        @Size(max = 200)
        private String memo;
    }

    @Getter @Setter
    public static class DeleteReq {
        @NotNull
        private Long id;
    }

    @Getter @Builder
    public static class SpendRes {
        private final Long id;
        private final String yearMonth;
        private final String date;
        private final Long categoryId;
        private final Integer amount;
        private final String memo;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ListByDateReq {
        private String yearMonth;   // "YYYY-MM"
        private LocalDate date;     // ex) 2025-11-28
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SpendItemRes {
        private Long id;
        private String yearMonth;     // "YYYY-MM"
        private LocalDate date;       // 지출 일자
        private Long categoryId;
        private String categoryName;  // 조인 결과
        private Integer amount;
        private String memo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ListByMonthReq {
        private String yearMonth; // "YYYY-MM"
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ListRes {
        private java.util.List<SpendItemRes> items;
    }
}
