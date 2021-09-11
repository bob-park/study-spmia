package com.spmia.organizationservice.controller;

import com.spmia.organizationservice.common.model.Organization;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/organizations")
public class OrganizationController {

  @GetMapping(path = "{organizationId}")
  public Organization getOrganization(@PathVariable String organizationId) {
    Organization org = new Organization();

    org.setId("1");
    org.setContactName("name");
    org.setContactEmail("name@name.com");
    org.setName("name");

    return org;
  }
}
