package com.novomind.commons.util.failsafe;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailsafeAcceptTest extends AbstractFailsafeTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeAcceptTest.class);

  @Test
  public void accept() {
    // Arrange
    final String expectedElementForException = "this throws an exception";

    final BiConsumer<String, Throwable> exceptionConsumerMock = mock(BiConsumer.class);
    final BooleanSupplier expectedDefaultSupplierMock = mock(BooleanSupplier.class);
    when(expectedDefaultSupplierMock.getAsBoolean()).thenReturn(true);

    final ArrayList<String> consumed = new ArrayList<>();

    final Consumer<String> tester = FailsafeStream.<String, Exception> failsafeForAccept(LOGGER)
        .consume(element -> {
          invokeForException(element, expectedElementForException);
          consumed.add(element);
        })
        .useExceptionConsumer(exceptionConsumerMock)
        .perform();

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    streamWithExceptionProvoker
        .forEach(tester);

    // Assert
    checkResultContainsExpectedElementsAndExceptionElementIsHandled(consumed, expectedElementForException, exceptionConsumerMock);
  }
}