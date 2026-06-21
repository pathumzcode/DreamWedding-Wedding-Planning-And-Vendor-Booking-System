package com.wedding.dreamwedding.controller;

import com.wedding.dreamwedding.entity.SiteSettings;
import com.wedding.dreamwedding.repository.SiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/site-settings")
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingsRepository siteSettingsRepository;

    // READ Operation: Retrieve the global site settings
    @GetMapping
    public ResponseEntity<?> getSettings() {
        return siteSettingsRepository.findById("global")
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.ok(new SiteSettings()));
    }

    // UPDATE Operation: Modify the global site settings
    @PutMapping
    public ResponseEntity<?> updateSettings(@RequestBody SiteSettings settings) {
        settings.setId("global");
        siteSettingsRepository.save(settings);
        return ResponseEntity.ok(Map.of("message", "Site settings updated"));
    }
}
