package com.spmia.licenseservice.common.uilts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class UserContextFilter implements Filter {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;

    UserContextHolder.getContext()
        .setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
    UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
    UserContextHolder.getContext()
        .setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
    UserContextHolder.getContext().setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

    log.debug("Correlation id : {}", UserContextHolder.getContext().getCorrelationId());

    chain.doFilter(request, response);
  }
}
