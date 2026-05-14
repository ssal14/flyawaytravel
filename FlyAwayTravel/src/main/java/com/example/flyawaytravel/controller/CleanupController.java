package com.example.flyawaytravel.controller;

import com.example.flyawaytravel.service.CleanupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cleanup")
public class CleanupController {

    private final CleanupService cleanupService;

    public CleanupController(CleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @DeleteMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> cleanup() {
        cleanupService.cleanAll();
        return ResponseEntity.noContent().build();
    }
}
