package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static com.cst438.controller.SectionControllerSystemTest.SLEEP_DURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignmentControllerSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver-win64/chromedriver.exe";

    //public static final String CHROME_DRIVER_FILE_LOCATION =
    //        "~/chromedriver_macOS/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.


    // add selenium dependency to pom.xml

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        // set properties required by Chrome Driver
        System.setProperty(
                "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(URL);
        // must have a short wait to allow time for the page to download
        Thread.sleep(SLEEP_DURATION);

    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void instructorAddsNewAssignmentSuccessfully() throws InterruptedException {
        WebElement addAssignmentButton = driver.findElement(By.id("addAssignment"));
        addAssignmentButton.click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("assignmentTitle")).sendKeys("New Assignment");
        driver.findElement(By.id("assignmentDescription")).sendKeys("Description of the new assignment");
        driver.findElement(By.id("dueDate")).sendKeys("2024-12-31");
        driver.findElement(By.id("submitButton")).click();
        Thread.sleep(SLEEP_DURATION);

        String successMessage = driver.findElement(By.id("successMessage")).getText();
        assertTrue(successMessage.contains("Assignment added successfully"));
    }
}
