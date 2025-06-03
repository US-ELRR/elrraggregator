package com.deloitte.elrr.aggregator.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.elrraggregator.exception.PersonNotFoundException;

/**
 * @author phleven
 * 
 */
@SuppressWarnings("checkstyle:linelength")
public class PersonNotFoundExceptionTest {

    private final String message = "PersonNotFoundException" + "Exception Message";

    private PersonNotFoundException personNotFoundException = new PersonNotFoundException(
            message);

    /**
     * @author phleven
     */
    @Test
    public void testTipExceptionWithMessage() {
        assertEquals(personNotFoundException.getMessage(), message);
    }
}
