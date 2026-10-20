package com.farmer.devops.service;

import com.farmer.devops.dto.RegistrationRequest;
import com.farmer.devops.dto.StatusUpdateRequest;
import com.farmer.devops.exception.DuplicateRegistrationException;
import com.farmer.devops.exception.ResourceNotFoundException;
import com.farmer.devops.exception.ValidationException;
import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.repository.RegistrationRepository;
import com.farmer.devops.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final TrainingRepository trainingRepository;
    private final SecureRandom random = new SecureRandom();

    public RegistrationService(RegistrationRepository registrationRepository,
                               TrainingRepository trainingRepository) {
        this.registrationRepository = registrationRepository;
        this.trainingRepository = trainingRepository;
    }

    public Registration registerFarmer(RegistrationRequest request) {
        // 1. Verify training program exists
        TrainingProgram training = trainingRepository.findById(request.getTrainingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training program not found with ID: " + request.getTrainingId()));

        // 2. Business validation: Duplicate check
        String sanitizedMobile = request.getMobileNumber().trim();
        if (registrationRepository.existsByMobileNumberAndTrainingProgramId(sanitizedMobile, training.getId())) {
            throw new DuplicateRegistrationException(
                    "A registration with mobile number " + sanitizedMobile +
                    " already exists for the training '" + training.getTitle() + "'");
        }

        // 3. Generate unique registration code: REG-XXXXXX
        String registrationCode = generateUniqueRegistrationCode();

        // 4. Construct entity
        Registration registration = new Registration(
                registrationCode,
                request.getFullName().trim(),
                sanitizedMobile,
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getVillage().trim(),
                request.getDistrict().trim(),
                request.getLandSizeAcres(),
                training
        );

        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public List<Registration> trackRegistration(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new ValidationException("Search query cannot be empty. Please enter Registration ID or 10-digit Mobile Number.");
        }
        String cleanQuery = query.trim();

        // Try lookup by exact Registration Code
        var byCode = registrationRepository.findByRegistrationCode(cleanQuery.toUpperCase());
        if (byCode.isPresent()) {
            return List.of(byCode.get());
        }

        // Try lookup by Mobile Number
        var byMobile = registrationRepository.findByMobileNumber(cleanQuery);
        if (!byMobile.isEmpty()) {
            return byMobile;
        }

        throw new ResourceNotFoundException(
                "No registration record found for query: '" + cleanQuery + "'. Please check the ID or Mobile Number.");
    }

    @Transactional(readOnly = true)
    public List<Registration> getAllRegistrations(RegistrationStatus status, Long trainingId) {
        if (status != null && trainingId != null) {
            return registrationRepository.findByStatusAndTrainingProgramId(status, trainingId);
        } else if (status != null) {
            return registrationRepository.findByStatus(status);
        } else if (trainingId != null) {
            return registrationRepository.findByTrainingProgramId(trainingId);
        }
        return registrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID: " + id));
    }

    public Registration updateStatus(Long id, StatusUpdateRequest request) {
        Registration registration = getRegistrationById(id);
        registration.setStatus(request.getStatus());
        if (request.getRemarks() != null && !request.getRemarks().trim().isEmpty()) {
            registration.setRemarks(request.getRemarks().trim());
        }
        registration.setStatusUpdatedAt(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardMetrics() {
        long total = registrationRepository.count();
        long pending = registrationRepository.countByStatus(RegistrationStatus.PENDING);
        long approved = registrationRepository.countByStatus(RegistrationStatus.APPROVED);
        long rejected = registrationRepository.countByStatus(RegistrationStatus.REJECTED);
        long completed = registrationRepository.countByStatus(RegistrationStatus.COMPLETED);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("total", total);
        metrics.put("pending", pending);
        metrics.put("approved", approved);
        metrics.put("rejected", rejected);
        metrics.put("completed", completed);
        return metrics;
    }

    private String generateUniqueRegistrationCode() {
        for (int i = 0; i < 10; i++) {
            int codeNumber = 100000 + random.nextInt(900000);
            String code = "REG-" + codeNumber;
            if (registrationRepository.findByRegistrationCode(code).isEmpty()) {
                return code;
            }
        }
        return "REG-" + System.currentTimeMillis() % 1000000;
    }
}
