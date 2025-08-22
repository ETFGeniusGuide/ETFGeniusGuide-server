package com.donghyun.EGG.api.controller.budget.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class CategoryDtos {
    @Getter @Setter
    public static class ListReq {
        private boolean includeInactive = false;
    }

    @Getter @Setter
    public static class CreateReq {
        @NotBlank @Size(max = 50)
        private String name;
    }

    @Getter @Setter
    public static class UpdateReq {
        @NotNull
        private Long id;
        @Size(max = 50)
        private String name;
        private Boolean isActive;
    }

    @Getter @Builder
    public static class CategoryRes {
        private final Long id;
        private final String name;
        private final boolean isCustom;
        private final boolean isActive;
    }
}
