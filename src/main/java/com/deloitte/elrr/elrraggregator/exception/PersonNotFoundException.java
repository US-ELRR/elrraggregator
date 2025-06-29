package com.deloitte.elrr.elrraggregator.exception;

public class PersonNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param errorMessage
     */
    public PersonNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
