package com.spmia.zuulserver.commons.config;

import com.spmia.zuulserver.commons.utils.UserContextInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class ZuulConfiguration {

  @LoadBalanced
  @Bean
  public RestTemplate getRestTemplate() {
    RestTemplate template = new RestTemplate();

    List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();

    interceptors.add(new UserContextInterceptor());

    return template;
  }
}
