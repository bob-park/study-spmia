package com.spmia.zuulserver.commons.config;

import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;

@Configuration
// * @EnableZuulServer 도 있지만 이것은 Eureka 가 아닌 다른 service discovery engine 과 통합할 경우 사용한다.
@EnableZuulProxy // service 를 zuul server 로 사용한다.
public class ZuulConfiguration {
}
