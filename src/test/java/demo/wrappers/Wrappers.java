package demo.wrappers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Wrappers {
    // Function to click on an element using JavaScript Executor
    public static void clickOnElement(ChromeDriver driver, WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            wait.until(ExpectedConditions.visibilityOf(element));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
            System.out.println("Element clicked successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while clicking element: " + e.getMessage());
        }
    }

    // Method to scrape hockey team data
    public static List<Map<String, Object>> scrapeHockeyTeamData(ChromeDriver driver, int pagesNumbersToScrape) {
        List<Map<String, Object>> teamsData = new ArrayList<>();

        for (int i = 0; i < pagesNumbersToScrape; i++) {
            // Wait for the table rows to be visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table/tbody/tr[@class='team']")));

            for (WebElement row : rows) {
                String winPctText = row.findElement(By.xpath(".//td[contains(@class,'pct')]")).getText().trim();
                double winPct = Double.parseDouble(winPctText);

                if (winPct < 0.40) {
                    Map<String, Object> teamData = new HashMap<>();
                    teamData.put("EpochTime", Instant.now().getEpochSecond());
                    teamData.put("TeamName", row.findElement(By.cssSelector(".name")).getText().trim());
                    teamData.put("Year", row.findElement(By.cssSelector(".year")).getText().trim());
                    teamData.put("WinPct", winPct);
                    teamsData.add(teamData);
                }
            }

            // Wait for the next page button and click
            if (i < pagesNumbersToScrape - 1) {
                WebElement nextPageButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@aria-label='Next']")));
                if (nextPageButton.isEnabled()) {
                    Wrappers.clickOnElement(driver, nextPageButton);
                } else {
                    break;
                }
            }
        }
        return teamsData;
    }

    public static List<Map<String, Object>> scrapeOscarData(ChromeDriver driver, List<String> years) {
        List<Map<String, Object>> filmsData = new ArrayList<>();
    
        for (String year : years) {
            // Find and click on the year link with WebDriver wait
            WebElement yearLink = driver.findElement(By.xpath("//a[contains(text(),'" + year + "')]"));
            clickOnElement(driver, yearLink);
    
            // Wait for the table to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody")));
    
            // Get the rows for films with WebDriver wait for visibility
            List<WebElement> rows = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table/tbody/tr[@class='film']")));
    
            for (int i = 0; i < Math.min(5, rows.size()); i++) {
                WebElement row = rows.get(i);
                Map<String, Object> filmData = new HashMap<>();
    
                filmData.put("EpochTime", Instant.now().toEpochMilli()); // Use milliseconds
                filmData.put("Year", year);
                filmData.put("Title", row.findElement(By.xpath(".//td[@class='film-title']")).getText().trim());
                filmData.put("Nominations", row.findElement(By.xpath(".//td[@class='film-nominations']")).getText().trim());
                filmData.put("Awards", row.findElement(By.xpath(".//td[@class='film-awards']")).getText().trim());
    
                // Determine if this film is the best picture winner
                boolean isWinner = !row.findElements(By.xpath(".//td[@class='film-best-picture']/i[contains(@class, 'glyphicon-flag')]")).isEmpty();
                filmData.put("isWinner", isWinner);
    
                filmsData.add(filmData);
            }
        }
        return filmsData;
    }

    public static void saveDataToJson(ChromeDriver driver, List<Map<String, Object>> data, String fileName) {
        ObjectMapper mapper = new ObjectMapper();

        // Specify the directory where the file will be saved
        String directory = "src/test/resources/";
        File jsonFile = new File(directory + fileName);

        // Delete the old file if it exists
        if (jsonFile.exists()) {
            boolean deleted = jsonFile.delete();
            if (deleted) {
                System.out.println("Old JSON file deleted successfully.");
            } else {
                System.out.println("Failed to delete the old JSON file.");
            }
        }
        // Save the new data to the JSON file with pretty printing
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, data);
            System.out.println("Data saved to " + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error writing data to JSON file: " + e.getMessage());
        }
    }

}
