package com.deloitte.elrr.elrrconsolidate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ElrrConsolidateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElrrConsolidateApplication.class, args);
	}

}
