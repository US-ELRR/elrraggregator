package com.deloitte.elrr.drools;

import org.drools.core.rule.consequence.KnowledgeHelper;

public class RulesFiredHelper {
  public static void help(final KnowledgeHelper drools, final String message) {
    System.out.println(message);
    System.out.println("rule triggered: " + drools.getRule().getName() + "\n");
  }

  public static void helper(final KnowledgeHelper drools) {
    System.out.println("rule triggered: " + drools.getRule().getName() + "\n");
  }
}
