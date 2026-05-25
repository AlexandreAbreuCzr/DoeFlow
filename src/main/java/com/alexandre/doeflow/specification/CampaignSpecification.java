package com.alexandre.doeflow.specification;

import com.alexandre.doeflow.model.campaign.Campaign;
import com.alexandre.doeflow.model.campaign.CampaignCategory;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CampaignSpecification {

    public static Specification<Campaign> filter(
            String title,
            CampaignStatus status,
            BigDecimal goalAmount,
            CampaignCategory category
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (title != null && !title.isBlank()) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                "%" + title.toLowerCase() + "%"
                        )
                );
            }

            if (status != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get("status"), status)
                );
            }

            if (goalAmount != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get("goalAmount"), goalAmount)
                );
            }

            if (category != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(root.get("category"), category)
                );
            }

            return predicates;
        };
    }
}
