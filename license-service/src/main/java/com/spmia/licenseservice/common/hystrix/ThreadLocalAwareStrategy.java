package com.spmia.licenseservice.common.hystrix;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.spmia.licenseservice.common.uilts.UserContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

// HystrixConcurrencyStrategy class 를 상속한다.
public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {

  private HystrixConcurrencyStrategy existingConcurrencyStrategy;

  /**
   * 설명
   *
   * <pre>
   *     - Spring Cloud 에서 미리 정의한 병행성 클래스를 이 HystrixConcurrencyStrategy 클래스의 생성자에 전달한다.
   * </pre>
   *
   * @param existingConcurrencyStrategy
   */
  public ThreadLocalAwareStrategy(HystrixConcurrencyStrategy existingConcurrencyStrategy) {
    this.existingConcurrencyStrategy = existingConcurrencyStrategy;
  }

  /**
   * 일부 메서드의 재정이가 필요
   *
   * <pre>
   *     - existingConcurrencyStrategy 메서드 구현을 호출하거나 부모 HystrixConcurrencyStrategy 메서드를 호출 한다.
   * </pre>
   *
   * @param maxQueueSize
   * @return
   */
  @Override
  public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
    return existingConcurrencyStrategy != null
        ? existingConcurrencyStrategy.getBlockingQueue(maxQueueSize)
        : super.getBlockingQueue(maxQueueSize);
  }

  @Override
  public <T> Callable<T> wrapCallable(Callable<T> callable) {
    return existingConcurrencyStrategy != null
        ? existingConcurrencyStrategy.wrapCallable(
            DelegatingUserContextCallable.create(
                callable, UserContextHolder.getContext())) // UserContext 를 설정할 Callable 구현체를 주입한다.
        : super.wrapCallable(
            DelegatingUserContextCallable.create(callable, UserContextHolder.getContext()));
  }
}
