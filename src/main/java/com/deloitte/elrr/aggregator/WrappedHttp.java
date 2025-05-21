package com.deloitte.elrr.aggregator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@SuppressWarnings("checkstyle:hiddenfield")
public class WrappedHttp extends HttpServletRequestWrapper {
    private final String body;

    /**
     * @param request
     * @param body
     */
    public WrappedHttp(HttpServletRequest request, String body) {
        super(request);
        this.body = body;

    }

    /**
     * @return body
     */
    public String getBody() {
        return this.body;
    }

    /**
     * @return
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));

    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream barray = new ByteArrayInputStream(body.getBytes());
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return barray.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                this.setReadListener(listener);

            }

            @Override
            public int read() throws IOException {
                return barray.read();
            }

        };

    }
}
