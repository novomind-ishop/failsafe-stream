package com.novomind.commons.util.failsafe;

import static com.novomind.commons.util.failsafe.FailsafeTestUtils.getFirstBunch;
import static com.novomind.commons.util.failsafe.FailsafeTestUtils.getLastBunch;
import static com.novomind.commons.util.failsafe.FailsafeTestUtils.getStreamOf21ElementsFirstBunchExceptionLastBunch;
import static com.novomind.commons.util.failsafe.FailsafeTestUtils.invokeForException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.novomind.commons.util.TriConsumer;

public class FailsafeSortedTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeSortedTest.class);

  @Test
  public void compare_defaultTrue() {
    // Arrange
    final String expectedElementForException = "this throws an exception";

    final TriConsumer<String, String, Throwable> exceptionConsumerMock = mock(TriConsumer.class);
    final Supplier<Integer> expectedDefaultSupplierMock = mock(Supplier.class);
    when(expectedDefaultSupplierMock.get()).thenReturn(1);

    final Multimap<String, String> effectiveRecombination = HashMultimap.create();
    final Comparator<String> sorter = FailsafeStream.<String, Exception> failsafeForSorted(LOGGER)
        .compare((element1, element2) -> {
          effectiveRecombination.put(element1, element2);
          return invokeForException(element1, expectedElementForException).compareTo(element2);
        })
        .useExceptionConsumer(exceptionConsumerMock)
        .useDefaultValueSupplier(expectedDefaultSupplierMock)
        .perform();

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    final List<String> result = streamWithExceptionProvoker
        .sorted(sorter)
        .collect(Collectors.toList());

    // Assert
    assertThat(result)
        .hasSize(21);

    assertThat(result).containsAll(getFirstBunch().collect(Collectors.toList()));
    assertThat(result).contains(expectedElementForException);
    assertThat(result).containsAll(getLastBunch().collect(Collectors.toList()));

    // re-combinations over all elements
    final ArgumentCaptor<String> secondElementCaptor = ArgumentCaptor.forClass(String.class);
    verify(exceptionConsumerMock, atLeast(1)).accept(eq(expectedElementForException), secondElementCaptor.capture(),
        any(Exception.class));

    final List<String> allValues = secondElementCaptor.getAllValues();
    final Collection<String> actualElements = effectiveRecombination.get(expectedElementForException);
    assertThat(allValues).hasSize(actualElements.size());
    assertThat(allValues).containsAll(actualElements);
  }

  @Test
  public void compare_defaultNotSetIsZero() {
    // Arrange
    final String expectedElementForException = "this throws an exception";

    final TriConsumer<String, String, Throwable> exceptionConsumerMock = mock(TriConsumer.class);
    final Multimap<String, String> effectiveRecombination = HashMultimap.create();
    final Comparator<String> sorter = FailsafeStream.<String, Exception> failsafeForSorted(LOGGER)
        .compare((element1, element2) -> {
          effectiveRecombination.put(element1, element2);
          return invokeForException(element1, expectedElementForException).compareTo(element2);
        })
        .useExceptionConsumer(exceptionConsumerMock)
        // no default is false
        .perform();

    final Stream<String> streamWithExceptionProvoker =
        getStreamOf21ElementsFirstBunchExceptionLastBunch(expectedElementForException);

    // Act
    final List<String> result = streamWithExceptionProvoker
        .sorted(sorter)
        .collect(Collectors.toList());

    // Assert
    assertThat(result).hasSize(21);

    assertThat(result).containsAll(getFirstBunch().collect(Collectors.toList()));
    assertThat(result).contains(expectedElementForException);
    assertThat(result).containsAll(getLastBunch().collect(Collectors.toList()));

    // re-combinations over all elements
    final ArgumentCaptor<String> secondElementCaptor = ArgumentCaptor.forClass(String.class);
    verify(exceptionConsumerMock, atLeast(1)).accept(eq(expectedElementForException), secondElementCaptor.capture(),
        any(Exception.class));

    final List<String> allValues = secondElementCaptor.getAllValues();
    final Collection<String> actualElements = effectiveRecombination.get(expectedElementForException);
    assertThat(allValues)
        .hasSize(actualElements.size());
    assertThat(allValues).containsAll(actualElements);
  }
}