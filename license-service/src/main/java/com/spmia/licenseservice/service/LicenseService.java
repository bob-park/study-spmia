package com.spmia.licenseservice.service;

import com.spmia.licenseservice.common.discovery.clients.OrganizationDiscoveryClient;
import com.spmia.licenseservice.common.discovery.clients.OrganizationFeignClient;
import com.spmia.licenseservice.common.discovery.clients.OrganizationRestTemplateClient;
import com.spmia.licenseservice.common.model.License;
import com.spmia.licenseservice.common.model.Organization;
import com.spmia.licenseservice.common.model.ServiceConfig;
import com.spmia.licenseservice.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LicenseService {

  private final LicenseRepository licenseRepository;

  private final OrganizationDiscoveryClient discoveryClient;
  private final OrganizationRestTemplateClient restTemplateClient;
  private final OrganizationFeignClient feignClient;

  private final ServiceConfig config;

  public LicenseService(
      LicenseRepository licenseRepository,
      OrganizationDiscoveryClient discoveryClient,
      OrganizationRestTemplateClient restTemplateClient,
      OrganizationFeignClient feignClient,
      ServiceConfig config) {

    this.licenseRepository = licenseRepository;
    this.discoveryClient = discoveryClient;
    this.restTemplateClient = restTemplateClient;
    this.feignClient = feignClient;
    this.config = config;
  }

  public License getLicense(String organizationId, String licenseId) {

    License license = licenseRepository.findByOrganizationIdAndId(organizationId, licenseId);

    return license.withComment(config.getExampleProperty());
  }

  public License getLicense(String organizationId, String licenseId, String clientType) {

    License license = licenseRepository.findByOrganizationIdAndId(organizationId, licenseId);

    Organization org = retrieveOrgInfo(organizationId, clientType);

    return license.withComment(config.getExampleProperty()).withOrganizationId(org.getId());
  }

  private Organization retrieveOrgInfo(String organizationId, String clientType) {
    //    return discoveryClient.getOrganization(organizationId);
    //    return restTemplateClient.getOrganization(organizationId);
    return feignClient.getOrganization(organizationId);
  }

  @Transactional
  public void saveLicense(String organizationId, String licenseId) {

    License license =
        new License()
            .withId(licenseId)
            .withOrganizationId(organizationId)
            .withProductName(config.getExampleProductName());

    licenseRepository.save(license);
  }

  public void updateLicense(License license) {
    license.withId(UUID.randomUUID().toString());

    licenseRepository.save(license);
  }

  public void deleteLicense(License license) {}
}
