package com.spmia.zuulserver.commons.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

public class FilterUtils {
  public static final String CORRELATION_ID = "tmx-correlation-id";
  public static final String AUTH_TOKEN = "tmx-auth-token";
  public static final String USER_ID = "tmx-user-id";
  public static final String ORG_ID = "tmx-org-id";
  public static final String PRE_FILTER_TYPE = "pre";
  public static final String POST_FILTER_TYPE = "post";
  public static final String ROUTE_FILTER_TYPE = "route";

  public static String getCorrelationId(ServerWebExchange exchange) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    if (ctx.getRequest().getHeader(CORRELATION_ID) != null) {
    //      return ctx.getRequest().getHeader(CORRELATION_ID);
    //    } else {
    //      return ctx.getZuulRequestHeaders().get(CORRELATION_ID);
    //    }
    return getRequestHeader(exchange, CORRELATION_ID);
  }

  public static ServerHttpRequest setCorrelationId(ServerWebExchange exchange, String correlationId) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    ctx.addZuulRequestHeader(CORRELATION_ID, correlationId);

    return setRequestHeader(exchange, CORRELATION_ID, correlationId);
  }

  public static String getOrgId(ServerWebExchange exchange) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    if (ctx.getRequest().getHeader(ORG_ID) != null) {
    //      return ctx.getRequest().getHeader(ORG_ID);
    //    } else {
    //      return ctx.getZuulRequestHeaders().get(ORG_ID);
    //    }
    return getRequestHeader(exchange, ORG_ID);
  }

  public static void setOrgId(ServerWebExchange exchange, String orgId) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    ctx.addZuulRequestHeader(ORG_ID, orgId);

    setRequestHeader(exchange, ORG_ID, orgId);
  }

  public static String getUserId(ServerWebExchange exchange) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    if (ctx.getRequest().getHeader(USER_ID) != null) {
    //      return ctx.getRequest().getHeader(USER_ID);
    //    } else {
    //      return ctx.getZuulRequestHeaders().get(USER_ID);
    //    }
    return getRequestHeader(exchange, USER_ID);
  }

  public static void setUserId(ServerWebExchange exchange, String userId) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    ctx.addZuulRequestHeader(USER_ID, userId);

    setRequestHeader(exchange, USER_ID, userId);
  }

  public static String getAuthToken(ServerWebExchange exchange) {
    //    RequestContext ctx = RequestContext.getCurrentContext();
    //    return ctx.getRequest().getHeader(AUTH_TOKEN);

    return getRequestHeader(exchange, AUTH_TOKEN);
  }

  //  public static String getServiceId(ServerWebExchange exchange) {
  ////    RequestContext ctx = RequestContext.getCurrentContext();
  ////
  ////    // We might not have a service id if we are using a static, non-eureka route.
  ////    if (ctx.get("serviceId") == null) return "";
  ////    return ctx.get("serviceId").toString();
  //
  //  }

  private static String getRequestHeader(ServerWebExchange exchange, String headerName) {

    List<String> strings = exchange.getRequest().getHeaders().get(headerName);

    return strings == null ? null : String.join(";", strings);
  }

  private static ServerHttpRequest setRequestHeader(
      ServerWebExchange exchange, String headerName, String value) {
    return exchange.getRequest().mutate().header(headerName, value).build();
  }
}
