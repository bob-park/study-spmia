package com.spmia.licenseservice.common.discovery.clients;

import com.spmia.licenseservice.common.model.Organization;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

  private final RestTemplate restTemplate;

  public OrganizationRestTemplateClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Organization getOrganization(String organizationId) {
    // * 리본을 지원하는 RestTemplate 의 url 패턴은 다음과 같다.
    // pattern : http://{applicationId}/...
    ResponseEntity<Organization> exchange =
        restTemplate.exchange(
            "http://organizationservice/v1/organizations/{organizationId}",
            HttpMethod.GET,
            null,
            Organization.class,
            organizationId);

    return exchange.getBody();
  }
}
