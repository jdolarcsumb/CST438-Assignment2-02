package com.cst438.controller;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class StudentControllerSystemTest {

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
    public void systemTestStudent() throws Exception {
        // add a section for cst499 Spring 2024 term
        // verify section shows on the list of sections for Spring 2024
        // delete the section
        // verify the section is gone


        // click link to navigate to Sections
        WebElement we = driver.findElement(By.id("users"));
        we.click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("addUser")).click();
        Thread.sleep(SLEEP_DURATION);

        driver.findElement(By.id("uname")).sendKeys("Elon Musk");
        driver.findElement(By.id("uemail")).sendKeys("emusk@csumb.edu");
        driver.findElement(By.id("utype")).sendKeys("STUDENT");
        driver.findElement(By.id("save")).click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("message")).getText();
        assertTrue(message.startsWith("user added"));

        WebElement user = driver.findElement(By.xpath("//tr[td='Elon Musk']"));
        List<WebElement> buttons = user.findElements(By.tagName("button"));
        assertEquals(2, buttons.size());
        buttons.get(1).click();
        Thread.sleep(SLEEP_DURATION);
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        assertThrows(NoSuchElementException.class, () ->
                driver.findElement(By.xpath("//tr[td='Elon Musk']")));

    }
}
