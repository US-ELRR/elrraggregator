package com.deloitte.elrr.elrrconsolidate;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JSONRequestSizeLimitFilter extends OncePerRequestFilter {
    private static final long MAX_SIZE_LIMIT = 2000000;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (isApplicationJson(request) && request.getContentLengthLong() < MAX_SIZE_LIMIT) {
            filterChain.doFilter(request, response);
        }else {
            log.error("Request size exceeds the limit.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request size exceeds the limit.");
        }
    }

    private boolean isApplicationJson(HttpServletRequest httpRequest) {
        return (MediaType.APPLICATION_JSON.isCompatibleWith(MediaType
                .parseMediaType(httpRequest.getHeader(HttpHeaders.CONTENT_TYPE))));
    }

}
