package com.akulogics.gallery.servlet;

import com.akulogics.gallery.service.FileService;
import com.akulogics.gallery.service.LoggerService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by zsolt_venczel on 2016.08.30
 */
public class InitServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        LoggerService.log("Directory read started!");
        long timeStart = System.currentTimeMillis();
        FileService.getService();
        long timeEnd = System.currentTimeMillis();
        LoggerService.log("Directory read finished in " + (timeEnd-timeStart) + "ms");
    }
}
