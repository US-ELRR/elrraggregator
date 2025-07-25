package com.deloitte.elrr.aggregator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

public class FilterTest {

    private final SanitizingFilter sf = new SanitizingFilter();
    private WrappedHttp http;
    private JSONRequestSizeLimitFilter sl = new JSONRequestSizeLimitFilter();
    private HeaderFilter hf = new HeaderFilter();

    @Test
    void testIllegalBodyNotJson() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        http = new WrappedHttp(req, "not allowed");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertEquals(res.getStatus(), 400);
        assertEquals(res.getErrorMessage(), "Malformed request body");
        assertTrue(res.isCommitted());
    }

    @Test
    void testeeIllegalBodyNotJson() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        // http = new WrappedHttp(req, "not allowed");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(req, res, chain);
        assertEquals(res.getErrorMessage(), null);
        assertFalse(res.isCommitted());
    }

    @Test
    void testIllegalBodyWhitelist() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        http = new WrappedHttp(req, "{Unwise: afsd,.e\0nab}"); // not allowed
                                                               // illegal \0
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertTrue(res.getErrorMessage().contains(
                "Illegal line in request body"));
        assertTrue(res.isCommitted());
    }

    @Test
    void testIllegalParam() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("file./iofa\0je%00\\0/0/00efwho", "anything");
        http = new WrappedHttp(req, "{Unwise: nap}");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertEquals(res.getStatus(), 400);
        assertEquals(res.getErrorMessage(), "Illegal Parameter Value");
        assertTrue(res.isCommitted());
    }

    @Test
    void testIllegalParamValue() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("anything", "file./iofaje%00\0/0/00efwho");
        http = new WrappedHttp(req, "{Unwise: nap}");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sf.doFilter(http, res, chain);
        assertTrue(res.isCommitted());
    }

    @Test
    void testSanatizerOk() throws IOException, ServletException {

        ReflectionTestUtils.setField(sl, "maxSizeLimit", 2000000L);
        ReflectionTestUtils.setField(sl, "checkMediaTypeJson", false);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        req.addParameter("anything", "goes");
        String requestBody = "{Unwise: napping during work}";
        req.setContent(requestBody.getBytes());
        http = new WrappedHttp(req, requestBody);

        // next lines are simply to increase coverage of wrappedhttp
        http.getInputStream().available();
        http.getInputStream().isReady();
        http.getInputStream().read();

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sl.doFilter(http, res, chain);
        assertEquals(res.getErrorMessage(), null);
        assertFalse(res.isCommitted());
    }

    @Test
    void testSizeLimitOk() throws IOException, ServletException {

        ReflectionTestUtils.setField(sl, "maxSizeLimit", 2000000L);
        ReflectionTestUtils.setField(sl, "checkMediaTypeJson", false);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        req.addParameter("anything", "goes");
        String requestBody = "{Unwise: napping during work}";
        req.setContent(requestBody.getBytes());
        http = new WrappedHttp(req, requestBody);

        // next lines are simply to increase coverage of wrappedhttp
        http.getInputStream().available();
        http.getInputStream().isReady();
        http.getInputStream().read();

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        sl.doFilter(http, res, chain);
        assertEquals(res.getErrorMessage(), null);
        assertFalse(res.isCommitted());
    }

    @Test
    void testHeader() throws IOException, ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        req.addParameter("anything", "goes");
        String requestBody = "{Unwise: napping during work}";
        req.setContent(requestBody.getBytes());
        http = new WrappedHttp(req, requestBody);

        // next lines are simply to increase coverage of wrappedhttp
        http.getInputStream().available();
        http.getInputStream().isReady();
        http.getInputStream().read();

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        hf.doFilter(http, res, chain);
        assertEquals(res.getErrorMessage(), null);
        assertFalse(res.isCommitted());
    }

    @Test
    void testHeaderFilterHttps() throws IOException, ServletException {
        // Set the checkHttpHeader to true to enable header checking,
        ReflectionTestUtils.setField(hf, "checkHttpHeader", true);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Forwarded-Proto", "https");

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        hf.doFilter(req, res, chain);

        assertFalse(res.isCommitted());
    }

    @Test
    void testHeaderFilterNotHttps() throws IOException, ServletException {
        // Set the checkHttpHeader to true to enable header checking,
        ReflectionTestUtils.setField(hf, "checkHttpHeader", true);

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Forwarded-Proto", "testNotHttps");

        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        hf.doFilter(req, res, chain);

        assertTrue(res.isCommitted());
        // Should return Forbidden
        assertEquals(403, res.getStatus());
    }

    @Test
    void testHeaderFilterExceptionHandling() throws IOException,
            ServletException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("X-Forwarded-Proto", "https");

        MockHttpServletResponse res = new MockHttpServletResponse();

        FilterChain exceptionChain = (requ, resp) -> {
            throw new IOException("Test the exception handling");
        };

        hf.doFilter(req, res, exceptionChain);

        assertFalse(res.isCommitted());
    }

}
