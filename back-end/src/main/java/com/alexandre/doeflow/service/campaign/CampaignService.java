package com.alexandre.doeflow.service.campaign;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexandre.doeflow.dto.campaign.CampaignCreateDTO;
import com.alexandre.doeflow.dto.campaign.CampaignResponseDTO;
import com.alexandre.doeflow.dto.campaign.CampaignUpdateDTO;
import com.alexandre.doeflow.infra.exceptions.campaign.CampaignAlreadyExistsException;
import com.alexandre.doeflow.infra.exceptions.campaign.CampaignNotFoundException;
import com.alexandre.doeflow.infra.exceptions.campaign.InvalidCampaignOperationException;
import com.alexandre.doeflow.infra.exceptions.user.UserNotFoundException;
import com.alexandre.doeflow.model.campaign.Campaign;
import com.alexandre.doeflow.model.campaign.CampaignCategory;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.model.user.UserRole;
import com.alexandre.doeflow.repository.CampaignRepository;
import com.alexandre.doeflow.repository.UserRepository;
import com.alexandre.doeflow.specification.CampaignSpecification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CampaignService {

    private final CampaignRepository repository;
    private final UserRepository userRepository;

    public CampaignService(CampaignRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CampaignResponseDTO create(CampaignCreateDTO dto, User authenticatedUser) {
        String title = dto.title().trim();
        validateGoal(dto.goalAmount());

        if (repository.existsByTitleIgnoreCase(title)) {
            throw new CampaignAlreadyExistsException();
        }

        User creator = getAuthenticatedUser(authenticatedUser, "Creator user not found");

        Campaign campaign = new Campaign();
        campaign.setTitle(title);
        campaign.setDescription(dto.description().trim());
        campaign.setGoalAmount(dto.goalAmount());
        campaign.setCreatedBy(creator);
        campaign.setCategory(dto.category());

        if (dto.imageUrl() != null && !dto.imageUrl().isBlank()) {
            campaign.setImageUrl(dto.imageUrl().trim());
        }

        Campaign salvo = repository.save(campaign);

        return CampaignResponseDTO.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public CampaignResponseDTO findById(Long id) {
        return CampaignResponseDTO.toResponse(getById(id));
    }


    @Transactional(readOnly = true)
    public List<CampaignResponseDTO> find(
            String title,
            CampaignStatus status,
            BigDecimal goalAmount,
            CampaignCategory category
    ) {
        var spec = CampaignSpecification.filter(title, status, goalAmount, category);

        return CampaignResponseDTO.toResponses(repository.findAll(spec));
    }

    @Transactional
    public void delete(Long id) {
        Campaign campaign = getById(id);
        repository.delete(campaign);
    }

    @Transactional
    public CampaignResponseDTO approve(Long id) {
        Campaign campaign = getById(id);
        ensurePending(campaign);
        campaign.setStatus(CampaignStatus.APPROVED);

        Campaign savedCampaign = repository.save(campaign);

        return CampaignResponseDTO.toResponse(savedCampaign);
    }

    @Transactional
    public CampaignResponseDTO refuse(Long id) {
        Campaign campaign = getById(id);
        ensurePending(campaign);
        campaign.setStatus(CampaignStatus.REFUSED);

        Campaign savedCampaign = repository.save(campaign);

        return CampaignResponseDTO.toResponse(savedCampaign);
    }

    @Transactional
    public CampaignResponseDTO update(Long id, CampaignUpdateDTO dto, User authenticatedUser) {
        Campaign campaign = getById(id);
        ensureCanManageCampaign(campaign, authenticatedUser);

        if (dto.title() != null && !dto.title().isBlank()) {
            String title = dto.title().trim();

            if (repository.existsByTitleIgnoreCaseAndIdNot(title, id)) {
                throw new CampaignAlreadyExistsException();
            }

            campaign.setTitle(title);
        }

        if (dto.description() != null && !dto.description().isBlank()) {
            campaign.setDescription(dto.description().trim());
        }

        if (dto.goalAmount() != null) {
            validateGoal(dto.goalAmount());
            validateGoalIsNotBelowCurrentAmount(dto.goalAmount(), campaign.getCurrentAmount());
            campaign.setGoalAmount(dto.goalAmount());

            if (
                    campaign.getStatus() == CampaignStatus.APPROVED
                            && campaign.getCurrentAmount().compareTo(dto.goalAmount()) == 0
            ) {
                campaign.setStatus(CampaignStatus.FINISHED);
            }
        }

        if (dto.imageUrl() != null && !dto.imageUrl().isBlank()) {
            campaign.setImageUrl(dto.imageUrl().trim());
        }

        return CampaignResponseDTO.toResponse(repository.save(campaign));
    }

    private Campaign getById(Long id) {
        return repository.findById(id)
                .orElseThrow(CampaignNotFoundException::new);
    }

    private void validateGoal(BigDecimal goalAmount) {
        if (goalAmount == null || goalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCampaignOperationException("The goal should be greater than zero");
        }
    }

    private void ensurePending(Campaign campaign) {
        if (campaign.getStatus() != CampaignStatus.PENDING) {
            throw new InvalidCampaignOperationException("Only pending campaigns can be approved or refused");
        }
    }

    private void validateGoalIsNotBelowCurrentAmount(BigDecimal goalAmount, BigDecimal currentAmount) {
        if (goalAmount.compareTo(currentAmount) < 0) {
            throw new InvalidCampaignOperationException("The goal cannot be lower than the current amount");
        }
    }

    private User getAuthenticatedUser(User authenticatedUser, String notFoundMessage) {
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }

        return userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new UserNotFoundException(notFoundMessage));
    }

    private void ensureCanManageCampaign(Campaign campaign, User authenticatedUser) {
        User currentUser = getAuthenticatedUser(authenticatedUser, "User not found");

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!campaign.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the campaign creator or an admin can update this campaign"
            );
        }
    }
}
