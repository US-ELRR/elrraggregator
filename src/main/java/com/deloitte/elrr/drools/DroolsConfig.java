package com.deloitte.elrr.drools;

/*@Configuration
public class DroolsConfig {

  // private static final String RULES_CUSTOMER_RULES_DRL = "rules/customer-discount.drl";
  private static final KieServices kieServices = KieServices.Factory.get();

  @Bean
  public KieContainer kieContainer() {
    KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    // kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_CUSTOMER_RULES_DRL));
    loadFileBasedRules(kieFileSystem);
    KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
    kb.buildAll();
    KieModule kieModule = kb.getKieModule();
    KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
    return kieContainer;
  }

  private void loadFileBasedRules(final KieFileSystem kieFileSystem) {
    final File dir = new File("src/main/resources/rules");
    final File[] directoryListing = dir.listFiles();
    if (directoryListing != null) {
      for (File child : directoryListing) {
        kieFileSystem.write(ResourceFactory.newFileResource(child));
      }
    }
  }
}*/
