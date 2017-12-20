package com.akulogics.gallery.servlet;

import com.akulogics.gallery.service.FileService;
import com.akulogics.gallery.service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * Created by zsolt_venczel on 2016.08.30
 */
@WebServlet(urlPatterns = "/init", loadOnStartup = 0)
public class InitServlet extends HttpServlet {

    @Autowired
    FileService fileService;

    @Override
    public void init() {
        LoggerService.log("Directory read started!");
        long timeStart = System.currentTimeMillis();
        fileService.initItemCache();
        long timeEnd = System.currentTimeMillis();
        LoggerService.log("Directory read finished in " + (timeEnd - timeStart) + "ms");
    }
}
