package com.deloitte.elrr.aggregator.drools;

import java.io.File;
import java.io.IOException;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DroolsConfig {

  private static final KieServices kieServices = KieServices.Factory.get();

  @Bean
  public KieContainer kieContainer() {
    KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    // Load rules into working memory
    loadFileBasedRules(kieFileSystem);
    KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
    kb.buildAll();
    KieModule kieModule = kb.getKieModule();
    KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
    return kieContainer;
  }

  private void loadFileBasedRules(final KieFileSystem kieFileSystem) {

    try {

      // Load rules into working memory
      File rule = new ClassPathResource("rules/processCompleted.drl").getFile();
      kieFileSystem.write(ResourceFactory.newFileResource(rule));

      rule = new ClassPathResource("rules/processCompetency.drl").getFile();
      kieFileSystem.write(ResourceFactory.newFileResource(rule));

    } catch (IOException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}
