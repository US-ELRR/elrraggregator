package com.deloitte.elrr;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JSONRequestSizeLimitFilter extends OncePerRequestFilter {

  @Value("${json.max.size.limit}")
  private long MAX_SIZE_LIMIT;

  @Value("${check.media.type.json}")
  private boolean checkMediaTypeJson;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      if (isApplicationJson(request) && request.getContentLengthLong() < MAX_SIZE_LIMIT) {
        filterChain.doFilter(request, response);
      } else {
        log.error("Request size exceeds the limit.");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request size exceeds the limit.");
      }
    } catch (IOException | ServletException e) {
      log.error("Error: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }

  private boolean isApplicationJson(HttpServletRequest httpRequest) {

    if (checkMediaTypeJson == false) {
      return true;
    } else {
      return (MediaType.APPLICATION_JSON.isCompatibleWith(
          MediaType.parseMediaType(httpRequest.getHeader(HttpHeaders.CONTENT_TYPE))));
    }
  }
}
