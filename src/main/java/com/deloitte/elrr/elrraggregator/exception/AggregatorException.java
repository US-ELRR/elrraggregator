package com.deloitte.elrr.elrraggregator.exception;

public class AggregatorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param errorMessage
     * @param e
     */
    public AggregatorException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
