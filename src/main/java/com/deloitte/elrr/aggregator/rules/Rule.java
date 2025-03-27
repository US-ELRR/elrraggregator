package com.deloitte.elrr.aggregator.rules;

import com.deloitte.elrr.entity.Person;
import com.yetanalytics.xapi.model.Statement;

public interface Rule {

  void processRule(Person person, Statement statement) throws Exception;
}
