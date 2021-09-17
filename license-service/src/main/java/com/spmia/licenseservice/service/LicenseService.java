package com.spmia.licenseservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.spmia.licenseservice.common.discovery.clients.OrganizationFeignClient;
import com.spmia.licenseservice.common.discovery.clients.OrganizationRestTemplateClient;
import com.spmia.licenseservice.common.model.License;
import com.spmia.licenseservice.common.model.Organization;
import com.spmia.licenseservice.common.model.ServiceConfig;
import com.spmia.licenseservice.common.uilts.UserContextHolder;
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

//  private final OrganizationDiscoveryClient discoveryClient;
  private final OrganizationRestTemplateClient restTemplateClient;
  private final OrganizationFeignClient feignClient;

  private final ServiceConfig config;

  public LicenseService(
      LicenseRepository licenseRepository,
//      OrganizationDiscoveryClient discoveryClient,
      OrganizationRestTemplateClient restTemplateClient,
      OrganizationFeignClient feignClient,
      ServiceConfig config) {

    this.licenseRepository = licenseRepository;
//    this.discoveryClient = discoveryClient;
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
        return restTemplateClient.getOrganization(organizationId);
//    return feignClient.getOrganization(organizationId);
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
   * ! 주의
   *
   * <pre>
   *     - 이 예시는 study 를 위해 하드 코딩한 것
   *     - 원래라면, Spring Cloud Config Server 에 저장하여, recompile 이나 redeploy 없이 instance 만 재시작하면 되게 해야한다.
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
      },
      commandProperties = {
        // Hystrix 가 호출 차단 고려 시간대 동안 연속 호출 횟수 제어
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
        // 회초 차단기를 차단하고 나서 requestVolumeThreshold 만큼 호출 한 후 timeout or throw exception 시 http 500 반환
        // 등으로 실패해야 하는 호출 비율
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
        // 차단되고 나서 hystrix 가 서비스의 회복 상태를 확인할때까지 대기 시간 간격
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
        // 회로 차단기 동작 제어 - Hystrix 가 서비스 호출 문제를 모니터할 시간 간격  default : 10s
        @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000"),
        // 회로 차단기 동작 제어 - metrics.rollingStats.timeInMilliseconds 값 동안 통계를 수집할 횟수
        @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "5")
      })
  public List<License> getLicenseByOrg(String organizationId) {
    // ! 기본적으로 Hystrix 는 부모 thread 의 context 를 Hystrix 명령이 관리하는 thread 로 전파시키지 않는다.
    log.debug("Correlation id : {}", UserContextHolder.getContext().getCorrelationId());

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
