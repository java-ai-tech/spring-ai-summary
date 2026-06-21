package com.glmapper.ai.chat.ollama.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class RequestResponseLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        log.info("""
                === Request Received ===
                Request URI: {}
                Request Method: {}
                Query String: {}
                Remote Address: {}
                """, 
                request.getRequestURI(), 
                request.getMethod(), 
                request.getQueryString(), 
                request.getRemoteAddr());

        // 记录请求头信息
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> 
            log.info("Header {}: {}", headerName, request.getHeader(headerName))
        );

        // 包装请求和响应对象以便读取内容
        RequestWrapper requestWrapper = new RequestWrapper(request);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);

        try {
            chain.doFilter(requestWrapper, responseWrapper);
            
            // 将包装的响应内容复制回原始响应
            byte[] responseToSend = responseWrapper.getDataStream();
            response.getOutputStream().write(responseToSend);
            
        } finally {
            log.info("""
                    === Request Completed ===
                    Request URI: {}
                    Response Status: {}
                    Request Body: {}
                    Response Body: {}
                    """, 
                    request.getRequestURI(), 
                    response.getStatus(),
                    requestWrapper.getBody(),
                    responseWrapper.getBody());
        }
    }
}