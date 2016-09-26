package com.akulogics.gallery.tests;

import com.akulogics.gallery.tests.testcases.login;
import com.akulogics.gallery.tests.testcases.lookup;
import com.akulogics.gallery.tests.testcases.quit;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Andras_Gaal on 9/21/2016.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        login.class,
        lookup.class,
        quit.class
})

public class suite {
    public void test() throws Exception {

    }
}
