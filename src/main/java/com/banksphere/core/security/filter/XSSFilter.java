package com.banksphere.core.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class XSSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
    }

    private static class XSSRequestWrapper extends HttpServletRequestWrapper {
        public XSSRequestWrapper(HttpServletRequest servletRequest) {
            super(servletRequest);
        }

        @Override
        public String[] getParameterValues(String parameter) {
            String[] values = super.getParameterValues(parameter);
            if (values == null) {
                return null;
            }
            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = stripXSS(values[i]);
            }
            return encodedValues;
        }

        @Override
        public String getParameter(String parameter) {
            String value = super.getParameter(parameter);
            return stripXSS(value);
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return stripXSS(value);
        }

        private String stripXSS(String value) {
            if (value != null) {
                // Avoid anything between script tags
                value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
                // Avoid anything in a src='...' type of expression
                value = value.replaceAll("(?i)src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", "");
                value = value.replaceAll("(?i)src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", "");
                // Remove any lonesome </script> tag
                value = value.replaceAll("(?i)</script>", "");
                // Remove any lonesome <script ...> tag
                value = value.replaceAll("(?i)<script.*?>", "");
                // Avoid eval(...) expressions
                value = value.replaceAll("(?i)eval\\((.*?)\\)", "");
                // Avoid expression(...) expressions
                value = value.replaceAll("(?i)expression\\((.*?)\\)", "");
                // Avoid javascript:... expressions
                value = value.replaceAll("(?i)javascript:", "");
                // Avoid vbscript:... expressions
                value = value.replaceAll("(?i)vbscript:", "");
                // Avoid onload= expressions
                value = value.replaceAll("(?i)onload(.*?)=", "");
            }
            return value;
        }
    }
}
