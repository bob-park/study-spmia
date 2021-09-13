package com.spmia.zuulserver.commons.filter;

import com.spmia.zuulserver.commons.model.AbTestingRoute;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SpecialRoutesFilter implements GlobalFilter, Ordered {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final Random random = SecureRandom.getInstanceStrong();

  private final RestTemplate restTemplate;

  public SpecialRoutesFilter(RestTemplate restTemplate) throws Exception {
    this.restTemplate = restTemplate;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    var request = exchange.getRequest();

    var serviceId = FilterUtils.getServiceId(exchange);

    var abTestRoute = getAbRoutingInfo(serviceId);

    if (abTestRoute != null && useSpecialRoute(abTestRoute)) {
      String route =
          buildRouteString(request.getURI().getPath(), abTestRoute.getEndpoint(), serviceId);

      log.debug("route : {}", route);

      //      forwardToSpecialRoute(route);
    }

    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }

  private AbTestingRoute getAbRoutingInfo(String serviceName) {

    ResponseEntity<AbTestingRoute> restExchange = null;

    try {
      // SpecialRouteService end-point 호출
      restExchange =
          restTemplate.exchange(
              "http://specialrouteservice/v1/route/abtesing/{serviceName}",
              HttpMethod.GET,
              null,
              AbTestingRoute.class,
              serviceName);
    } catch (HttpClientErrorException e) {
      // record 를 찾지 못하면 (HTTP 404 상태 리턴되면 메서드를 null 리턴
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return null;
      }

      throw e;
    }

    return restExchange.getBody();
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

  //  private void forwardToSpecialRoute(, String route) {
  //
  //    HttpServletRequest request = context.getRequest();
  //
  //    MultiValueMap<String, String> headers = this.helper
  //            .buildZuulRequestHeaders(request);
  //    MultiValueMap<String, String> params = this.helper
  //            .buildZuulRequestQueryParams(request);
  //    String verb = getVerb(request);
  //    InputStream requestEntity = getRequestBody(request);
  //    if (request.getContentLength() < 0) {
  //      context.setChunkedRequestBody();
  //    }
  //
  //    this.helper.addIgnoredHeaders();
  //    CloseableHttpClient httpClient = null;
  //    HttpResponse response = null;
  //
  //    try {
  //      httpClient  = HttpClients.createDefault();
  //      response = forward(httpClient, verb, route, request, headers,
  //              params, requestEntity);
  //      setResponse(response);
  //    }
  //    catch (Exception ex ) {
  //      ex.printStackTrace();
  //
  //    }
  //    finally{
  //      try {
  //        httpClient.close();
  //      }
  //      catch(IOException ex){}
  //    }
  //  }

  private String buildRouteString(String oldEndpoint, String newEndpoint, String serviceName) {
    int index = oldEndpoint.indexOf(serviceName);

    String strippedRoute = oldEndpoint.substring(index + serviceName.length());
    System.out.println("Target route: " + String.format("%s/%s", newEndpoint, strippedRoute));
    return String.format("%s/%s", newEndpoint, strippedRoute);
  }

  //  private String getVerb(HttpServletRequest request) {
  //    String sMethod = request.getMethod();
  //    return sMethod.toUpperCase();
  //  }

  //  private HttpHost getHttpHost(URL host) {
  //    HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(),
  //            host.getProtocol());
  //    return httpHost;
  //  }
  //
  //  private Header[] convertHeaders(MultiValueMap<String, String> headers) {
  //    List<Header> list = new ArrayList<>();
  //    for (String name : headers.keySet()) {
  //      for (String value : headers.get(name)) {
  //        list.add(new BasicHeader(name, value));
  //      }
  //    }
  //    return list.toArray(new BasicHeader[0]);
  //  }
  //
  //  private HttpResponse forwardRequest(HttpClient httpclient, HttpHost httpHost,
  //                                      HttpRequest httpRequest) throws IOException {
  //    return httpclient.execute(httpHost, httpRequest);
  //  }
  //
  //
  //  private MultiValueMap<String, String> revertHeaders(Header[] headers) {
  //    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
  //    for (Header header : headers) {
  //      String name = header.getName();
  //      if (!map.containsKey(name)) {
  //        map.put(name, new ArrayList<String>());
  //      }
  //      map.get(name).add(header.getValue());
  //    }
  //    return map;
  //  }
  //
  //  private InputStream getRequestBody(HttpServletRequest request) {
  //    InputStream requestEntity = null;
  //    try {
  //      requestEntity = request.getInputStream();
  //    }
  //    catch (IOException ex) {
  //      // no requestBody is ok.
  //    }
  //    return requestEntity;
  //  }
  //
  //  private void setResponse(HttpResponse response) throws IOException {
  //    this.helper.setResponse(response.getStatusLine().getStatusCode(),
  //            response.getEntity() == null ? null : response.getEntity().getContent(),
  //            revertHeaders(response.getAllHeaders()));
  //  }
  //
  //  private HttpResponse forward(HttpClient httpclient, String verb, String uri,
  //                               HttpServletRequest request, MultiValueMap<String, String>
  // headers,
  //                               MultiValueMap<String, String> params, InputStream requestEntity)
  //          throws Exception {
  //    Map<String, Object> info = this.helper.debug(verb, uri, headers, params,
  //            requestEntity);
  //    URL host = new URL( uri );
  //    HttpHost httpHost = getHttpHost(host);
  //
  //    HttpRequest httpRequest;
  //    int contentLength = request.getContentLength();
  //    InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength,
  //            request.getContentType() != null
  //                    ? ContentType.create(request.getContentType()) : null);
  //    switch (verb.toUpperCase()) {
  //      case "POST":
  //        HttpPost httpPost = new HttpPost(uri);
  //        httpRequest = httpPost;
  //        httpPost.setEntity(entity);
  //        break;
  //      case "PUT":
  //        HttpPut httpPut = new HttpPut(uri);
  //        httpRequest = httpPut;
  //        httpPut.setEntity(entity);
  //        break;
  //      case "PATCH":
  //        HttpPatch httpPatch = new HttpPatch(uri );
  //        httpRequest = httpPatch;
  //        httpPatch.setEntity(entity);
  //        break;
  //      default:
  //        httpRequest = new BasicHttpRequest(verb, uri);
  //
  //    }
  //    try {
  //      httpRequest.setHeaders(convertHeaders(headers));
  //      HttpResponse zuulResponse = forwardRequest(httpclient, httpHost, httpRequest);
  //
  //      return zuulResponse;
  //    }
  //    finally {
  //    }
  //  }
}
