package com.spmia.licenseservice.common.hystrix;

import com.spmia.licenseservice.common.uilts.UserContext;
import com.spmia.licenseservice.common.uilts.UserContextHolder;

import java.util.concurrent.Callable;

public class DelegatingUserContextCallable<V> implements Callable<V> {

  private final Callable<V> delegate;
  private UserContext originalUserContext;

  /**
   * 설명
   *
   * <pre>
   *     - 사용자 정의 Callable 클래스에 Hystrix 로 보호된 코드를 호출하는 원본 Callable 클래스와 부모 Thread 에서 받은 UserContext 를 전달한다.
   * </pre>
   *
   * @param delegate
   * @param userContext
   */
  public DelegatingUserContextCallable(Callable<V> delegate, UserContext userContext) {
    this.delegate = delegate;
    this.originalUserContext = userContext;
  }

  /**
   * {@code @HystrixCommand} 메서드를 보호하기 전에 호출되는 Call 함수
   *
   * @return
   * @throws Exception
   */
  @Override
  public V call() throws Exception {
    // UserContext 설정, UserContext 를 저장하는 ThreadLocal 변수는 Hystrix 가 보호되는 메소드를 실행하는 thread 에 연결된다.
    UserContextHolder.setContext(originalUserContext);

    try {
      // UserContext 가 설정되면, LicenseService.getLicenseByOrg() 같은 Hystrix 가 보호되는 메소드의 call() 를 호출한다.
      return delegate.call();
    } finally {
      this.originalUserContext = null;
    }
  }

  public static <V> Callable<V> create(Callable<V> delegate, UserContext userContext) {
    return new DelegatingUserContextCallable<>(delegate, userContext);
  }
}
