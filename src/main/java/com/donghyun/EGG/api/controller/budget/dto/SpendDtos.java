package com.donghyun.EGG.api.controller.budget.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class SpendDtos {
    @Getter @Setter
    public static class AddReq {
        @NotNull
        @Schema(example = "2025-10-01")
        private LocalDate date;
        @NotNull
        private Long categoryId;
        @NotNull @DecimalMin("0.01")
        @Schema(example = "20000")
        private Integer amount;
        @Size(max = 200)
        @Schema(example = "생수")
        private String memo;
    }

    @Getter @Setter
    public static class UpdateReq {
        @NotNull
        private Long id;
        private Long categoryId;
        @DecimalMin("0.01")
        @Schema(example = "20000")
        private Integer amount;
        @Size(max = 200)
        @Schema(example = "생수")
        private String memo;
    }

    @Getter @Setter
    public static class DeleteReq {
        @NotNull
        private Long id;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class loadByMonthReq {
        @NotNull
        @Schema(example = "2025-10")
        private YearMonth ym;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class getSpendDetailReq {
        @NotNull
        @Schema(example = "2025-10-01")
        private Long id;
    }


    /***
     *
     */

    @Getter @Builder
    public static class SpendRes {
        private final Long id;
        private final LocalDate date;
        private final String categoryName;
        private final Integer amount;
        private final String memo;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class loadByMonthRes {
        private List<SpendRes> spendDetailResList;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SpendDetailRes {
        private Long id;
        private LocalDate date;       // 지출 일자
        private Long categoryId;
        private String categoryName;  // 조인 결과
        private Integer amount;
        private String memo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
