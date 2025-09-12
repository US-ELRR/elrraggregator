package com.deloitte.elrr.aggregator.rules;

import java.net.URI;

public final class ContextActivityTypeConstants {

    public static final URI OTHER_CRED_URI = URI.create(
            "https://w3id.org/xapi/cred/activities/credential");

    public static final URI OTHER_COMP_URI = URI.create(
            "https://w3id.org/xapi/comp/activities/competency");

    private ContextActivityTypeConstants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
