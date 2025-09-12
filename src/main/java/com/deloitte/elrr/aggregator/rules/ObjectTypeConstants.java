package com.deloitte.elrr.aggregator.rules;

@SuppressWarnings("checkstyle:linelength")
public final class ObjectTypeConstants {

    public static final String CREDENTIAL = "https://w3id.org/xapi/tla/activity-types/credential";
    public static final String OTHER_CREDENTIAL = "https://w3id.org/xapi/cred/activity/credential";
    public static final String GOAL = "https://w3id.org/xapi/activities/goal";

    private ObjectTypeConstants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
