package com.deloitte.elrr.aggregator.rules;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class ExtensionsConstantsTest {

    @Test
    void testConstants_Extensions() {
        assertEquals(ExtensionsConstants.CONTEXT_EXTENSIONS,
                "https://w3id.org/xapi/comp/contextextensions/expires");
    }

}
