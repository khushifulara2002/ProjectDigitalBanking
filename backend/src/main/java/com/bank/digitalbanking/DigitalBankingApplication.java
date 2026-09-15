package com.bank.digitalbanking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



/**
 * Main Entry Point for Digital Banking  Management Application.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 * 1. @Configuration: Tags the class as a source of bean definitions.
 * 2. @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings.
 * 3. @ComponentScan: Tells Spring to look for other components, configurations, and services in 'com.bank.finance'.
 */


@SpringBootApplication
public class DigitalBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DigitalBankingApplication.class, args);
	}
}