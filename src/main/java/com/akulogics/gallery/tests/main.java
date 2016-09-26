package com.akulogics.gallery.tests;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class main {

    public WebDriver driver;
    private final String EMAIL = "testuser.20160921@gmail.com";
    private final String PASSWORD = "TestWithThisAccount222";
    private final String baseUrl = "http://localhost:8082";

    public void waitUntilElementPresent(String elementname) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(elementname)));
    }


    @Before
    public void setUp() throws Exception {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("chrome.switches", "--disable-extensions");
        System.setProperty("webdriver.chrome.driver", "C:\\Windows\\chromedriver.exe");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void opensite() throws Exception {
        driver.get(baseUrl);

        waitUntilElementPresent(selectors.SIGNIN_BUTTON);
        WebElement signInButton = driver.findElement(By.cssSelector(selectors.SIGNIN_BUTTON));
        signInButton.click();
        Thread.sleep(1000);


       // String popupWindow = (String) driver.getWindowHandles().toArray()[1];
        // driver.switchTo().window(popupWindow);

        String oldTab = driver.getWindowHandle();
        ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());
        //newTab.remove(oldTab);
        System.out.println(newTab);
        System.out.println(oldTab);
        driver.switchTo().window(newTab.get(1));

        waitUntilElementPresent(selectors.GOOGLE_EMAIL);
        WebElement element_email = driver.findElement(By.cssSelector(selectors.GOOGLE_EMAIL));
        element_email.click();
        element_email.sendKeys(EMAIL);
        element_email.sendKeys(Keys.ENTER);

        Thread.sleep(1000);
        waitUntilElementPresent(selectors.GOOGLE_NOREMEMBER);
        WebElement element_noremember = driver.findElement(By.cssSelector(selectors.GOOGLE_NOREMEMBER));
        element_noremember.click();

        waitUntilElementPresent(selectors.GOOGLE_PASSWD);
        WebElement element_passwd = driver.findElement(By.cssSelector(selectors.GOOGLE_PASSWD));
        element_passwd.click();
        element_passwd.sendKeys(PASSWORD);
        element_passwd.sendKeys(Keys.ENTER);

        //Thread.sleep(1000);
        //WebElement element_approve = driver.findElement(By.cssSelector(selectors.GOOGLE_APPROVE));

        Thread.sleep(1000);
//        if (element_approve.isDisplayed()) {
//            element_approve.click();
//        } else {
//            //
//        }

        //driver.switchTo().window("");
        //driver.switchTo().window(oldTab);
        System.out.println(oldTab);
        driver.switchTo().window(newTab.get(0));
        Thread.sleep(1000);
        waitUntilElementPresent(selectors.SEARCH);
        WebElement buttonone = driver.findElement(By.cssSelector("#galleries > button:nth-child(3)"));
        buttonone.click();

        WebElement search = driver.findElement(By.cssSelector(selectors.SEARCH));
        Actions action = new Actions(driver);
        action.moveToElement(search).click().perform();

        search.clear();
        search.sendKeys("ovi");
        search.sendKeys(Keys.ENTER);
        String result = driver.findElement(By.cssSelector("#galleryList > li > a > h3")).getText();
        Assert.assertEquals(result, "ovi");

    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }


}
