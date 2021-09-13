package com.spmia.zuulserver.commons.filter;

import com.spmia.zuulserver.commons.model.AbTestingRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class SpecialRoutesFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Random random = SecureRandom.getInstanceStrong();

  private final WebClient webClient;

  public SpecialRoutesFilter(WebClient webClient) throws Exception {
    this.webClient = webClient;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    var request = exchange.getRequest();

    var serviceId = FilterUtils.getServiceId(exchange);

    log.debug("service id : {}", serviceId);

    webClient
        .get()
        .uri("http://specialrouteservice/v1/route/abtesting/{serviceName}", serviceId)
        .retrieve()
        .toEntity(AbTestingRoute.class)
        .subscribe(
            entity -> {
              AbTestingRoute abTestingRoute = entity.getBody();

              if (abTestingRoute != null && useSpecialRoute(abTestingRoute)) {
                String route =
                    buildRouteString(
                        request.getURI().getPath(), abTestingRoute.getEndpoint(), serviceId);

                log.debug("route : {}", route);
              }
            },
            ex -> log.warn(ex.getMessage()));

    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  public boolean useSpecialRoute(AbTestingRoute testRoute) {
    // 경로가 활성 상태인지 확인
    if ("N".equals(testRoute.getActive())) {
      return false;
    }

    // 대체 service path 의 사용 여부 결정
    int value = random.nextInt((10 - 1) + 1) + 1;

    return testRoute.getWeight() < value;
  }

  private String buildRouteString(String oldEndpoint, String newEndpoint, String serviceName) {
    int index = oldEndpoint.indexOf(serviceName);

    String strippedRoute = oldEndpoint.substring(index + serviceName.length());
    System.out.println("Target route: " + String.format("%s/%s", newEndpoint, strippedRoute));
    return String.format("%s/%s", newEndpoint, strippedRoute);
  }
}
