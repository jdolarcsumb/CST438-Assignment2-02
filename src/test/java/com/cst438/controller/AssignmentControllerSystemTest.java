package com.cst438.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentControllerSystemTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.get("http://localhost:3000");
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

    @Test
    public void testAddAndGradeAssignment() throws InterruptedException {
        navigateToAddAssignmentPage();
        addNewAssignment("Test Assignment", "2024-04-30");
    }

    private void navigateToAddAssignmentPage() {
        driver.findElement(By.id("section")).click();
    }

    private void addNewAssignment(String title, String dueDate) {
        driver.findElement(By.id("assignmentName")).sendKeys(title);
        driver.findElement(By.id("dueDate")).sendKeys(dueDate);
        driver.findElement(By.id("submit")).click();
    }
}
