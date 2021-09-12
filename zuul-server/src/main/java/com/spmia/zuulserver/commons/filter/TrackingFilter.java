//package com.spmia.zuulserver.commons.filter;
//
//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import com.netflix.zuul.exception.ZuulException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//public class TrackingFilter extends ZuulFilter {
//
//  private static final int FILTER_ORDER = 1;
//  private static final boolean SHOULD_FILTER = true;
//
//  private final Logger log = LoggerFactory.getLogger(getClass());
//
//  private final FilterUtils filterUtils;
//
//  public TrackingFilter(FilterUtils filterUtils) {
//    this.filterUtils = filterUtils;
//  }
//
//  /**
//   * zuul 에서 사전-경로-사후 필터를 지정하는데 사용된다.
//   *
//   * @return
//   */
//  @Override
//  public String filterType() {
//    return FilterUtils.PRE_FILTER_TYPE;
//  }
//
//  /**
//   * zuul 이 다른 필터 유형으로 요청을 보내야하는 순서를 나타내는 값
//   *
//   * @return
//   */
//  @Override
//  public int filterOrder() {
//    return FILTER_ORDER;
//  }
//
//  /**
//   * 필터의 활성화 여부를 나타낸다.
//   *
//   * @return
//   */
//  @Override
//  public boolean shouldFilter() {
//    return SHOULD_FILTER;
//  }
//
//  private boolean isCorrelationIdPresent() {
//    return filterUtils.getCorrelationId() != null;
//  }
//
//  @Override
//  public Object run() throws ZuulException {
//
//    if (isCorrelationIdPresent()) {
//      log.debug("tmx-correlation-id found in tracking filter: {}", filterUtils.getCorrelationId());
//    } else {
//      filterUtils.setCorrelationId(generateCorrelationId());
//
//      log.debug(
//          "tmx-correlation-id generated in tracking filter: {}", filterUtils.getCorrelationId());
//    }
//
//    RequestContext ctx = RequestContext.getCurrentContext();
//
//    log.debug("Processing incoming request for {}", ctx.getRequest().getRequestURI());
//
//    return null;
//  }
//
//  /**
//   * 실제로 tmx-correlation-id 존재 여부를 확인하고 상관관계 ID 값을 생성하는 helper method
//   *
//   * @return
//   */
//  private String generateCorrelationId() {
//    return UUID.randomUUID().toString();
//  }
//}
