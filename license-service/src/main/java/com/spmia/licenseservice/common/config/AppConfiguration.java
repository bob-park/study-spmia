package com.spmia.licenseservice.common.config;

import feign.Logger;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableDiscoveryClient // Spring Discovery Client 활성화
@EnableFeignClients("com.spmia.licenseservice.common.discovery.clients") // Netflix 에서 개발 및 지원이 중단되어 Spring Cloud OpenFeign 으로 이전되었다.
@EnableHystrix // Spring Cloud 에서 Hystrix 를 사용할 것이라고 지정 - 원래 @EnableCircuitBreaker 인데 deprecated 되었다.
public class AppConfiguration {

  @LoadBalanced // Spring Cloud 가 리본이 지원하는 RestTemplate 클래스를 생성하도록 지정
  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  // feign logger 를 별도로 지정해줘야 나오네?
  @Bean
  public Logger.Level feignLogger(){
    return Logger.Level.FULL;
  }
}


