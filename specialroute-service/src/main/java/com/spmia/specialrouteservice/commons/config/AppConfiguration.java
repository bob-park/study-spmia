package com.spmia.specialrouteservice.commons.config;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEurekaClient
@EnableHystrix
public class AppConfiguration {}
