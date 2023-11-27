package com.deloitte.elrr.elrrconsolidate;

import fr.spacefox.confusablehomoglyphs.Confusables;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Iterator;


@Component
@Slf4j
public class SanatizingFilter implements Filter {
	

	private boolean invalidParam;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		WrappedHttp httpRequest;
		invalidParam = false;
		
		StringBuilder body = new StringBuilder();
		for(String line : request.getReader().lines().toList()) {
			if(InputSanatizer.isValidInput(line)) {
				body.append(line);
				body.append('\n');
				
			}
			else {
				log.error("Illegal line in request body: " + line);
				httpResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Illegal line in request body: " + line);
			}
		}
		if(httpResponse.isCommitted())
			return;
		

		httpRequest = new WrappedHttp((HttpServletRequest) request, body.toString());
		httpRequest.getParameterMap(); //might help to cache parameters for future filter chain

		//below we check each parameter string for any invalid values
		httpRequest.getParameterNames().asIterator().forEachRemaining((param) -> { 
			String paramVal = request.getParameter(param);
			if(!InputSanatizer.isValidInput(paramVal)) {
				invalidParam = true;
				}
			});
		
		if(invalidParam) {
			log.error("Illegal Parameter Value");
			httpResponse.sendError(HttpStatus.BAD_REQUEST.value(), "Illegal Parameter Value");
			return;
		}

		if (hasHomoGlyphs(httpRequest))
		{
			log.error("Request body contains homoglyphs.");
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request body contains homoglyphs.");
			return;
		}

		chain.doFilter(httpRequest, response);
	}

	private static boolean hasHomoGlyphs(WrappedHttp httpRequest) {
		if(!StringUtils.hasLength(httpRequest.getBody()))
		{
			return false;
		}
		Confusables confusables = Confusables.fromInternal();
		JSONObject jsonObject = new JSONObject(httpRequest.getBody());
		Iterator keys = jsonObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
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


