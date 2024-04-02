package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnrollmentControllerSystemTest {

    private WebDriver driver;
    private WebDriverWait wait;


    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(90));
        driver.get("http://localhost:3000");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void systemTestEnterFinalGrade() {
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.linkText("Show Sections")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("8"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("View Enrollments"))).click();

        driver.findElements(By.name("changer")).forEach(button -> {
            button.click();
            WebElement gradeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("grade")));
            gradeField.clear();
            gradeField.sendKeys("C");
            driver.findElement(By.name("saver")).click();
        });

        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("messager"))).getText();
        assertEquals("Grade saved", message);
    }
}