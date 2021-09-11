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

import java.util.ArrayList;
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

  /**
   * hystrix fallback pattern
   *
   * <p>* 구현 시 해야할 일
   *
   * <pre>
   *     1. @HystrixCommand 에  fallbackMethod 지정
   *     2. fallbackMethod 정의
   * </pre>
   *
   * ! 주의해야할 점
   *
   * <pre>
   *     1. timeout exception 을 잡아내고, error logging 만 한다면 service call 후 try~catch 사용하여 logging block을 넣어도 된다.
   *     2. fallback 기능으로 수행하는 행동을 알고 있어야 한다.
   *        - fallback service 에서 다른 분산 서비스를 호출한다면 @HystrixCommand 로 fallback 을 감싸야 할 수 있다.
   *        - 1차 fallback 의 동일한 장애가 2차 fallback 옵션에도 영향을 줄 수 있다.
   * </pre>
   *
   * @param organizationId
   * @return
   */
  @HystrixCommand(
      // hystrix 를 사용자 정의하기 위해 추가한 매개변수를 전달하는 commandProperties
      //      commandProperties = {
      //        @HystrixProperty(
      //            name = "execution.isolation.thread.timeoutInMilliseconds",
      //            value = "12000") // 회로 차단기의 timeout 설정
      //      }
      fallbackMethod = "buildFallbackLicenseList",
      threadPoolKey = "licenseByOrgThreadPool", // thread pool 의 고유 이름
      // thread pool 동작 정의 - 없는 경우 default 로 생성됨
      threadPoolProperties = {
        @HystrixProperty(name = "coreSize", value = "30"), // thread 개수
        // value 에 따른 동작
        // -1 :  SynchronousQueue 사용됨 - thread poll 에서 가용 thread 개수보다 더 많은 요청을 처리할 수 없다.
        // > 1 : LinkedBlockQueue 사용됨 - 모든 thread 가 요청을 처리하고 있어도, 더 많은 요청을 queue 에 넣을 수 있다.
        // ! Thread Poll 개수는 다음과 같은 공식으로 제안된다.
        // (service 가 정상일때 최고점에서 초당 요청수 * 99 백분위 수 지연시간(단위: 초)) + overhead 를 대비한 소량의 추가 thread
        @HystrixProperty(name = "maxQueueSize", value = "10") // request queue 수
      })
  public List<License> getLicenseByOrg(String organizationId) {
    //    log.debug("Correlation id : {}", UserContextHolder.getContext().getCorrelationId());

    randomlyRunLog();

    return licenseRepository.findByOrganizationId(organizationId);
  }

  /**
   * fallback method
   *
   * @param organizationId
   * @return
   */
  private List<License> buildFallbackLicenseList(String organizationId) {
    List<License> fallbackList = new ArrayList<>();

    License license =
        new License()
            .withId("000000-00-00000")
            .withOrganizationId(organizationId)
            .withProductName("Sorry no licensing information currently available");

    fallbackList.add(license);

    return fallbackList;
  }
}
