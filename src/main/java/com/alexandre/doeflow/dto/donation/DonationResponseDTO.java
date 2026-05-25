package com.alexandre.doeflow.dto.donation;

import com.alexandre.doeflow.model.donation.Donation;
import com.alexandre.doeflow.model.donation.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DonationResponseDTO(
        Long id,
        BigDecimal amount,
        UUID donor,
        Long campaign,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt
) {
    public static DonationResponseDTO toResponse(Donation donation) {
        return new DonationResponseDTO(
                donation.getId(),
                donation.getAmount(),
                donation.getDonor().getId(),
                donation.getCampaign().getId(),
                donation.getPaymentStatus(),
                donation.getCreatedAt()
        );
    }

    public static List<DonationResponseDTO> toResponses(List<Donation> donations) {
        return donations.stream()
                .map(DonationResponseDTO::toResponse)
                .toList();
    }
}