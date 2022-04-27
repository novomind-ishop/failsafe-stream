package com.novomind.commons.util.failsafe;

public interface FailsafeOperation<T> {
  T create();
}
