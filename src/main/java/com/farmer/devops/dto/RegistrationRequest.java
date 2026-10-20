package com.farmer.devops.dto;

import jakarta.validation.constraints.*;

public class RegistrationRequest {

    @NotBlank(message = "Farmer full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 numeric digits")
    private String mobileNumber;

    @Email(message = "Email must be a valid format if provided")
    private String email;

    @NotBlank(message = "Village name is required")
    private String village;

    @NotBlank(message = "District name is required")
    private String district;

    @NotNull(message = "Land size is required")
    @Positive(message = "Land size must be a positive number of acres")
    private Double landSizeAcres;

    @NotNull(message = "Training program ID is required")
    private Long trainingId;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String fullName, String mobileNumber, String email, String village,
                               String district, Double landSizeAcres, Long trainingId) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.village = village;
        this.district = district;
        this.landSizeAcres = landSizeAcres;
        this.trainingId = trainingId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Double getLandSizeAcres() {
        return landSizeAcres;
    }

    public void setLandSizeAcres(Double landSizeAcres) {
        this.landSizeAcres = landSizeAcres;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }
}
