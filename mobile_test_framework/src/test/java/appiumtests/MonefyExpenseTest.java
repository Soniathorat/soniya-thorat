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
public class MonefyExpenseTest {
    private AndroidDriver driver;
    private WebDriverWait wait;
    
    private static final String EXPENSE_BUTTON_ID = "com.monefy.app.lite:id/expense_button";
    private static final String BALANCE_ID = "com.monefy.app.lite:id/balance_amount";
    private static final String NOTE_FIELD_XPATH = "//android.widget.EditText[@resource-id='com.monefy.app.lite:id/textViewNote']";
    private static final String SAVE_BUTTON_ID = "com.monefy.app.lite:id/keyboard_action_button";

    @BeforeAll
    public void setup() throws MalformedURLException {
        Map<String, String> env = System.getenv(); // Load from system environment variables

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Reusable wait instance
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
    @DisplayName("Verify Expense is Deducted Successfully")
    public void testAddExpenseAndVerify() {
        double balanceBefore = getBalance();
        System.out.println("Balance before adding expense: " + balanceBefore);

        String expenseAmount = "200"; 
        addTransaction(expenseAmount, "Clothes", "New jacket!", false);

        double balanceAfter = getBalance();
        System.out.println("Balance after adding expense: " + balanceAfter);

        Assertions.assertEquals(balanceBefore - Double.parseDouble(expenseAmount), balanceAfter, 0.01, "Balance did not decrease correctly!");
    }

    private void addTransaction(String amount, String category, String note, boolean isIncome) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id(isIncome ? "com.monefy.app.lite:id/income_button" : EXPENSE_BUTTON_ID)));
        button.click();

        for (char digit : amount.toCharArray()) {
            driver.findElement(By.id("com.monefy.app.lite:id/buttonKeyboard" + digit)).click();
        }

        WebElement noteField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(NOTE_FIELD_XPATH)));
        noteField.click();
        noteField.sendKeys(note);
        driver.pressKey(new KeyEvent(AndroidKey.BACK));

        WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(SAVE_BUTTON_ID)));
        saveButton.click();

        WebElement categoryElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//android.widget.TextView[@text='" + category + "']")));
        categoryElement.click();
    }

    private double getBalance() {
        WebElement balanceView = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(BALANCE_ID)));
        String balanceText = balanceView.getText().trim();
        System.out.println("Raw Balance Text: " + balanceText);

        balanceText = balanceText.replaceAll("[^0-9.-]", "").trim();
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
