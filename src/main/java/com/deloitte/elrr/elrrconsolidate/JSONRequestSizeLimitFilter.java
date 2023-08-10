package com.deloitte.elrr.elrrconsolidate;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JSONRequestSizeLimitFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isApplicationJson(request) && request.getContentLengthLong() > 2000000) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Request size exceeds the limit.");
        }else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isApplicationJson(HttpServletRequest httpRequest) {
        return (MediaType.APPLICATION_JSON.isCompatibleWith(MediaType
                .parseMediaType(httpRequest.getHeader(HttpHeaders.CONTENT_TYPE))));
    }

}
