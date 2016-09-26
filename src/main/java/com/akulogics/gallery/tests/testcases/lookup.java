package com.akulogics.gallery.tests.testcases;

import com.akulogics.gallery.tests.testbase;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.akulogics.gallery.tests.testcases.login.*;

/**
 * Created by Andras_Gaal on 9/22/2016.
 */
public class lookup extends testbase {


    @Test
    public void findElements(){
        String result = driver.findElement(By.cssSelector("#galleryList > li > a > h3")).getText();
        Assert.assertEquals(result, "ovi");
    }

}
