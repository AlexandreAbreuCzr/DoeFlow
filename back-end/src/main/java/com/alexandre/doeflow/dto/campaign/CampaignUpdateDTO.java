package com.alexandre.doeflow.dto.campaign;

import java.math.BigDecimal;

public record CampaignUpdateDTO(
        String title,
        String description,
        BigDecimal goalAmount,
        String imageUrl
) {
}
