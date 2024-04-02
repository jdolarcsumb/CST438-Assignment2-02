package com.cst438.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StudentControllerSystemTest {

    private static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver-win64/chromedriver.exe";
    private static final String URL = "http://localhost:3000/";
    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get(URL);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testEnrollAndDropSection() throws InterruptedException {
        navigateToAddCourse();
        enrollInFirstAvailableSection();
        assertCourseAdded();
        navigateToScheduleAndDropCourse();
    }

    private void navigateToAddCourse() throws InterruptedException {
        driver.findElement(By.id("addCourse")).click();
        Thread.sleep(1000); // Simulating user wait time for navigation
    }

    private void enrollInFirstAvailableSection() throws InterruptedException {
        WebElement firstEnrollButton = driver.findElement(By.xpath("//button[contains(text(), 'Enroll')]"));
        firstEnrollButton.click();
        Thread.sleep(1000); // Simulating user wait time for enrollment processing
    }

    private void assertCourseAdded() {
        String message = driver.findElement(By.id("addMessage")).getText();
        assertEquals("course added", message);
    }

    private void navigateToScheduleAndDropCourse() throws InterruptedException {
        driver.findElement(By.id("schedule")).click();
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("search")).click();
        Thread.sleep(1000); // Simulating user wait time for search processing

        WebElement dropButton = driver.findElement(By.xpath("//button[contains(text(), 'Drop')]"));
        dropButton.click();
        Thread.sleep(1000); // Simulating user wait time for drop processing

        confirmDrop();
    }

    private void confirmDrop() throws InterruptedException {
        WebElement confirmButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        confirmButton.click();
        Thread.sleep(1000); // Simulating user confirmation processing
    }
}
