package com.novomind.commons.util.failsafe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

abstract class FailsafeOperationBuilderWithLogger<O, T> {
  protected Logger logger;

  O useLogger(@Nullable final Logger logger) {
    this.logger = logger;
    return (O) this;
  }

  /**
   * Accessor for the necessary {@link FunctionalInterface} performing the essential operation of the built Failsafe-Object.
   */
  @Nullable
  protected abstract Object getPerformingElement();

  @Nonnull
  protected abstract FailsafeOperation<T> build();

  /**
   * This is a convenience method for {@code}{@link #build()}->{@link FailsafeOperation#create()}{@code}.
   */
  public T perform() {
    if (getPerformingElement() == null) {
      throw new IllegalStateException("No performing operation set!");
    }

    return build().create();
  }
}
