package com.deloitte.elrr.aggregator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import com.deloitte.elrr.aggregator.rules.ObjectTypeConstants;

public class ObjectTypeConstantsTest {


    @Test
    public void testObjectTypeConstantsPrivateConstructor() {
        try {
            // Get the constructor object for ObjectTypeConstants and make it accessible
            Constructor<ObjectTypeConstants> constructor = ObjectTypeConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (InvocationTargetException ie) {
            assertEquals("This is a utility class and cannot be instantiated", ie.getCause().getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
