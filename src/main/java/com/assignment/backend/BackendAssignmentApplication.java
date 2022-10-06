package com.assignment.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
@EntityScan(basePackages = {"com.assignment.backend.model"})  // scan JPA entities
public class BackendAssignmentApplication {

	public static void main(String[] args) {SpringApplication.run(BackendAssignmentApplication.class, args);}
}
