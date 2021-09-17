package com.spmia.specialrouteservice.commons.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class UserContextInterceptor implements ClientHttpRequestInterceptor {

  /**
   * RequestTemplate 실제 HTTP 서비스 호출을 하기전에 intercept() 를 호출
   *
   * @param request
   * @param body
   * @param execution
   * @return
   * @throws IOException
   */
  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

    // 서비스 호출을 위해 준비한 http 요청 헤더를 가져와 UserContext 에 저장된 상관관계 ID 를 추가한다.
    HttpHeaders headers = request.getHeaders();
    headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
    headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken());


    return execution.execute(request, body);
  }
}
