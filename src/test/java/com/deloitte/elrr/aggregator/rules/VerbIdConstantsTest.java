package com.deloitte.elrr.aggregator.rules;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VerbIdConstantsTest {

    @Test
    void testConstants_Extensions() {

        try {
            URI uri = new URI("http://adlnet.gov/expapi/verbs/achieved");
            assertEquals(VerbIdConstants.ACHIEVED_VERB_ID, uri);
        } catch (URISyntaxException e) {
            log.error("Error: " + e.getMessage());
        }

    }

}
