package com.akulogics.gallery.tests.testcases;

import com.akulogics.gallery.tests.selectors;
import com.akulogics.gallery.tests.testbase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andras_Gaal on 9/22/2016.
 */

public class login extends testbase {

    public static WebDriver driver;
    private final String EMAIL = "testuser.20160921@gmail.com";
    private final String PASSWORD = "TestWithThisAccount222";
    private final String baseUrl = "http://localhost:8082";

    public void waitUntilElementPresent(String elementname) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(elementname)));
    }

    public void setUpDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("chrome.switches", "--disable-extensions");
        System.setProperty("webdriver.chrome.driver", "C:\\Windows\\chromedriver.exe");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get(baseUrl);
    }

    public void authenticateWithGoogle(){
        waitUntilElementPresent(selectors.SIGNIN_BUTTON);
        WebElement signInButton = driver.findElement(By.cssSelector(selectors.SIGNIN_BUTTON));
        signInButton.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitUntilElementPresent(selectors.GOOGLE_NOREMEMBER);
        WebElement element_noremember = driver.findElement(By.cssSelector(selectors.GOOGLE_NOREMEMBER));
        element_noremember.click();

        waitUntilElementPresent(selectors.GOOGLE_PASSWD);
        WebElement element_passwd = driver.findElement(By.cssSelector(selectors.GOOGLE_PASSWD));
        element_passwd.click();
        element_passwd.sendKeys(PASSWORD);
        element_passwd.sendKeys(Keys.ENTER);
    }


    public void isfirstTimeGoogleAccountApprovalNeeded(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = driver.getTitle().toString();
        System.out.println(result);
        String hun = "Engedély kérése";
        String en = "Request for Permission";
        if ((result.equals(hun)) || (result.equals(en))) {
            WebElement element_approve = driver.findElement(By.cssSelector(selectors.GOOGLE_APPROVE));
            element_approve.click();
        } else {
            System.out.println("No first time authentication needed!");
        }

    }

    @Test
    public void login() throws Exception {

        setUpDriver();
        authenticateWithGoogle();
        isfirstTimeGoogleAccountApprovalNeeded();

        // String popupWindow = (String) driver.getWindowHandles().toArray()[1];
        // driver.switchTo().window(popupWindow);
        //Thread.sleep(1000);
        //driver.switchTo().window("");
        //driver.switchTo().window(oldTab);
//        System.out.println(oldTab);
//        driver.switchTo().window(newTab.get(0));

    }
}
