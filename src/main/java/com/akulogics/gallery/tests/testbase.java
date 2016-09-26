package com.akulogics.gallery.tests;

import org.junit.Rule;
import org.junit.rules.Timeout;

/**
 * Created by Andras_Gaal on 9/22/2016.
 */
public class testbase {


        @Rule
        public Timeout globalTimeout = new Timeout(15000);

}
