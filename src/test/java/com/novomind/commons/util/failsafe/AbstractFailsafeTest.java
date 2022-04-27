package com.novomind.commons.util.failsafe;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.google.common.collect.Streams;

public class AbstractFailsafeTest {

  @Nonnull
  protected Stream<String> getStreamOf21ElementsFirstBunchExceptionLastBunch(
      @Nonnull final String expectedElementForException) {
    final Collection<String> firstBunch = getFirstBunch();
    final Collection<String> lastBunch = getLastBunch();
    return Streams.concat(
        firstBunch.stream(),
        Stream.of(expectedElementForException),
        lastBunch.stream()
    );
  }

  @Nonnull
  protected Collection<String> getFirstBunch() {
    return IntStream.rangeClosed(1, 10).mapToObj(Integer::toString).collect(Collectors.toList());
  }

  @Nonnull
  protected List<String> getLastBunch() {
    return IntStream.rangeClosed(11, 20).mapToObj(Integer::toString).collect(Collectors.toList());
  }

  @Nonnull
  protected String invokeForException(@Nonnull final String element, @Nonnull final String expectedElementForException)
      throws Exception {
    if (element.equals(expectedElementForException)) {
      throw new Exception("Expected exception thrown for " + expectedElementForException);
    }

    return element;
  }

  protected void checkResultContainsExpectedElementsAndExceptionElementIsHandled(
      @Nonnull final List<String> result,
      @Nonnull final String expectedElementForException,
      @Nonnull final BiConsumer<String, Throwable> exceptionConsumerMock) {
    assertTrue(result.containsAll(getFirstBunch()));
    assertTrue(result.containsAll(getLastBunch()));
    verify(exceptionConsumerMock).accept(eq(expectedElementForException), any(Exception.class));
  }
}
