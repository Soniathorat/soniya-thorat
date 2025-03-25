package appiumtests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
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
public class MonefyIncomeTest {
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
    @DisplayName("Verify Income is Added Successfully")
    public void testAddIncomeAndVerify() {
    	
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        double balanceBefore = getBalance(wait);
        System.out.println("Balance before adding income: " + balanceBefore);

        String incomeAmount = "2450"; 

        addIncome(wait, incomeAmount, "Salary", "Freelance Payment");

        double balanceAfter = getBalance(wait);
        System.out.println("Balance after adding income: " + balanceAfter);

        double enteredIncome = Double.parseDouble(incomeAmount);

        Assertions.assertEquals(balanceBefore + enteredIncome, balanceAfter, 0.01, "Balance did not update correctly!");
    }

    private void addIncome(WebDriverWait wait, String amount, String category, String note) {
        WebElement incomeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("com.monefy.app.lite:id/income_button")));
        incomeButton.click();

        for (char digit : amount.toCharArray()) {
            driver.findElement(By.id("com.monefy.app.lite:id/buttonKeyboard" + digit)).click();
        }

        WebElement noteField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//android.widget.EditText[@resource-id='com.monefy.app.lite:id/textViewNote']")
        ));
        noteField.click();
        noteField.sendKeys(note);
        driver.pressKey(new KeyEvent(AndroidKey.BACK));

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("com.monefy.app.lite:id/keyboard_action_button")
        ));
        saveButton.click();

        WebElement categoryElement = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//android.widget.TextView[@text='" + category + "']")
        ));
        categoryElement.click();
    }

    private double getBalance(WebDriverWait wait) {
        WebElement balanceView = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("com.monefy.app.lite:id/balance_amount")
        ));
        
        String balanceText = balanceView.getText();
        System.out.println("Raw Balance Text: " + balanceText);

        balanceText = balanceText.replaceAll("[^\\d.-]", "").trim();

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
