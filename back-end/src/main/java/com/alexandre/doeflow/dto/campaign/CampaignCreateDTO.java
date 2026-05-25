package com.alexandre.doeflow.dto.campaign;

import com.alexandre.doeflow.model.campaign.CampaignCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CampaignCreateDTO(
        @NotBlank
        @NotNull
        String title,
        @NotBlank
        @NotNull
        String description,
        @NotNull
        @Positive
        BigDecimal goalAmount,
        @NotNull
        CampaignCategory category,
        String imageUrl
) {
}
