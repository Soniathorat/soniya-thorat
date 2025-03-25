package appiumtests;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MonefyBalanceTest {
    private AndroidDriver driver;

    @BeforeAll
    public void setup() throws MalformedURLException {
    	
        Map<String, String> env = System.getenv();

        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("platformName", ConfigReader.getProperty("platformName"));
        cap.setCapability("appium:deviceName", env.getOrDefault("DEVICE_NAME", ConfigReader.getProperty("DEVICE_NAME")));
        cap.setCapability("appium:udid", env.getOrDefault("UDID", ConfigReader.getProperty("UDID")));
        cap.setCapability("appium:platformVersion", env.getOrDefault("PLATFORM_VERSION", ConfigReader.getProperty("PLATFORM_VERSION")));
        cap.setCapability("appium:automationName", ConfigReader.getProperty("automationName"));
        cap.setCapability("appium:appPackage", ConfigReader.getProperty("appPackage"));
        cap.setCapability("appium:appActivity", ConfigReader.getProperty("appActivity"));
        cap.setCapability("appium:autoGrantPermissions", Boolean.parseBoolean(ConfigReader.getProperty("autoGrantPermissions")));
        cap.setCapability("appium:noReset", Boolean.parseBoolean(ConfigReader.getProperty("noReset")));


        driver = new AndroidDriver(new URL(ConfigReader.getProperty("url")), cap);
        System.out.println("Monefy app started...");
    }

    @BeforeEach
    public void setUp() {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", ConfigReader.getProperty("platformName"));
        caps.setCapability("deviceName", ConfigReader.getProperty("deviceName"));
        caps.setCapability("app", ConfigReader.getProperty("appPath"));

        try {
            driver = new AndroidDriver(new URL(ConfigReader.getProperty("appiumServerURL")), caps);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid Appium server URL");
        }
    }
    @Test
    @DisplayName("Verify Balance after Adding Expense and Income")
    public void testBalanceAfterTransactions() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        double previousBalance = getBalance(wait);

        double expenseAmount = 25.00;
        double incomeAmount = 40.00;

        addTransaction(wait, "expense_button", String.valueOf((int) expenseAmount), "Communications");
        addTransaction(wait, "income_button", String.valueOf((int) incomeAmount), "Deposits");

        double expectedBalance = previousBalance + incomeAmount - expenseAmount;

        double displayedBalance = getBalance(wait);

        System.out.println("Previous Balance: $" + previousBalance);
        System.out.println("Expected Balance: $" + expectedBalance);
        System.out.println("Final Displayed Balance: $" + displayedBalance);

        Assertions.assertEquals(expectedBalance, displayedBalance, 0.01, "Balance should match calculated value!");
    }

    private void addTransaction(WebDriverWait wait, String buttonId, String amount, String category) {
        WebElement transactionButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.monefy.app.lite:id/" + buttonId)));
        transactionButton.click();
        System.out.println("Clicked on " + (buttonId.contains("expense") ? "Expense" : "Income") + " button");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.monefy.app.lite:id/buttonKeyboard0")));

        for (char digit : amount.toCharArray()) {
            driver.findElement(By.id("com.monefy.app.lite:id/buttonKeyboard" + digit)).click();
        }
        System.out.println("Entered amount: " + amount);

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("com.monefy.app.lite:id/keyboard_action_button")
        ));
        saveButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.TextView[@text='" + category + "']")));

        WebElement categoryElement = driver.findElement(By.xpath("//android.widget.TextView[@text='" + category + "']"));
        categoryElement.click();
        System.out.println("Selected category: " + category);
    }

    private double getBalance(WebDriverWait wait) {
        WebElement balanceView = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("com.monefy.app.lite:id/balance_amount")
        ));
        String balanceText = balanceView.getText().replaceAll("[^\\d.-]", ""); 
        return Double.parseDouble(balanceText);
    }

    @AfterAll
    public void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Monefy app closed.");
        }
    }
}
