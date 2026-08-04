package com.banksphere.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.banksphere.core.constants.BankingConstants;

@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        START_TIME.set(System.currentTimeMillis());
        String correlationId = (String) request.getAttribute(BankingConstants.CORRELATION_ID_HEADER);
        log.info("Incoming Request: {} {} | CorrelationId: {}", request.getMethod(), request.getRequestURI(), correlationId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = START_TIME.get();
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed Request: {} {} | Status: {} | Duration: {}ms", request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
            START_TIME.remove();
        }
    }
}
