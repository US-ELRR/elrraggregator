package com.deloitte.elrr.aggregator.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;

/**
 * @author phleven
 */
@SuppressWarnings("checkstyle:linelength")
public class AggregatorExceptionTest {

    private final String message = "AggregatorException" + "Exception Message";

    private final Exception e = new NullPointerException();

    private AggregatorException aggregatorException = new AggregatorException(
            message, e);

    /**
     * @author phleven
     */
    @Test
    public void testTipExceptionWithMessage() {
        assertEquals(aggregatorException.getMessage(), message);
    }
}
