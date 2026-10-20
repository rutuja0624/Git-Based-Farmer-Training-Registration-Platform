package com.farmer.devops.service;

import com.farmer.devops.dto.RegistrationRequest;
import com.farmer.devops.dto.StatusUpdateRequest;
import com.farmer.devops.exception.DuplicateRegistrationException;
import com.farmer.devops.exception.ResourceNotFoundException;
import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.repository.RegistrationRepository;
import com.farmer.devops.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private TrainingProgram sampleTraining;
    private Registration sampleRegistration;

    @BeforeEach
    void setUp() {
        sampleTraining = new TrainingProgram(
                1L,
                "Organic Farming Masterclass",
                "Sustainable Ag",
                "Dr. Patil",
                "Pune KVK",
                "2026-11-10",
                30,
                "Comprehensive organic methods"
        );

        sampleRegistration = new Registration(
                "REG-123456",
                "Ganesh More",
                "9876543210",
                "ganesh@example.com",
                "Baramati",
                "Pune",
                5.0,
                sampleTraining
        );
        sampleRegistration.setId(10L);
    }

    @Test
    @DisplayName("Should successfully register farmer when inputs are valid")
    void testRegisterFarmer_Success() {
        RegistrationRequest request = new RegistrationRequest(
                "Ganesh More",
                "9876543210",
                "ganesh@example.com",
                "Baramati",
                "Pune",
                5.0,
                1L
        );

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(sampleTraining));
        when(registrationRepository.existsByMobileNumberAndTrainingProgramId("9876543210", 1L)).thenReturn(false);
        when(registrationRepository.findByRegistrationCode(anyString())).thenReturn(Optional.empty());
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            Registration saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        Registration result = registrationService.registerFarmer(request);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("Ganesh More");
        assertThat(result.getMobileNumber()).isEqualTo("9876543210");
        assertThat(result.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        assertThat(result.getRegistrationCode()).startsWith("REG-");
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    @DisplayName("Should throw DuplicateRegistrationException when phone already registered for training")
    void testRegisterFarmer_DuplicateMobile_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest(
                "Ganesh More",
                "9876543210",
                "ganesh@example.com",
                "Baramati",
                "Pune",
                5.0,
                1L
        );

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(sampleTraining));
        when(registrationRepository.existsByMobileNumberAndTrainingProgramId("9876543210", 1L)).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerFarmer(request))
                .isInstanceOf(DuplicateRegistrationException.class)
                .hasMessageContaining("already exists");

        verify(registrationRepository, never()).save(any(Registration.class));
    }

    @Test
    @DisplayName("Should retrieve registration record by Registration Code")
    void testTrackRegistration_ByCode_Success() {
        when(registrationRepository.findByRegistrationCode("REG-123456")).thenReturn(Optional.of(sampleRegistration));

        List<Registration> result = registrationService.trackRegistration("REG-123456");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRegistrationCode()).isEqualTo("REG-123456");
    }

    @Test
    @DisplayName("Should retrieve registration records by Mobile Number")
    void testTrackRegistration_ByMobile_Success() {
        when(registrationRepository.findByRegistrationCode("9876543210")).thenReturn(Optional.empty());
        when(registrationRepository.findByMobileNumber("9876543210")).thenReturn(List.of(sampleRegistration));

        List<Registration> result = registrationService.trackRegistration("9876543210");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMobileNumber()).isEqualTo("9876543210");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when tracking non-existent record")
    void testTrackRegistration_NotFound_ThrowsException() {
        when(registrationRepository.findByRegistrationCode("REG-000000")).thenReturn(Optional.empty());
        when(registrationRepository.findByMobileNumber("REG-000000")).thenReturn(List.of());

        assertThatThrownBy(() -> registrationService.trackRegistration("REG-000000"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No registration record found");
    }

    @Test
    @DisplayName("Should update registration status and record remarks")
    void testUpdateStatus_Success() {
        when(registrationRepository.findById(10L)).thenReturn(Optional.of(sampleRegistration));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StatusUpdateRequest updateReq = new StatusUpdateRequest(RegistrationStatus.APPROVED, "Verified documents");
        Registration updated = registrationService.updateStatus(10L, updateReq);

        assertThat(updated.getStatus()).isEqualTo(RegistrationStatus.APPROVED);
        assertThat(updated.getRemarks()).isEqualTo("Verified documents");
        verify(registrationRepository, times(1)).save(sampleRegistration);
    }
}
