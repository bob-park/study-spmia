package com.spmia.licenseservice.common.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig {

  @Value("${example.property}")
  private String exampleProperty;

  @Value("${example.product-name}")
  private String exampleProductName;

  public String getExampleProperty() {
    return exampleProperty;
  }

  public String getExampleProductName() {
    return exampleProductName;
  }
}
