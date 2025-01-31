package com.deloitte.elrr.drools;

public enum CustomerType {
  LOYAL,
  NEW,
  DISSATISFIED;

  public String getValue() {
    return this.toString();
  }
}
