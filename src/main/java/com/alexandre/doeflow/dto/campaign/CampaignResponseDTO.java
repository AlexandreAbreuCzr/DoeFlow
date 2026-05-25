package com.alexandre.doeflow.dto.campaign;

import com.alexandre.doeflow.model.campaign.Campaign;
import com.alexandre.doeflow.model.campaign.CampaignCategory;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import com.alexandre.doeflow.model.donation.Donation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record CampaignResponseDTO(
        Long id,
        String title,
        String description,
        BigDecimal goalAmount,
        BigDecimal currentAmount,
        CampaignStatus status,
        UUID createdBy,
        CampaignCategory category,
        String imageUrl,
        List<Long> donations
) {
    public static CampaignResponseDTO toResponse(Campaign campaign) {
        return new CampaignResponseDTO(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getGoalAmount(),
                campaign.getCurrentAmount(),
                campaign.getStatus(),
                campaign.getCreatedBy().getId(),
                campaign.getCategory(),
                campaign.getImageUrl(),
                Optional.ofNullable(campaign.getDonations())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(Donation::getId)
                        .toList()
        );
    }

    public static List<CampaignResponseDTO> toResponses(List<Campaign> campaigns) {
        return campaigns.stream()
                .map(CampaignResponseDTO::toResponse)
                .toList();
    }
}
