package com.spmia.zuulserver.commons.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SpecialRoutesFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {



    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
