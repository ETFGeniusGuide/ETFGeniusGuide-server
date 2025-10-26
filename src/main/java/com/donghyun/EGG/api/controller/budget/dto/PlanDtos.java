package com.donghyun.EGG.api.controller.budget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class PlanDtos {
    @Getter @Setter
    public static class CreateReq {
        @NotNull
        @Schema(example = "2025-10")
        private YearMonth ym;
        @NotNull @DecimalMin("0.01")
        @Schema(example = "500000")
        private Integer amount;
    }

    @Getter @Setter
    public static class GetReq {
        @NotNull
        @Schema(example = "2025-10")
        private YearMonth ym;
    }

    @Getter @Setter
    public static class UpdateReq {
        @NotNull
        @Schema(example = "2025-10")
        private YearMonth ym;
        @NotNull @DecimalMin("0.01")
        @Schema(example = "600000")
        private Integer amount;
    }

    @Getter @Builder
    public static class PlanRes {
        private final Long planId;
        private final YearMonth ym;
        private final Integer amount;
        private final String createdAt;
    }
}
