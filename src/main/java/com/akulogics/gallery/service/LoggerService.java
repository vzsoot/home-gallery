package com.akulogics.gallery.service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zsolt_venczel on 2016.10.25
 */
public class LoggerService {

    private static final SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String logFormat = "[%s] %s%n";
    private static final String logFormatUser = "[%s][%s] %s%n";

    public static void log(String message) {
        System.out.printf(logFormat, logDateFormat.format(new Date()), message);
    }

    public static void log(String user, String message) {
        System.out.printf(logFormatUser, logDateFormat.format(new Date()), user, message);
    }

}
