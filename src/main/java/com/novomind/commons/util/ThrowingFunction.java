package com.novomind.commons.util;

@FunctionalInterface
public interface ThrowingFunction<A, B, E extends Exception> {
  B apply(A input) throws E;
}
