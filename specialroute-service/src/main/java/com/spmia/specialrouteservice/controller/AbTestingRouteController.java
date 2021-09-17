package com.spmia.specialrouteservice.controller;

import com.spmia.specialrouteservice.commons.model.AbTestingRoute;
import com.spmia.specialrouteservice.service.AbTestingRouteService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/route/")
public class AbTestingRouteController {

  private final AbTestingRouteService routeService;

  public AbTestingRouteController(AbTestingRouteService routeService) {
    this.routeService = routeService;
  }

  @RequestMapping(value = "abtesting/{serviceName}", method = RequestMethod.GET)
  public AbTestingRoute abstestings(@PathVariable("serviceName") String serviceName) {

    return routeService.getRoute(serviceName);
  }
}
