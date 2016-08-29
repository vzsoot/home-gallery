package com.akulogics.gallery.servlet;

import com.akulogics.gallery.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by zsolt_venczel on 2016.08.15
 */
public class LoginServlet extends HttpServlet {

    public static final String SESSION_TOKEN = "token";
    public static final String SESSION_USER = "user";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getReader().readLine();
        OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(resp.getOutputStream()));

        if (AuthenticationService.getService().fetchProfile(token)!=null) {
            HttpSession session = req.getSession();
            session.setAttribute(SESSION_TOKEN, token);

            out.write("{\"valid\": true}");
        } else {
            out.write("{\"valid\": false}");
        }
        out.close();
    }



}
