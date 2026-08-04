package com.banksphere.core.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IPWhitelistingFilter implements Filter {

    @Value("${banksphere.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        String path = httpRequest.getRequestURI();
        
        if (path.startsWith("/admin/")) {
            String ipAddress = request.getRemoteAddr();
            // TODO: Check if request IP is in whitelist for admin paths
            // If not, return 403 Forbidden
        }

        chain.doFilter(request, response);
    }
}
