package com.novomind.commons.util.failsafe;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

import lombok.Builder;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FailsafeStream {

  @Nonnull
  public static Failsafe.Builder failsafe() {
    return Failsafe.builder();
  }

  @Nonnull
  public static <A, B, E extends Exception> FailsafeMap.Builder<A, B, E> failsafeForMap(@Nonnull final Logger logger) {
    return failsafe()
        .useLogger(logger)
        .forMap();
  }

  @Nonnull
  public static <A, E extends Exception> FailsafeAccept.Builder<A, E> failsafeForAccept(@Nonnull final Logger logger) {
    return failsafe()
        .useLogger(logger)
        .forAccept();
  }

  @Nonnull
  public static <A, E extends Exception> FailsafeSort.Builder<A, E> failsafeForSorted(@Nonnull final Logger logger) {
    return failsafe()
        .useLogger(logger)
        .forSorted();
  }

  @Nonnull
  public static <A, E extends Exception> FailsafeTest.Builder<A, E> failsafeForTest(@Nonnull final Logger logger) {
    return failsafe()
        .useLogger(logger)
        .forTest();
  }

  @Builder(builderClassName = "Builder", buildMethodName = "build")
  public static class Failsafe {

    private Logger logger;

    @Nonnull
    private <A, B, E extends Exception> FailsafeMap.Builder<A, B, E> forMap() {
      return FailsafeMap.<A, B, E> builder()
          .useLogger(logger);
    }

    @Nonnull
    private <E extends Exception, A> FailsafeSort.Builder<A, E> forSorted() {
      return FailsafeSort.<A, E> builder()
          .useLogger(logger);
    }

    @Nonnull
    private <A, E extends Exception> FailsafeTest.Builder<A, E> forTest() {
      return FailsafeTest.<A, E> builder()
          .useLogger(logger);
    }

    @Nonnull
    private <E extends Exception, A> FailsafeAccept.Builder<A, E> forAccept() {
      return FailsafeAccept.<A, E> builder()
          .useLogger(logger);
    }

    public static class Builder {
      /**
       * @deprecated Do not use this method, despite you really don't need to know the errors.
       */
      @Deprecated
      @Nonnull
      public Failsafe silent() {
        logger = null;
        return build();
      }

      @Nonnull
      public Failsafe useLogger(@Nonnull final Logger logger) {
        this.logger = logger;
        return build();
      }
    }
  }
}
