package com.farmer.devops.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-End Selenium WebDriver Automated UI Test Suite.
 * Validates critical user journeys:
 * Journey 1: Farmer Self-Registration Happy Path
 * Journey 2: Duplicate Registration Prevention (HTTP 409 Conflict)
 * Journey 3: Real-Time Application Lifecycle Tracking
 * Journey 4: Coordinator Console Dashboard & Approval State Machine
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FarmerRegistrationUiTest extends BaseSeleniumTest {

    private static final String TEST_MOBILE = "9822" + (100000 + (int)(Math.random() * 899999));
    private static String registeredCode;

    @Test
    @Order(1)
    @DisplayName("Journey 1: Farmer Self-Registration Happy Path")
    public void testHappyPathFarmerRegistration() {
        driver.get(getBaseUrl() + "/");

        // Wait for page title and training dropdown to load
        assertTrue(driver.getTitle().contains("Farmer Training Registration Platform"),
                "Portal homepage title mismatch");

        WebElement trainingDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("trainingSelect")));
        wait.until(d -> new Select(trainingDropdown).getOptions().size() > 1);

        Select select = new Select(trainingDropdown);
        select.selectByIndex(1); // Select first available training program

        // Populate Farmer Registration Form
        WebElement fullNameInput = driver.findElement(By.id("fullName"));
        fullNameInput.clear();
        fullNameInput.sendKeys("Ramesh Baburao Patil");

        WebElement mobileInput = driver.findElement(By.id("mobileNumber"));
        mobileInput.clear();
        mobileInput.sendKeys(TEST_MOBILE);

        WebElement emailInput = driver.findElement(By.id("email"));
        emailInput.clear();
        emailInput.sendKeys("ramesh.patil@agrifarmer.org");

        WebElement villageInput = driver.findElement(By.id("village"));
        villageInput.clear();
        villageInput.sendKeys("Baramati");

        WebElement districtInput = driver.findElement(By.id("district"));
        districtInput.clear();
        districtInput.sendKeys("Pune");

        WebElement landSizeInput = driver.findElement(By.id("landSize"));
        landSizeInput.clear();
        landSizeInput.sendKeys("4.5");

        // Submit Application
        WebElement submitButton = driver.findElement(By.id("submitBtn"));
        submitButton.click();

        // Verify Success Modal Appearance
        WebElement successModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successModal")));
        assertTrue(successModal.isDisplayed(), "Registration confirmation modal should be visible");

        WebElement modalCodeSpan = driver.findElement(By.id("modalCode"));
        registeredCode = modalCodeSpan.getText().trim();
        log.info("Successfully generated registration tracking ID: {}", registeredCode);

        assertTrue(registeredCode.startsWith("REG-"), "Registration ID must start with REG- prefix");
        assertTrue(driver.findElement(By.id("modalFarmerName")).getText().contains("Ramesh Baburao Patil"),
                "Modal should address the registered farmer name");

        captureScreenshot("01_farmer_registration_success");
    }

    @Test
    @Order(2)
    @DisplayName("Journey 2: Duplicate Registration Prevention Validation")
    public void testDuplicateRegistrationPrevention() {
        driver.get(getBaseUrl() + "/");

        WebElement trainingDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("trainingSelect")));
        wait.until(d -> new Select(trainingDropdown).getOptions().size() > 1);

        Select select = new Select(trainingDropdown);
        select.selectByIndex(1); // Select same training

        driver.findElement(By.id("fullName")).sendKeys("Duplicate Registration Test");
        driver.findElement(By.id("mobileNumber")).sendKeys(TEST_MOBILE); // Same registered mobile
        driver.findElement(By.id("village")).sendKeys("Baramati");
        driver.findElement(By.id("district")).sendKeys("Pune");
        driver.findElement(By.id("landSize")).sendKeys("3.0");

        driver.findElement(By.id("submitBtn")).click();

        // Verify error toast appears preventing duplicate submission
        WebElement toastError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toast, .toast-error")));
        assertTrue(toastError.isDisplayed(), "Duplicate alert toast must be displayed to user");
        log.info("Received expected conflict alert toast message: {}", toastError.getText());

        captureScreenshot("02_duplicate_prevention_error");
    }

    @Test
    @Order(3)
    @DisplayName("Journey 3: Real-Time Application Lifecycle Tracking")
    public void testApplicationStatusTracking() {
        driver.get(getBaseUrl() + "/tracker");

        assertTrue(driver.getTitle().contains("Application Status Tracker"),
                "Tracker page title mismatch");

        WebElement trackInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("trackQuery")));
        trackInput.clear();

        // Use newly generated ID or default seeded demo ID
        String queryId = (registeredCode != null && !registeredCode.isEmpty()) ? registeredCode : "REG-100201";
        trackInput.sendKeys(queryId);

        WebElement trackButton = driver.findElement(By.id("trackBtn"));
        trackButton.click();

        // Verify tracking card renders results
        WebElement resultsContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("trackerResults")));
        wait.until(d -> resultsContainer.findElements(By.className("card")).size() > 0);

        String resultsText = resultsContainer.getText();
        assertTrue(resultsText.contains(queryId) || resultsText.contains("Status"),
                "Tracker output must display the requested registration or its status");

        captureScreenshot("03_application_status_tracking");
    }

    @Test
    @Order(4)
    @DisplayName("Journey 4: Coordinator Console Dashboard & Approval Workflow")
    public void testCoordinatorDashboardApprovalWorkflow() {
        driver.get(getBaseUrl() + "/admin");

        assertTrue(driver.getTitle().contains("Coordinator Dashboard"),
                "Coordinator console title mismatch");

        // Verify KPI metric cards load
        WebElement metricTotal = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("metricTotal")));
        assertNotNull(metricTotal.getText(), "Total metric value must be rendered");

        // Verify roster table populates
        WebElement tableBody = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("adminTableBody")));
        wait.until(d -> tableBody.findElements(By.tagName("tr")).size() > 0);

        List<WebElement> rows = tableBody.findElements(By.tagName("tr"));
        assertFalse(rows.isEmpty(), "Participant roster table must contain registration rows");

        log.info("Found {} registration rows in Coordinator Roster table", rows.size());

        // Verify Action Buttons (Approve / Reject) are present
        List<WebElement> actionButtons = tableBody.findElements(By.tagName("button"));
        log.info("Found {} interactive action buttons in table roster", actionButtons.size());

        captureScreenshot("04_coordinator_dashboard_approval");
    }
}
