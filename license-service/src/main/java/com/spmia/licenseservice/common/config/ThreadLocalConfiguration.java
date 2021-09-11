package com.spmia.licenseservice.common.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.spmia.licenseservice.common.hystrix.ThreadLocalAwareStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;

@Configuration
public class ThreadLocalConfiguration {

  private final HystrixConcurrencyStrategy existingConcurrencyStrategy;

  public ThreadLocalConfiguration(
      @Nullable HystrixConcurrencyStrategy existingConcurrencyStrategy) {
    this.existingConcurrencyStrategy = existingConcurrencyStrategy;
  }

  @PostConstruct
  public void init() {
    // ! hystrix 는 하나의 HystrixConcurrencyStrategy 만 허용한다.
    // 기존 hystrix plugin 의 ref 유지
    // 새로운 병행성 전략을 등록하기 때문에 모든 Hystrix component 를 가져와 hystrix plugin 을 재설정한다.
    HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
    HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
    HystrixPropertiesStrategy propertiesStrategy =
        HystrixPlugins.getInstance().getPropertiesStrategy();
    HystrixCommandExecutionHook commandExecutionHook =
        HystrixPlugins.getInstance().getCommandExecutionHook();

    HystrixPlugins.reset();

    // HystrixConcurrencyStragy(ThreadLocalAwareStrategy) 를 Hystrix plugin 에 등록
    HystrixPlugins.getInstance()
        .registerConcurrencyStrategy(new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
    // Hystrix plugin 이 사용하는 모든 Hystrix component 를 재등록 해준다.
    HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
    HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
    HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
    HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
  }
}
