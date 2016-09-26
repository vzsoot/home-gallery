package com.akulogics.gallery.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by Andras_Gaal on 9/21/2016.
 */


public class testrunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(suite.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}