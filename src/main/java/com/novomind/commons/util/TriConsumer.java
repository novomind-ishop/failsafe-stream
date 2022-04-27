package com.novomind.commons.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
  void accept(A valueA, B valueB, C valueC);
}
