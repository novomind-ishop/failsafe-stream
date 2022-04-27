package com.novomind.commons.util.failsafe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailsafeStreamTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeStreamTest.class);

  @Test
  public void failsafe_withLogger() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeStream.Failsafe.Builder failsafeBuilder = FailsafeStream.failsafe();
    final FailsafeStream.Failsafe failsafe = failsafeBuilder
        .logger(LOGGER)
        .build();

    // Assert
    assertNotNull(failsafeBuilder);
    assertNotNull(failsafe);

    checkLoggerSetWithValue(failsafe, LOGGER);
  }

  @Test
  public void failsafe_withoutLogger() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeStream.Failsafe.Builder failsafeBuilder = FailsafeStream.failsafe();
    final FailsafeStream.Failsafe failsafe = failsafeBuilder
        .build();

    // Assert
    assertNotNull(failsafeBuilder);
    assertNotNull(failsafe);

    checkLoggerSetWithValue(failsafe, null);
  }

  @Test
  public void failsafeForMap_noPerformingMethodSet_fails() {
    // Act
    final FailsafeMap.Builder<String, String, Exception> failsafeBuilder = FailsafeStream.failsafeForMap(LOGGER);

    IllegalStateException expectedException;
    try {
      failsafeBuilder.build().create();
      fail("Expected exception to be thrown here, since the performing method reference was not set!");
      expectedException = null;
    } catch (final IllegalStateException exception) {
      expectedException = exception;
    }
    // Assert
    assertNotNull(failsafeBuilder);
    assertThat(expectedException.getMessage(), StringContains.containsString("method reference not set"));
    assertThat(expectedException.getMessage(), StringContains.containsString("not sufficiently initialized"));
  }

  @Test
  public void failsafeForMap_allSet() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeMap.Builder<String, String, Exception> failsafeBuilder = FailsafeStream.failsafeForMap(LOGGER);

    final FailsafeMap<String, String, Exception> failsafeMap = failsafeBuilder
        .useDefaultValue("test")
        .useExceptionConsumer((value, exception) -> {
        })
        .build();

    // Assert
    assertNotNull(failsafeMap);
    checkLoggerSetWithValue(failsafeMap, LOGGER);
  }

  @Test
  public void failsafeForSorted() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeSort.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForSorted(LOGGER);

    final FailsafeSort<String, Exception> failsafeSort = failsafeBuilder
        .useDefaultValue(0)
        .useExceptionConsumer((value1, value2, exception) -> {
        })
        .build();

    // Assert
    assertNotNull(failsafeSort);
    checkLoggerSetWithValue(failsafeSort, LOGGER);
  }

  @Test
  public void failsafeForSorted_noPerformingMethodSet_fails() {
    // Act
    final FailsafeSort.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForSorted(LOGGER);

    IllegalStateException expectedException;
    try {
      failsafeBuilder.build().create();
      fail("Expected exception to be thrown here, since the performing method reference was not set!");
      expectedException = null;
    } catch (final IllegalStateException exception) {
      expectedException = exception;
    }
    // Assert
    assertNotNull(failsafeBuilder);
    assertThat(expectedException.getMessage(), StringContains.containsString("method reference not set"));
    assertThat(expectedException.getMessage(), StringContains.containsString("not sufficiently initialized"));
  }

  @Test
  public void failsafeForTest() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeTest.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForTest(LOGGER);

    final FailsafeTest<String, Exception> failsafeTest = failsafeBuilder
        .useDefaultValue(true)
        .useExceptionConsumer((value, exception) -> {
        })
        .build();

    // Assert
    assertNotNull(failsafeTest);
    checkLoggerSetWithValue(failsafeTest, LOGGER);
  }

  @Test
  public void failsafeForTest_noPerformingMethodSet_fails() {
    // Act
    final FailsafeTest.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForTest(LOGGER);

    IllegalStateException expectedException;
    try {
      failsafeBuilder.build().create();
      fail("Expected exception to be thrown here, since the performing method reference was not set!");
      expectedException = null;
    } catch (final IllegalStateException exception) {
      expectedException = exception;
    }
    // Assert
    assertNotNull(failsafeBuilder);
    assertThat(expectedException.getMessage(), StringContains.containsString("method reference not set"));
    assertThat(expectedException.getMessage(), StringContains.containsString("not sufficiently initialized"));
  }

  private void checkLoggerSetWithValue(final Object failsafe, final Logger expectedLoggerValue)
      throws NoSuchFieldException, IllegalAccessException {
    final Field loggerField = failsafe.getClass().getDeclaredField("logger");
    loggerField.setAccessible(true);
    final Logger actualLogger = (Logger) loggerField.get(failsafe);
    assertEquals(expectedLoggerValue, actualLogger);
  }

}