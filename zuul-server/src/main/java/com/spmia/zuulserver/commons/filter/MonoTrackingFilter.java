package com.spmia.zuulserver.commons.filter;

import com.spmia.zuulserver.commons.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class MonoTrackingFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final int FILTER_ORDER = -1;

  //  private final FilterUtils filterUtils;
  //
  //  public MonoTrackingFilter(FilterUtils filterUtils) {
  //    this.filterUtils = filterUtils;
  //  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    log.debug("First Pre Global Filter");

    // ? 상관관계 ID 로 지정하고 마이크로서비스에서 실행된 것을 추척하기 위해 request set header 를 하는 것 - 근데 안됨 - request header
    // 는 read only 이다.
    //    if (isCorrelationIdPresent()) {
    //      log.debug("tmx-correlation-id found in tracking filter: {}",
    // filterUtils.getCorrelationId());
    //    } else {
    //      filterUtils.setCorrelationId(generateCorrelationId());
    //      log.debug(
    //          "tmx-correlation-id generated in tracking filter: {}",
    // filterUtils.getCorrelationId());
    //    }

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

    return chain
        .filter(exchange)
        .then(Mono.fromRunnable(() -> log.info("Last Post Global Filter")));
  }

  @Override
  public int getOrder() {
    return FILTER_ORDER;
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
