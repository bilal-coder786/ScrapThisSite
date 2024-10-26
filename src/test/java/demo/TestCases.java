package demo;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import demo.wrappers.Wrappers;

public class TestCases {

    ChromeDriver driver;

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @Test
    public void testCase01() throws InterruptedException {
        System.out.println("Start test Case 01");
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement hockeyElement = driver.findElement(By.xpath("//a[@href='/pages/forms/']"));
        Wrappers.clickOnElement(driver, hockeyElement);
        // Scrape data
        List<Map<String, Object>> teamsData = Wrappers.scrapeHockeyTeamData(driver, 5);
        // Save data to JSON
        Wrappers.saveDataToJson(driver, teamsData, "hockey-team-data.json");
        // Verify the file
        File jsonFile = new File("src/test/resources/hockey-team-data.json");
        Assert.assertTrue(jsonFile.exists() && jsonFile.length() > 0, "File is present and not empty");
        System.out.println("End test Case 01");
    }


    @Test
    public void testCase02() {
        System.out.println("Start test Case 02");
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement hockeyElement = driver.findElement(By.xpath("//a[@href='/pages/ajax-javascript/']"));
        Wrappers.clickOnElement(driver, hockeyElement);
        List<String> years = List.of("2015", "2014", "2013", "2012", "2011", "2010");
        List<Map<String, Object>> filmsData = Wrappers.scrapeOscarData(driver, years);
        Wrappers.saveDataToJson(driver, filmsData, "oscar-winner-data.json");
        // Assert that the file is present and not empty
        File jsonFile = new File("src/test/resources/oscar-winner-data.json"); 
        Assert.assertTrue(jsonFile.exists() && jsonFile.length() > 0, "File is present and not empty");
        System.out.println("End test Case 02");
    }

    @AfterTest
    public void endTest() {
        // driver.close();
        // driver.quit();

    }
}
