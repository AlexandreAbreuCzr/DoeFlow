package com.alexandre.doeflow.controller;

import com.alexandre.doeflow.dto.donation.DonationCreateDTO;
import com.alexandre.doeflow.dto.donation.DonationResponseDTO;
import com.alexandre.doeflow.model.user.User;
import com.alexandre.doeflow.service.donation.DonationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/donation")
@RestController
public class DonationController {

    private final DonationService service;

    public DonationController(DonationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DonationResponseDTO> create(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid DonationCreateDTO dto
    ) {
        DonationResponseDTO donation = service.create(dto, user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(donation);
    }

    @GetMapping("/my")
    public ResponseEntity<List<DonationResponseDTO>> findMine(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.findByDonor(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DonationResponseDTO>> find(
            @RequestParam(required = false) String campaignTitle
    ) {
        return ResponseEntity.ok(service.find(campaignTitle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
