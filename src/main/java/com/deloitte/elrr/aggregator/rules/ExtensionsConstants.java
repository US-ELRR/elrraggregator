package com.deloitte.elrr.aggregator.rules;

@SuppressWarnings("checkstyle:linelength")
public final class ExtensionsConstants {

    public static final String CONTEXT_EXTENSIONS_EXPIRES = "https://w3id.org/xapi/comp/contextextensions/expires";
    public static final String CONTEXT_ACTIVITY_EXTENSIONS_EXPIRES = "http://xapi.edlm/goals/activity-extensions/expires";
    public static final String CONTEXT_ACTIVITY_EXTENSIONS_ACHIEVED_BY = "http://xapi.edlm/goals/activity-extensions/achieved-by";
    public static final String CONTEXT_EXTENSIONS_GOAL_TYPE = "http://xapi.edlm/goals/activity-extensions/goal-type";

    private ExtensionsConstants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
