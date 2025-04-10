package com.deloitte.elrr.aggregator.drools;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    // Load rules into working memory
    String[] drlFiles = {
      "rules/processCompleted.drl", "rules/processCompetency.drl", "rules/processCredential.drl"
    };

    for (String drlFile : drlFiles) {
      kieFileSystem.write(ResourceFactory.newClassPathResource(drlFile));
    }
  }
}
