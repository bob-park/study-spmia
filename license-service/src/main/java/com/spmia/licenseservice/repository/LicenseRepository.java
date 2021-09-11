package com.spmia.licenseservice.repository;

import com.spmia.licenseservice.common.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, String> {

  List<License> findByOrganizationId(String organizationId);

  License findByOrganizationIdAndId(String organizationId, String licenseId);
}
