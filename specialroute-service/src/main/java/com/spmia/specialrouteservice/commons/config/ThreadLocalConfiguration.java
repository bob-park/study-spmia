package com.spmia.specialrouteservice.commons.config;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.spmia.specialrouteservice.commons.hystrix.ThreadLocalAwareStrategy;
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
    // Keeps references of existing Hystrix plugins.
    HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
    HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
    HystrixPropertiesStrategy propertiesStrategy =
        HystrixPlugins.getInstance().getPropertiesStrategy();
    HystrixCommandExecutionHook commandExecutionHook =
        HystrixPlugins.getInstance().getCommandExecutionHook();

    HystrixPlugins.reset();

    // Registers existing plugins excepts the Concurrent Strategy plugin.
    HystrixPlugins.getInstance()
        .registerConcurrencyStrategy(new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
    HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
    HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
    HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
    HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
  }
}
