package com.novomind.commons.util.failsafe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.collect.Streams;

public final class FailsafeTestUtils {

  @Nonnull
  public static Stream<String> getStreamOf21ElementsFirstBunchExceptionLastBunch(
      @Nonnull final String expectedElementForException) {
    final Stream<String> firstBunch = getFirstBunch();
    final Stream<String> lastBunch = getLastBunch();
    return Streams.concat(
        firstBunch,
        Stream.of(expectedElementForException),
        lastBunch
    );
  }

  @Nonnull
  public static Stream<String> getFirstBunch() {
    return IntStream.rangeClosed(1, 10).mapToObj(Integer::toString);
  }

  @Nonnull
  public static Stream<String> getLastBunch() {
    return IntStream.rangeClosed(11, 20).mapToObj(Integer::toString);
  }

  @Nonnull
  public static String invokeForException(@Nonnull final String element, @Nonnull final String expectedElementForException)
      throws Exception {
    if (element.equals(expectedElementForException)) {
      throw new Exception("Expected exception thrown for " + expectedElementForException);
    }

    return element;
  }

  public static void checkResultContainsExpectedElementsAndExceptionElementIsHandled(
      @Nonnull final List<String> result,
      @Nonnull final String expectedElementForException,
      @Nonnull final BiConsumer<String, Throwable> exceptionConsumerMock) {
    assertThat(result).containsAll(getFirstBunch().collect(Collectors.toList()));
    assertThat(result).containsAll(getLastBunch().collect(Collectors.toList()));

    verify(exceptionConsumerMock).accept(eq(expectedElementForException), any(Exception.class));
  }
}
