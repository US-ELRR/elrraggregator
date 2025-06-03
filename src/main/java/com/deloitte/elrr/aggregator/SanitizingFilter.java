package com.deloitte.elrr.aggregator;

import java.io.IOException;
import java.util.Iterator;

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

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        WrappedHttp httpRequest;

        StringBuilder body = new StringBuilder();
        for (String line : request.getReader().lines().toList()) {
            if (InputSanitizer.isValidInput(line)) {
                body.append(line);
                body.append('\n');

            } else {
                // need to log bad request. Might be best to continue processing
                // and report all bad lines. / complete body
                httpResponse.sendError(HttpStatus.BAD_REQUEST.value(),
                        "Illegal line in request body: " + line);
            }
        }

        if (httpResponse.isCommitted()) {
            return;
        }

        httpRequest = new WrappedHttp((HttpServletRequest) request,
                body.toString());
        httpRequest.getParameterMap(); // might help to cache parameters for
                                       // future filter chain

        // below we check each parameter and parameter values for any invalid
        // strings
        httpRequest.getParameterNames().asIterator().forEachRemaining(param -> {
            String paramVal = request.getParameter(param);
            if (!InputSanitizer.isValidInput(paramVal)
                    || !InputSanitizer.isValidInput(param)) {
                try {
                    httpResponse.sendError(HttpStatus.BAD_REQUEST.value(),
                            "Illegal Parameter Value");
                    return;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        try {
            if (httpRequest.getBody().length() > 0
                    && hasHomoGlyphs(new JSONObject(httpRequest.getBody()))) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Request body contains homoglyphs.");
                log.warn("returning on homoglyph");
                return;
            }
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Malformed request body");
            return;
        }
        chain.doFilter(httpRequest, response);
    }

    private static boolean hasHomoGlyphs(JSONObject jo) {
        Confusables confusables = Confusables.fromInternal();

        Iterator<String> keys = jo.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object val = jo.get(key);
            if (val instanceof JSONObject) {
                if (hasHomoGlyphs((JSONObject) val)) {
                    return true;
                }
            } else if (confusables.isDangerous(key)
                    || confusables.isDangerous(String.valueOf(val))) {
                return true;
            }
        }
        return false;
    }
}
