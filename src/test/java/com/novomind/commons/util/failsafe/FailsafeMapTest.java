package com.novomind.commons.util.failsafe;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailsafeMapTest extends AbstractFailsafeTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeMapTest.class);

  @Test
  public void map() {
    // Arrange
    final String expectedElementForException = "this throws an exception";
    final String expectedDefault = "instead of exception";

    final BiConsumer<String, Throwable> exceptionConsumerMock = mock(BiConsumer.class);
    final Supplier<String> expectedDefaultSupplierMock = mock(Supplier.class);
    when(expectedDefaultSupplierMock.get()).thenReturn(expectedDefault);

    final Function<String, String> mapper = FailsafeStream.<String, String, Exception> failsafeForMap(LOGGER)
        .map(element -> invokeForException(element, expectedElementForException))
        .useExceptionConsumer(exceptionConsumerMock)
        .useDefaultValueSupplier(expectedDefaultSupplierMock)
        .perform();

    

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    final List<String> result = streamWithExceptionProvoker
        .map(mapper)
        .collect(Collectors.toList());

    // Assert
    assertThat(result.size(), is(21));
    assertTrue(result.contains(expectedDefault));

    checkResultContainsExpectedElementsAndExceptionElementIsHandled(result, expectedElementForException, exceptionConsumerMock);
  }
}