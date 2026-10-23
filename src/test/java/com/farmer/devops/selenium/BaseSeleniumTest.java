package com.farmer.devops.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

/**
 * Base class for Selenium WebDriver Automated UI tests.
 * Manages embedded Tomcat lifecycle, headless browser configuration,
 * explicit waits, and test screenshot captures.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseSeleniumTest.class);

    @LocalServerPort
    protected int port;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    public static void setupWebDriverManager() {
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            log.warn("WebDriverManager setup encountered warning, relying on Selenium Manager fallback: {}", e.getMessage());
        }
    }

    @BeforeEach
    public void initDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        File standardChrome = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        if (standardChrome.exists()) {
            options.setBinary(standardChrome);
        }

        this.driver = new ChromeDriver(options);
        this.driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        this.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Error closing WebDriver instance: {}", e.getMessage());
            }
        }
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    /**
     * Captures browser viewport screenshot and stores it in test artifact directories.
     */
    protected String captureScreenshot(String testName) {
        if (driver == null) return null;
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);

            // Save to target/selenium-screenshots
            Path targetDir = Paths.get("target", "selenium-screenshots");
            Files.createDirectories(targetDir);
            Path destination = targetDir.resolve(testName + ".png");
            Files.copy(source.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Also copy to docs/screenshots for academic submission
            Path docsDir = Paths.get("docs", "screenshots");
            Files.createDirectories(docsDir);
            Path docsDest = docsDir.resolve(testName + ".png");
            Files.copy(source.toPath(), docsDest, StandardCopyOption.REPLACE_EXISTING);

            log.info("Captured screenshot saved to: {}", destination.toAbsolutePath());
            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to capture screenshot for test {}: {}", testName, e.getMessage());
            return null;
        }
    }
}
