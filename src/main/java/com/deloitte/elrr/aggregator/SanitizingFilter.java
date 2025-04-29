package com.deloitte.elrr.aggregator;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import fr.spacefox.confusablehomoglyphs.Confusables;
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
public class SanitizingFilter implements Filter {

  private boolean invalidParam;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    WrappedHttp httpRequest;
    invalidParam = false;

    StringBuilder body = new StringBuilder();
    try {
      for (String line : request.getReader().lines().toList()) {
        if (InputSanitizer.isValidInput(line)) {
          body.append(line);
          body.append('\n');

        } else {
          // need to log bad request. Might be best to continue processing
          // and report all bad lines. / complete body
          httpResponse.sendError(
              HttpStatus.BAD_REQUEST.value(), "Illegal line in request body: " + line);
        }
      }
    } catch (IOException | IllegalStateException e) {
      log.error("Error: " + e.getMessage());
      e.printStackTrace();
      return;
    }

    if (httpResponse.isCommitted()) return;

    httpRequest = new WrappedHttp((HttpServletRequest) request, body.toString());
    httpRequest.getParameterMap(); // might help to cache parameters for
    // future filter chain

    // Check each parameter string for any invalid values
    httpRequest
        .getParameterNames()
        .asIterator()
        .forEachRemaining(
            (param) -> {
              String paramVal = request.getParameter(param);
              if (!InputSanitizer.isValidInput(paramVal)) {
                invalidParam = true;
                log.error("Illegal Parameter Value " + paramVal);
              }
            });

    if (invalidParam) {
      try {
        httpResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Illegal Parameter Value");
        return;
      } catch (IOException e) {
        log.error("Error: " + e.getMessage());
        e.printStackTrace();
        return;
      }
    }

    if (hasHomoGlyphs(httpRequest)) {
      try {
        log.error("Request body contains homoglyphs.");
        httpResponse.sendError(
            HttpServletResponse.SC_BAD_REQUEST, "Request body contains homoglyphs.");
        return;
      } catch (IOException | JSONException e) {
        log.error("Error: " + e.getMessage());
        e.printStackTrace();
        return;
      }
    }

    try {
      chain.doFilter(httpRequest, response);
    } catch (IOException | ServletException e) {
      log.error("Error: " + e.getMessage());
      e.printStackTrace();
      return;
    }
  }

  private static boolean hasHomoGlyphs(WrappedHttp httpRequest) {

    if (httpRequest.getBody().isEmpty()) return false;
    Confusables confusables = Confusables.fromInternal();
    JSONObject jsonObject = new JSONObject(httpRequest.getBody());
    Iterator<String> keys = jsonObject.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = (String) jsonObject.get(key);
      boolean dangerousKey = confusables.isDangerous(key);
      boolean dangerousValue = confusables.isDangerous(value);
      if (dangerousKey || dangerousValue) {
        return true;
      }
    }
    return false;
  }
}
