package com.glmapper.ai.chat.ollama.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String decodedQueryString = decodeQueryString(request.getQueryString());
        log.info("""
                === Request Received ===
                Request URI: {}
                Request Method: {}
                Query String: {}
                Decoded Query String: {}
                Remote Address: {}
                """, 
                request.getRequestURI(), 
                request.getMethod(), 
                request.getQueryString(),
                decodedQueryString,
                request.getRemoteAddr());

        // 记录请求头信息
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> 
            log.info("Header {}: {}", headerName, request.getHeader(headerName))
        );

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
        log.info("""
                === Request Processed ===
                Response Status: {}
                """, response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        if (ex != null) {
            log.error("""
                    === Request Completed with Exception ===
                    Request URI: {}
                    Response Status: {}
                    Exception: 
                    """, request.getRequestURI(), response.getStatus(), ex);
        } else {
            log.info("""
                    === Request Completed Successfully ===
                    Request URI: {}
                    Response Status: {}
                    """, request.getRequestURI(), response.getStatus());
        }
    }
    
    private String decodeQueryString(String queryString) {
        if (queryString == null) {
            return null;
        }
        
        try {
            return URLDecoder.decode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("Failed to decode query string: {}", queryString, e);
            return queryString; // 如果解码失败，返回原始字符串
        }
    }
}