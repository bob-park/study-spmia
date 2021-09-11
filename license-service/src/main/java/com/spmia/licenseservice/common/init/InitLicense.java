package com.spmia.licenseservice.common.init;

import com.spmia.licenseservice.common.model.License;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
public class InitLicense {

  private final InitService initService;

  public InitLicense(InitService initService) {
    this.initService = initService;
  }

  @PostConstruct
  public void init() {
    initService.init();
  }

  @Component
  @Transactional
  static class InitService {

    private final EntityManager em;

    public InitService(EntityManager em) {
      this.em = em;
    }

    public void init() {

      for (int i = 0; i < 10; i++) {

        String organizationId = i % 2 == 0 ? "1" : "2";

        License license =
            new License()
                .withId(i + "")
                .withProductName("product-" + i)
                .withOrganizationId(organizationId);

        em.persist(license);
      }
    }
  }
}
