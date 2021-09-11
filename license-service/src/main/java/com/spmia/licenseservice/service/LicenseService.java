package com.spmia.licenseservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.spmia.licenseservice.common.discovery.clients.OrganizationDiscoveryClient;
import com.spmia.licenseservice.common.discovery.clients.OrganizationFeignClient;
import com.spmia.licenseservice.common.discovery.clients.OrganizationRestTemplateClient;
import com.spmia.licenseservice.common.model.License;
import com.spmia.licenseservice.common.model.Organization;
import com.spmia.licenseservice.common.model.ServiceConfig;
import com.spmia.licenseservice.repository.LicenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LicenseService {

  private final Logger log = LoggerFactory.getLogger(getClass());

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

  private Organization retrieveOrgInfo(String organizationId, String clientType) {
    //    return discoveryClient.getOrganization(organizationId);
    //    return restTemplateClient.getOrganization(organizationId);
    return feignClient.getOrganization(organizationId);
  }

  @HystrixCommand
  private Organization getOrganization(String organizationId) {
    return restTemplateClient.getOrganization(organizationId);
  }

  // Hystrix 시뮬레이션
  private void randomlyRunLog() {
    Random rand = new Random();

    int randomNum = rand.nextInt((3 - 1) + 1) + 1;

    if (randomNum == 3) {
      sleep();
    }
  }

  private void sleep() {
    try {
      Thread.sleep(11000); // hystrix 는 default timeout 이 1000 ms 이다.
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @HystrixCommand(
      // hystrix 를 사용자 정의하기 위해 추가한 매개변수를 전달하는 commandProperties
      commandProperties = {
        @HystrixProperty(
            name = "execution.isolation.thread.timeoutInMilliseconds",
            value = "12000") // 회로 차단기의 timeout 설정
      })
  public List<License> getLicenseByOrg(String organizationId) {
    //    log.debug("Correlation id : {}", UserContextHolder.getContext().getCorrelationId());

    randomlyRunLog();

    return licenseRepository.findByOrganizationId(organizationId);
  }
}
