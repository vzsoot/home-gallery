package com.akulogics.gallery.servlet.filter;

import com.akulogics.gallery.servlet.LoginServlet;
import com.akulogics.gallery.service.AuthenticationService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by zsolt_venczel on 2016.08.17
 */
public class AuthenticationFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) servletRequest).getSession();
            Object emailId = session.getAttribute(LoginServlet.SESSION_USER);
            if (emailId==null) {
                Object token = session.getAttribute(LoginServlet.SESSION_TOKEN);
                if (token != null) {
                    GoogleIdToken idToken = AuthenticationService.getService().fetchProfile(token.toString());
                    if (idToken != null && idToken.getPayload() != null) {
                        emailId = idToken.getPayload().getEmail();
                        session.setAttribute(LoginServlet.SESSION_USER, emailId);
                    }
                }
            }
            servletRequest.setAttribute(LoginServlet.SESSION_USER, emailId);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
