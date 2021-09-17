package com.spmia.configserver.common.config;

import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEurekaClient
@EnableConfigServer // 서비스를 String Cloud Service 로 사용 가능하게 한다.
public class AppConfiguration {
}
