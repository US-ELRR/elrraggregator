package com.deloitte.elrr.elrrconsolidate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ECCService {

  @Autowired private LearningResourceSvc learningResourceService;

  public LearningResource getLearningResource(String iri) {

    LearningResource learningResource = null;
    // Add get learningResource logic

    return learningResource;
  }
}
