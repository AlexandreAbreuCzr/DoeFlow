package com.alexandre.doeflow.repository;

import com.alexandre.doeflow.model.donation.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByCampaignTitleContainingIgnoreCase(String campaignTitle);

    List<Donation> findByDonorId(UUID donorId);
}
