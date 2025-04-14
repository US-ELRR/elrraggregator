package com.deloitte.elrr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class ElrrConsolidateApplication {

  protected ElrrConsolidateApplication() {}

  /**
   * @param args
   */
  public static void main(final String[] args) {
    SpringApplication.run(ElrrConsolidateApplication.class, args);
  }
}
