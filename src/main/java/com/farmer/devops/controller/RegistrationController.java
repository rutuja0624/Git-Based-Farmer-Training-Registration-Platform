package com.farmer.devops.controller;

import com.farmer.devops.dto.ApiResponse;
import com.farmer.devops.dto.RegistrationRequest;
import com.farmer.devops.dto.StatusUpdateRequest;
import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import com.farmer.devops.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Registration>> registerFarmer(
            @Valid @RequestBody RegistrationRequest request) {
        Registration created = registrationService.registerFarmer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration submitted successfully! Your tracking ID is " + created.getRegistrationCode(), created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Registration>>> getAllRegistrations(
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(required = false) Long trainingId) {
        List<Registration> list = registrationService.getAllRegistrations(status, trainingId);
        return ResponseEntity.ok(ApiResponse.ok("Registrations retrieved successfully", list));
    }

    @GetMapping("/track")
    public ResponseEntity<ApiResponse<List<Registration>>> trackRegistration(
            @RequestParam String query) {
        List<Registration> results = registrationService.trackRegistration(query);
        return ResponseEntity.ok(ApiResponse.ok("Registration record found", results));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Registration>> getRegistrationById(@PathVariable Long id) {
        Registration reg = registrationService.getRegistrationById(id);
        return ResponseEntity.ok(ApiResponse.ok(reg));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Registration>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        Registration updated = registrationService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Registration status updated to " + updated.getStatus(), updated));
    }

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics() {
        Map<String, Object> metrics = registrationService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.ok("Metrics retrieved successfully", metrics));
    }
}
