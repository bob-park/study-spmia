package com.spmia.zuulserver.commons.config;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
// * @EnableZuulServer 도 있지만 이것은 Eureka 가 아닌 다른 service discovery engine 과 통합할 경우 사용한다.
public class ZuulConfiguration {
//
//  @LoadBalanced
//  @Bean
//  public RestTemplate getRestTemplate() {
//    RestTemplate template = new RestTemplate();
//
//    List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
//
//    interceptors.add(new UserContextInterceptor());
//
//    return template;
//  }
}
