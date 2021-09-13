package com.spmia.specialrouteservice.repository;

import com.spmia.specialrouteservice.commons.model.AbTestingRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbTestingRouteRepository extends JpaRepository<AbTestingRoute, String> {
  AbTestingRoute findByServiceName(String serviceName);
}
