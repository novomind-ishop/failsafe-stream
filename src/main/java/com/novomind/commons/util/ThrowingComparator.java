package com.novomind.commons.util;

@FunctionalInterface
public interface ThrowingComparator<A, E extends Exception> {
  int compare(A valueA, A valueB) throws E;
}
