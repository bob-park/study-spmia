package com.spmia.organizationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class OrganizationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrganizationServiceApplication.class, args);
  }
}
