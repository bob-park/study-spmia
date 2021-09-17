package com.spmia.specialrouteservice.service;

import com.spmia.specialrouteservice.commons.exception.NoRouteFound;
import com.spmia.specialrouteservice.commons.model.AbTestingRoute;
import com.spmia.specialrouteservice.repository.AbTestingRouteRepository;
import org.springframework.stereotype.Service;

@Service
public class AbTestingRouteService {

  private final AbTestingRouteRepository abTestingRouteRepository;

  public AbTestingRouteService(AbTestingRouteRepository abTestingRouteRepository) {
    this.abTestingRouteRepository = abTestingRouteRepository;
  }

  public AbTestingRoute getRoute(String serviceName) {
    AbTestingRoute route = abTestingRouteRepository.findByServiceName(serviceName);

    if (route == null) {
      throw new NoRouteFound();
    }

    return route;
  }

  public void saveAbTestingRoute(AbTestingRoute route) {

    abTestingRouteRepository.save(route);
  }

  public void updateRouteAbTestingRoute(AbTestingRoute route) {
    abTestingRouteRepository.save(route);
  }

  public void deleteRoute(AbTestingRoute route) {
    abTestingRouteRepository.delete(route);
  }
}
