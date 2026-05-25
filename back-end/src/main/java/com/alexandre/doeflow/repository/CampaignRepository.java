package com.alexandre.doeflow.repository;

import com.alexandre.doeflow.model.campaign.Campaign;
import com.alexandre.doeflow.model.campaign.CampaignCategory;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
    List<Campaign> findByTitleContainingIgnoreCase(String title);
    List<Campaign> findByStatus(CampaignStatus status);
    List<Campaign> findByGoalAmount(BigDecimal goalAmount);
    List<Campaign> findByCategory(CampaignCategory category);
    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);
    boolean existsByTitleIgnoreCase(String title);
}
