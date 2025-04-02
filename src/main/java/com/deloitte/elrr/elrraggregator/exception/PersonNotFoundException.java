package com.deloitte.elrr.elrraggregator.exception;

public class PersonNotFoundException extends RuntimeException {

  public PersonNotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
