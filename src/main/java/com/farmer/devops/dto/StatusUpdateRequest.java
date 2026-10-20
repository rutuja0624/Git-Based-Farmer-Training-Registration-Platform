package com.farmer.devops.dto;

import com.farmer.devops.model.RegistrationStatus;
import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "New status is required")
    private RegistrationStatus status;

    private String remarks;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(RegistrationStatus status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
