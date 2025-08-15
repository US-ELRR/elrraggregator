package com.deloitte.elrr.aggregator.rules;

@SuppressWarnings("checkstyle:linelength")
public final class ExtensionsConstants {

    public static final String CONTEXT_EXTENSION_EXPIRES = "https://w3id.org/xapi/comp/contextextensions/expires";
    public static final String ACTIVITY_EXTENSION_EXPIRES = "http://xapi.edlm/goals/activity-extensions/expires";
    public static final String ACTIVITY_EXTENSION_ACHIEVED_BY = "http://xapi.edlm/goals/activity-extensions/achieved-by";
    public static final String CONTEXT_EXTENSION_GOAL_TYPE = "http://xapi.edlm/goals/activity-extensions/goal-type";
    public static final String CONTEXT_EXTENSION_BY = "https://yetanalytics.com/profiles/prepositions/concepts/context-extensions/by";
    public static final String CONTEXT_EXTENSION_TO = "https://yetanalytics.com/profiles/prepositions/concepts/context-extensions/to";

    private ExtensionsConstants() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
