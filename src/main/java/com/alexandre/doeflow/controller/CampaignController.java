package com.alexandre.doeflow.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alexandre.doeflow.dto.campaign.CampaignCreateDTO;
import com.alexandre.doeflow.dto.campaign.CampaignResponseDTO;
import com.alexandre.doeflow.dto.campaign.CampaignUpdateDTO;
import com.alexandre.doeflow.model.campaign.CampaignCategory;
import com.alexandre.doeflow.model.campaign.CampaignStatus;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.service.campaign.CampaignService;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/campaign")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }


    @PostMapping
    public ResponseEntity<CampaignResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CampaignCreateDTO dto
    ){
        return ResponseEntity.status(201).body(campaignService.create(dto, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(campaignService.findById(id));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<CampaignResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.approve(id));
    }

    @PatchMapping("/{id}/refuse")
    public ResponseEntity<CampaignResponseDTO> refuse(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.refuse(id));
    }


    @GetMapping
    public ResponseEntity<List<CampaignResponseDTO>> find(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) CampaignStatus status,
            @RequestParam(required = false) BigDecimal goalAmount,
            @RequestParam(required = false) CampaignCategory category
    ) {
        return ResponseEntity.ok(
                campaignService.find(title, status, goalAmount, category)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CampaignResponseDTO> update(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CampaignUpdateDTO dto
    ) {
        return ResponseEntity.ok(campaignService.update(id, dto, user));
    }
}
