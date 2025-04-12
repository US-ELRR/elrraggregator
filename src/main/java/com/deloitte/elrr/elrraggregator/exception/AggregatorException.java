package com.deloitte.elrr.elrraggregator.exception;

public class AggregatorException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AggregatorException(String errorMessage) {
    super(errorMessage);
  }
}
