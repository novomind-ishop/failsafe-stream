package com.novomind.commons.util.failsafe;

import javax.annotation.Nonnull;

public interface FailsafeOperation<T> {
  @Nonnull
  T create();
}
