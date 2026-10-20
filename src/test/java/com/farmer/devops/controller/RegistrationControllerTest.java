package com.farmer.devops.controller;

import com.farmer.devops.dto.RegistrationRequest;
import com.farmer.devops.dto.StatusUpdateRequest;
import com.farmer.devops.model.Registration;
import com.farmer.devops.model.RegistrationStatus;
import com.farmer.devops.model.TrainingProgram;
import com.farmer.devops.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@SuppressWarnings("null")
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    @Test
    @DisplayName("POST /api/registrations should return 201 Created when valid")
    void testRegisterFarmer_ValidInput_ReturnsCreated() throws Exception {
        RegistrationRequest req = new RegistrationRequest(
                "Santosh Rao",
                "9876543210",
                "santosh@example.com",
                "Shirur",
                "Pune",
                3.5,
                1L
        );

        TrainingProgram training = new TrainingProgram(1L, "Organic Farming", "Ag", "Dr. Patil", "Pune", "2026-11-15", 35, "Desc");
        Registration reg = new Registration("REG-555123", req.getFullName(), req.getMobileNumber(), req.getEmail(), req.getVillage(), req.getDistrict(), req.getLandSizeAcres(), training);

        when(registrationService.registerFarmer(any(RegistrationRequest.class))).thenReturn(reg);

        mockMvc.perform(post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.registrationCode").value("REG-555123"))
                .andExpect(jsonPath("$.data.fullName").value("Santosh Rao"))
                .andExpect(jsonPath("$.data.farmerCategory").value("SEMI_MEDIUM"));
    }

    @Test
    @DisplayName("POST /api/registrations should return 400 Bad Request when mobile number is invalid")
    void testRegisterFarmer_InvalidMobile_ReturnsBadRequest() throws Exception {
        RegistrationRequest req = new RegistrationRequest(
                "Santosh Rao",
                "12345", // invalid: not 10 digits
                "santosh@example.com",
                "Shirur",
                "Pune",
                3.5,
                1L
        );

        mockMvc.perform(post("/api/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Mobile number must be exactly 10 numeric digits")));
    }

    @Test
    @DisplayName("GET /api/registrations/track should return record when found")
    void testTrackRegistration_Success() throws Exception {
        TrainingProgram training = new TrainingProgram(1L, "Organic Farming", "Ag", "Dr. Patil", "Pune", "2026-11-15", 35, "Desc");
        Registration reg = new Registration("REG-555123", "Santosh Rao", "9876543210", "santosh@example.com", "Shirur", "Pune", 3.5, training);

        when(registrationService.trackRegistration("REG-555123")).thenReturn(List.of(reg));

        mockMvc.perform(get("/api/registrations/track")
                        .param("query", "REG-555123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].registrationCode").value("REG-555123"));
    }

    @Test
    @DisplayName("PATCH /api/registrations/{id}/status should update status")
    void testUpdateStatus_Success() throws Exception {
        TrainingProgram training = new TrainingProgram(1L, "Organic Farming", "Ag", "Dr. Patil", "Pune", "2026-11-15", 35, "Desc");
        Registration reg = new Registration("REG-555123", "Santosh Rao", "9876543210", "santosh@example.com", "Shirur", "Pune", 3.5, training);
        reg.setStatus(RegistrationStatus.APPROVED);

        StatusUpdateRequest updateReq = new StatusUpdateRequest(RegistrationStatus.APPROVED, "Verified");

        when(registrationService.updateStatus(eq(1L), any(StatusUpdateRequest.class))).thenReturn(reg);

        mockMvc.perform(patch("/api/registrations/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
