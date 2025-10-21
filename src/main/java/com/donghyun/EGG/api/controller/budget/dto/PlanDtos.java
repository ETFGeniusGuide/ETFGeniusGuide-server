package com.donghyun.EGG.api.controller.budget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.YearMonth;

public class PlanDtos {
    @Getter @Setter
    public static class CreateReq {
        @Pattern(regexp = "\\d{4}-\\d{2}")
        @NotNull
        private String yearMonth;
        @NotNull @DecimalMin("0.01")
        private Integer amount;
    }

    @Getter @Setter
    public static class GetReq {
        @NotNull
        @JsonFormat(pattern = "yyyy-MM")
        private YearMonth yearMonth;
    }

    @Getter @Setter
    public static class UpdateReq {
        @Pattern(regexp = "\\d{4}-\\d{2}") @NotNull
        private String yearMonth;
        @NotNull @DecimalMin("0.01")
        private Integer amount;
    }

    @Getter @Builder
    public static class PlanRes {
        private final Long planId;
        private final String yearMonth;
        private final Integer amount;
        private final String createdAt;
    }
}
