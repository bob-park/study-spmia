package com.spmia.licenseservice.common.discovery.clients;

import com.spmia.licenseservice.common.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "organizationservice")
public interface OrganizationFeignClient {

  @GetMapping(
      path = "v1/organizations/{organizationId}",
      consumes = MediaType.APPLICATION_JSON_VALUE) // end-point 와 action 정의
  Organization getOrganization(@PathVariable String organizationId);
}
