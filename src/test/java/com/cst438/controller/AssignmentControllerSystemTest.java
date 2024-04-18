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
        // This tests for enrollment of student
        // Do this test on INSTRUCTOR

        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Spring");
        driver.findElement(By.id("sections")).click();
        Thread.sleep(SLEEP_DURATION);

        WebElement row438 = driver.findElement(By.xpath("//tr[td='cst438']"));
        List<WebElement> lynx = row438.findElements(By.id("assign"));
        // assignments is the second link
        assertEquals(1, lynx.size());
        lynx.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("addAssign")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("title")).sendKeys("Some stuff");
        driver.findElement(By.id("dueDate")).sendKeys("2024-04-20");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("message")).getText();
        assertTrue(message.startsWith("Assignment created"));

        WebElement target = driver.findElement(By.xpath("//tr[td='Some stuff']"));
        List<WebElement> buttons = target.findElements(By.tagName("button"));
        // delete is the third button
        assertEquals(3, buttons.size());
        buttons.get(2).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        message = driver.findElement(By.id("message")).getText();
        assertTrue(message.startsWith("Assignment deleted"));

        Thread.sleep(SLEEP_DURATION);

    }
}
