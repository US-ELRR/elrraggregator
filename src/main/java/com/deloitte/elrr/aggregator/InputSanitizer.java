package com.deloitte.elrr.aggregator;

import org.apache.commons.validator.GenericValidator;

public final class InputSanitizer {

    private static final String CHAR_WHITE_LIST_REGEX = "^[\\x09\\x0A\\x0D\\x20-\\x7E | \\xC2-\\xDF | \\xE0\\xA0-\\xBF | [\\xE1-\\xEC\\xEE\\xEF]{2} | \\xED\\x80-\\x9F | [\\xF0\\\\x90-\\xBF]{2} | [\\xF1-\\xF3]{3} | [\\xF4\\x80-\\x8F]{2}]*$";

    /**
     * @param input
     * @return boolean
     */
    public static boolean isValidInput(final String input) {
        return GenericValidator.matchRegexp(input, CHAR_WHITE_LIST_REGEX);

    }

    private InputSanitizer() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }
}
