package com.akulogics.gallery.servlet;

import com.akulogics.gallery.service.AuthenticationService;
import com.akulogics.gallery.service.LoggerService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
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
@WebServlet(urlPatterns = "/login", loadOnStartup = 2)
public class LoginServlet extends HttpServlet {

    public static final String SESSION_TOKEN = "token";
    public static final String SESSION_USER = "user";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void init() {
        authenticationService.checkPathPermission("", "");
        LoggerService.log("LoginServlet init.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = req.getReader().readLine();
        OutputStreamWriter out = new OutputStreamWriter(new BufferedOutputStream(resp.getOutputStream()));

        GoogleIdToken idToken = authenticationService.fetchProfile(token);
        if (idToken != null) {
            HttpSession session = req.getSession();
            session.setAttribute(SESSION_TOKEN, token);
            session.setAttribute(SESSION_USER, idToken.getPayload().getEmail());

            out.write("{\"valid\": true}");

            LoggerService.log((String) session.getAttribute(SESSION_USER), "Login successful");
        } else {
            out.write("{\"valid\": false}");
        }
        out.close();
    }


}
