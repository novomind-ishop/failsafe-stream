package com.novomind.commons.util.failsafe;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailsafeTestTest extends AbstractFailsafeTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeTestTest.class);

  @Test
  public void filter_defaultTrue() {
    // Arrange
    final String expectedElementForException = "this throws an exception";

    final BiConsumer<String, Throwable> exceptionConsumerMock = mock(BiConsumer.class);
    final BooleanSupplier expectedDefaultSupplierMock = mock(BooleanSupplier.class);
    when(expectedDefaultSupplierMock.getAsBoolean()).thenReturn(true);

    final Predicate<String> tester = FailsafeStream.<String, Exception> failsafeForTest(LOGGER)
        .filter(element -> invokeForException(element, expectedElementForException).equals(element))
        .useExceptionConsumer(exceptionConsumerMock)
        .useDefaultValueSupplier(expectedDefaultSupplierMock)
        .perform();

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    final List<String> result = streamWithExceptionProvoker
        .filter(tester)
        .collect(Collectors.toList());

    // Assert
    assertThat(result.size(), is(21));

    assertTrue(result.contains(expectedElementForException));

    checkResultContainsExpectedElementsAndExceptionElementIsHandled(result, expectedElementForException, exceptionConsumerMock);
  }

  @Test
  public void filter_defaultNotSetIsFalse() {
    // Arrange
    final String expectedElementForException = "this throws an exception";

    final BiConsumer<String, Throwable> exceptionConsumerMock = mock(BiConsumer.class);

    final Predicate<String> tester = FailsafeStream.<String, Exception> failsafeForTest(LOGGER)
        .filter(element -> invokeForException(element, expectedElementForException).equals(element))
        .useExceptionConsumer(exceptionConsumerMock)
        // no default is false
        .perform();

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    final List<String> result = streamWithExceptionProvoker
        .filter(tester)
        .collect(Collectors.toList());

    // Assert
    assertThat(result.size(), is(20));

    checkResultContainsExpectedElementsAndExceptionElementIsHandled(result, expectedElementForException, exceptionConsumerMock);
  }
}