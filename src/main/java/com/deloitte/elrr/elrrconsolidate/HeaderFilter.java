package com.deloitte.elrr.elrrconsolidate;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HeaderFilter implements Filter {
    @Override
    public
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if("https".equalsIgnoreCase(httpServletRequest.getHeader("X-Forwarded-Proto"))) {
            chain.doFilter(request, response);
        }else {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Not a HTTPS request.");
        }
    }
}
