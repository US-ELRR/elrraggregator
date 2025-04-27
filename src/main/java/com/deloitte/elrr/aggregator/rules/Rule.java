package com.deloitte.elrr.aggregator.rules;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.yetanalytics.xapi.model.Statement;

public interface Rule {

	boolean fireRule(Statement statement);

	Person processRule(Person person, Statement statement)
			throws AggregatorException, ClassCastException, NullPointerException, RuntimeServiceException;
}
