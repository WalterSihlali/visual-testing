import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Eyes;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VisualTesting {

    private EyesRunner runner;
    private Eyes eyes;
    private static BatchInfo batchInfo;
    private WebDriver driver;
    private String propertyFile = "./src/test/resources/property.properties";


    @BeforeClass
    public static void setBatchInfo() {
        batchInfo = new BatchInfo("Visual Testing Demo");
    }

    @Before
    public void beforeEachTest() {
        runner = new ClassicRunner();
        eyes = new Eyes(runner);
        eyes.setApiKey(getConfigPropertyValue(propertyFile,"api_key"));
        eyes.setBatch(batchInfo);
        System.setProperty("webdriver.chrome.driver", getConfigPropertyValue(propertyFile,"chrome_driver_location"));
        driver = new ChromeDriver();
        driver.get(getConfigPropertyValue(propertyFile,"url"));
        driver.manage().window().maximize();
        eyes.open(driver,"Demo","Smoke test");
    }

    @Test
    public void basicTest() {
        String landingPage = driver.getTitle();
        Assert.assertEquals(landingPage,"Online Banking");
        secondsDelay(2);
        eyes.checkWindow("Money Web Landing Page");
        driver.findElement(By.className(PageObjects.branchLocator)).click();
        secondsDelay(2);
        int size = driver.findElements(By.className(PageObjects.searchField)).size();
        Assert.assertEquals(1,size);
        secondsDelay(4);
        eyes.checkWindow("Money ATM / Branch Locator Page");
    }

    @Test
    public void scrollingTest() {
        String landingPage = driver.getTitle();
        Assert.assertEquals(landingPage,"Online Banking");
        driver.findElement(By.xpath(PageObjects.appStoreLink)).click();
        secondsDelay(2);
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        secondsDelay(2);
        jse.executeScript("window.scrollTo(0,Math.max(document.documentElement.scrollHeight));");
        eyes.checkWindow("Whole Money ATM / Branch Locator Page");
    }

    @After
    public  void afterEachTest() {
        eyes.closeAsync();
        driver.quit();
    }

    private void secondsDelay(int sec) {
        int timeInMilliSeconds;
        try {
            timeInMilliSeconds = sec * 1000;
            Thread.sleep(timeInMilliSeconds);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    private String getConfigPropertyValue(String propertyFileName, String propertyName) {
        String Value = null;
        try {
            FileInputStream fileIS = new FileInputStream(new File(propertyFileName));
            Properties prop = new Properties();
            prop.load(fileIS);

            Value = prop.getProperty(propertyName);
        } catch (IOException ex) {
            ex.getStackTrace();
        }

        return Value;
    }
}
