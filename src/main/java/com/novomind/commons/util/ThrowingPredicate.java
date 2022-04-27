package com.novomind.commons.util;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Exception> {
  boolean test(T input) throws E;
}
