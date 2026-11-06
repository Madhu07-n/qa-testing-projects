import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class signup1 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeMethod
    public void setup() {
        // KEEP YOUR OWN WORKING PATH LINE HERE if it is different from this one
        System.setProperty("webdriver.chrome.driver",
                "C:\\drivers\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Opens the Sign Up (14 days free trial) page directly
        driver.get("https://www.storehippo.com/en/free-trial");

        // Close the cookie box if it appears
        try {
        	WebElement gotIt=wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[contains(text(),'Got it')]")));
        	gotIt.click();
        } catch (Exception e) {
            // cookie box not shown, continue
        }

    }
    // Script 1: valid data in all fields
    @Test(priority = 1)
    public void validSignUp() {
        driver.findElement(By.xpath("//input[contains(@id,'store_name')]"))
                .sendKeys("mytestshop" + System.currentTimeMillis());
        driver.findElement(By.xpath("//input[@type='email']"))
                .sendKeys("test" + System.currentTimeMillis() + "@mail.com");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("Test@1234");
        driver.findElement(By.xpath("//input[@placeholder='Enter Phone']")).sendKeys("9876543210");
        driver.findElement(By.xpath("//textarea[contains(@id,'message')]"))
                .sendKeys("Interested in 14-day free trial");

        WebElement button= driver.findElement(By.xpath("//button[contains(text(), 'Go to Store')]"));
        Assert.assertTrue(button.isDisplayed() && button.isEnabled(), "Go to Store button not available");
        Assert.assertEquals(countVisibleErrors(), 0,"Validation errors shown for valid data");
    }

    // Script 2: invalid data, validation errors expected
    @Test(priority = 2)
    public void invalidSignUp() {
        driver.findElement(By.xpath("//input[contains(@id,'store_name')]")).sendKeys("");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("abc.com");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("123");
        driver.findElement(By.xpath("//input[@placeholder='Enter Phone']")).sendKeys("12345");
        driver.findElement(By.xpath("//textarea[contains(@id,'message')]")).sendKeys("");

        WebElement button= driver.findElement(By.xpath("//button[contains(text(),'Go to Store')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",button);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();",button);

        boolean formInvalid = (Boolean) ((JavascriptExecutor) driver)
        		.executeScript("return document.querySelectorAll('input:invalid').length > 0;");
        Assert.assertTrue(countVisibleErrors() > 0 || formInvalid,
        		"No validation error shown for invalid data");
        Assert.assertTrue(driver.getCurrentUrl().contains("free-trial"),
                "Page moved forward even with invalid data");
    }

    // Counts the red error messages that are actually visible on the form
    private int countVisibleErrors() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        List<WebElement> errors = driver.findElements(By.cssSelector("label.invalid-feedback"));
        int visible = 0;
        for (WebElement e : errors) {
            if (e.isDisplayed()) {
                visible++;
            }
        }
        return visible;
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}