package com.spmia.specialrouteservice.commons.utils;

import org.springframework.util.Assert;

public class UserContextHolder {

  private static final ThreadLocal<UserContext> USER_CONTEXT =
      new ThreadLocal<>(); // 정적 ThreadLocal

  public static UserContext getContext() {
    UserContext context = USER_CONTEXT.get();

    if (context == null) {
      context = createEmptyContext();
      USER_CONTEXT.set(context);
    }

    return USER_CONTEXT.get();
  }

  public static void setContext(UserContext context) {
    Assert.notNull(context, "Only non0null UserContext instances are permitted");
    USER_CONTEXT.set(context);
  }

  private static UserContext createEmptyContext() {
    return new UserContext();
  }

  public void unload() {
    USER_CONTEXT.remove();
  }
}
