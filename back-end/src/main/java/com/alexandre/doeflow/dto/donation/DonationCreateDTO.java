package com.alexandre.doeflow.dto.donation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DonationCreateDTO(
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        @Positive
        Long campaign
) {
}
