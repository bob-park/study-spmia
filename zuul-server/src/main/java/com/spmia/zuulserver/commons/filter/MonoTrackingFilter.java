package com.spmia.zuulserver.commons.filter;

import com.spmia.zuulserver.commons.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MonoTrackingFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    log.debug("First Pre Global Filter");

    String correlationId = null;

    if (isCorrelationIdPresent(exchange)) {
      correlationId = FilterUtils.getCorrelationId(exchange);
      log.debug("tmx-correlation-id found in tracking filter: {}", correlationId);
    } else {
      correlationId = generateCorrelationId();

      exchange =
          exchange.mutate().request(FilterUtils.setCorrelationId(exchange, correlationId)).build();

      log.debug("tmx-correlation-id generated in tracking filter: {}", correlationId);
    }

    UserContextHolder.getContext().setCorrelationId(correlationId);

    log.debug("Processing incoming request for {}", exchange.getRequest().getURI());

    // response 에 correlation_id 추가
    exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, correlationId);

    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  private boolean isCorrelationIdPresent(ServerWebExchange exchange) {
    return FilterUtils.getCorrelationId(exchange) != null;
  }

  /**
   * 실제로 tmx-correlation-id 존재 여부를 확인하고 상관관계 ID 값을 생성하는 helper method
   *
   * @return
   */
  private String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }
}
