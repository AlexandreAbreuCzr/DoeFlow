package com.alexandre.doeflow.service.donation;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexandre.doeflow.dto.donation.DonationCreateDTO;
import com.alexandre.doeflow.dto.donation.DonationResponseDTO;
import com.alexandre.doeflow.infra.exceptions.campaign.CampaignNotFoundException;
import com.alexandre.doeflow.infra.exceptions.campaign.InvalidCampaignOperationException;
import com.alexandre.doeflow.infra.exceptions.donation.DonationNotFoundException;
import com.alexandre.doeflow.infra.exceptions.donation.InvalidDonationException;
import com.alexandre.doeflow.infra.exceptions.user.UserNotFoundException;
import com.alexandre.doeflow.model.campaign.Campaign;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import com.alexandre.doeflow.model.donation.Donation;
import com.alexandre.doeflow.model.donation.PaymentStatus;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.repository.CampaignRepository;
import com.alexandre.doeflow.repository.DonationRepository;
import com.alexandre.doeflow.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DonationService {

    private final DonationRepository repository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;

    public DonationService(
            DonationRepository repository,
            UserRepository userRepository,
            CampaignRepository campaignRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
    }

    @Transactional
    public DonationResponseDTO create(DonationCreateDTO dto, User authenticatedUser) {
        validateDonation(dto);

        User donor = getAuthenticatedUser(authenticatedUser, "Donor user not found");

        Campaign campaign = campaignRepository.findById(dto.campaign())
                .orElseThrow(CampaignNotFoundException::new);

        if (campaign.getStatus() != CampaignStatus.APPROVED) {
            throw new InvalidCampaignOperationException("Campaign is not approved for donations");
        }

        BigDecimal newCurrentAmount = campaign.getCurrentAmount().add(dto.amount());

        if (newCurrentAmount.compareTo(campaign.getGoalAmount()) > 0) {
            throw new InvalidDonationException("Donation exceeds campaign remaining amount");
        }

        Donation donation = new Donation();
        donation.setAmount(dto.amount());
        donation.setDonor(donor);
        donation.setCampaign(campaign);
        donation.setPaymentStatus(PaymentStatus.PAID);


        campaign.setCurrentAmount(newCurrentAmount);

        if (newCurrentAmount.compareTo(campaign.getGoalAmount()) == 0) {
            campaign.setStatus(CampaignStatus.FINISHED);
        }

        campaignRepository.save(campaign);

        Donation salvo = repository.save(donation);
        return DonationResponseDTO.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public DonationResponseDTO findById(Long id) {
        return DonationResponseDTO.toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public List<DonationResponseDTO> findByDonor(User donor) {
        return DonationResponseDTO.toResponses(repository.findByDonorId(donor.getId()));
    }

    @Transactional(readOnly = true)
    public List<DonationResponseDTO> find(String campaignTitle) {
        if (campaignTitle == null || campaignTitle.isBlank()) {
            return DonationResponseDTO.toResponses(repository.findAll());
        }

        return DonationResponseDTO.toResponses(
                repository.findByCampaignTitleContainingIgnoreCase(campaignTitle.trim())
        );
    }

    @Transactional
    public void delete(Long id) {
        Donation donation = getById(id);
        Campaign campaign = donation.getCampaign();
        BigDecimal updatedAmount = campaign.getCurrentAmount().subtract(donation.getAmount());

        if (updatedAmount.compareTo(BigDecimal.ZERO) < 0) {
            updatedAmount = BigDecimal.ZERO;
        }

        campaign.setCurrentAmount(updatedAmount);

        if (
                campaign.getStatus() == CampaignStatus.FINISHED
                        && updatedAmount.compareTo(campaign.getGoalAmount()) < 0
        ) {
            campaign.setStatus(CampaignStatus.APPROVED);
        }

        repository.delete(donation);
        campaignRepository.save(campaign);
    }

    private Donation getById(Long id) {
        return repository.findById(id)
                .orElseThrow(DonationNotFoundException::new);
    }

    private void validateDonation(DonationCreateDTO dto) {
        if (dto == null || dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDonationException("The donation amount should be greater than zero");
        }
    }

    private User getAuthenticatedUser(User authenticatedUser, String notFoundMessage) {
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }

        return userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException(notFoundMessage));
    }
}
