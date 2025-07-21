package com.deloitte.elrr.aggregator.rules;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class ExtensionsConstantsTest {

    @Test
    void testConstantsExtensions() {
        assertEquals(ExtensionsConstants.CONTEXT_EXTENSIONS_EXPIRES,
                "https://w3id.org/xapi/comp/contextextensions/expires");
    }

}
