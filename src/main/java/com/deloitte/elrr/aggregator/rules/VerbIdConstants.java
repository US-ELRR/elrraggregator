package com.deloitte.elrr.aggregator.rules;

import java.net.URI;

public final class VerbIdConstants {
    public static final URI COMPLETED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/completed");
    public static final URI ACHIEVED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/achieved");
    public static final URI PASSED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/passed");
    public static final URI FAILED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/failed");
    public static final URI INITIALIZED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/initialized");
    public static final URI SATISFIED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/satisfied");
    public static final URI REGISTERED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/registered");
    public static final URI SCHEDULED_VERB_ID = URI.create(
            "http://adlnet.gov/expapi/verbs/scheduled");
}
