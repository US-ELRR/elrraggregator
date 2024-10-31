package com.deloitte.elrr.elrrconsolidate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HeaderFilter implements Filter {
    
    @Value("${http.header}")
    private String httpHeader;

    @Override
    public
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (httpHeader != null 
                && !httpHeader.trim().isEmpty()) {
            
            if (httpHeader.equalsIgnoreCase("off")) {
                chain.doFilter(request, response);
            } else {
                if("https".equalsIgnoreCase(httpServletRequest.getHeader("X-Forwarded-Proto"))) {
                    chain.doFilter(request, response);
                } else {
                    log.error("Not a HTTPS request.");
                    ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a HTTPS request.");
                }
            }
            
        } else {
            
            chain.doFilter(request, response);
            
        }        
           
    }
}
