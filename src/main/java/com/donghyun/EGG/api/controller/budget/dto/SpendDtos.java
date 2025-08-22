package com.donghyun.EGG.api.controller.budget.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

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
        private BigDecimal amount;
        @Size(max = 200)
        private String memo;
    }

    @Getter @Setter
    public static class UpdateReq {
        @NotNull
        private Long id;
        private Long categoryId;
        @DecimalMin("0.01")
        private BigDecimal amount;
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
        private final BigDecimal amount;
        private final String memo;
    }
}
