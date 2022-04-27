package com.novomind.commons.util;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {
  void consume(T input) throws E;
}
