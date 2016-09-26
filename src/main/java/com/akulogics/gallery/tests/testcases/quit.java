package com.akulogics.gallery.tests.testcases;

import com.akulogics.gallery.tests.testbase;
import org.junit.Test;
import static com.akulogics.gallery.tests.testcases.login.driver;


/**
 * Created by Andras_Gaal on 9/22/2016.
 */

public class quit extends testbase {

    @Test
    public void quit() {
        driver.quit();
    }

}
