package com.novomind.commons.util;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {
  void run() throws E;
}
