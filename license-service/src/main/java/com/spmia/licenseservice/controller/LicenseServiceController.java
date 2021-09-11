package com.spmia.licenseservice.controller;

import com.spmia.licenseservice.common.model.License;
import com.spmia.licenseservice.common.uilts.UserContextHolder;
import com.spmia.licenseservice.service.LicenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "v1/organizations/{organizationId}/licenses")
public class LicenseServiceController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final LicenseService licenseService;

  public LicenseServiceController(LicenseService licenseService) {
    this.licenseService = licenseService;
  }

  @GetMapping
  public List<License> getLicenses(@PathVariable String organizationId) {
    log.debug("Correlation id : {}", UserContextHolder.getContext().getCorrelationId());
    return licenseService.getLicenseByOrg(organizationId);
  }

  @GetMapping(path = "/{licenseId}")
  public License getLicenses(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {

    return licenseService.getLicense(organizationId, licenseId);
  }

  @PutMapping(value = "{licenseId}")
  public String updateLicenses(@PathVariable("licenseId") String licenseId) {
    return String.format("This is the Update");
  }

  @PostMapping(value = "{licenseId}")
  public String saveLicenses(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {
    licenseService.saveLicense(organizationId, licenseId);
    return licenseId;
  }

  @DeleteMapping(value = "{licenseId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public String deleteLicenses(@PathVariable("licenseId") String licenseId) {
    return String.format("This is the Delete");
  }

  @GetMapping(path = "{licenseId}/{clientType}")
  public License getLicenseWithClient(
      @PathVariable String organizationId,
      @PathVariable String licenseId,
      @PathVariable String clientType) {
    return licenseService.getLicense(organizationId, licenseId, clientType);
  }
}
