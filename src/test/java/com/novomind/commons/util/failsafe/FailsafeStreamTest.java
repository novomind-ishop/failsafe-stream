package com.novomind.commons.util.failsafe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailsafeStreamTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailsafeStreamTest.class);

  @Test
  public void failsafe_withLogger() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeStream.Failsafe.Builder failsafeBuilder = FailsafeStream.failsafe();
    assertThat(failsafeBuilder).isNotNull();

    final FailsafeStream.Failsafe failsafe = failsafeBuilder
        .logger(LOGGER)
        .build();

    // Assert
    assertThat(failsafe).isNotNull();

    checkLoggerSetWithValue(failsafe, LOGGER);
  }

  @Test
  public void failsafe_withoutLogger() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeStream.Failsafe.Builder failsafeBuilder = FailsafeStream.failsafe();
    assertThat(failsafeBuilder).isNotNull();

    final FailsafeStream.Failsafe failsafe = failsafeBuilder
        .build();

    // Assert
    assertThat(failsafe).isNotNull();

    checkLoggerSetWithValue(failsafe, null);
  }

  @Test
  public void failsafeForMap_noPerformingMethodSet_fails() {
    final FailsafeMap.Builder<String, String, Exception> failsafeBuilder = FailsafeStream.failsafeForMap(LOGGER);
    assertThat(failsafeBuilder).isNotNull();

    // Act & Assert
    assertThatThrownBy(() -> failsafeBuilder.build().create())
        .withFailMessage("Expected exception to be thrown here, since the performing method reference was not set!")
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("method reference not set")
        .hasMessageContaining("not sufficiently initialized");

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
    assertThat(failsafeMap).isNotNull();
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
    assertThat(failsafeSort).isNotNull();
    checkLoggerSetWithValue(failsafeSort, LOGGER);
  }

  @Test
  public void failsafeForSorted_noPerformingMethodSet_fails() {
    final FailsafeSort.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForSorted(LOGGER);
    assertThat(failsafeBuilder).isNotNull();

    // Act & Assert
    assertThatThrownBy(() -> failsafeBuilder.build().create())
        .withFailMessage("Expected exception to be thrown here, since the performing method reference was not set!")
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("method reference not set")
        .hasMessageContaining("not sufficiently initialized");
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
    assertThat(failsafeTest).isNotNull();
    checkLoggerSetWithValue(failsafeTest, LOGGER);
  }

  @Test
  public void failsafeForTest_noPerformingMethodSet_fails() {
    final FailsafeTest.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForTest(LOGGER);
    assertThat(failsafeBuilder).isNotNull();

    // Act & Assert
    assertThatThrownBy(() -> failsafeBuilder.build().create())
        .withFailMessage("Expected exception to be thrown here, since the performing method reference was not set!")
        .hasMessageContaining("method reference not set")
        .hasMessageContaining("not sufficiently initialized");
  }
  @Test
  public void failsafeForAccept() throws NoSuchFieldException, IllegalAccessException {
    // Act
    final FailsafeAccept.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForAccept(LOGGER);

    final FailsafeAccept<String, Exception> failsafeAccept = failsafeBuilder
        .useExceptionConsumer((value, exception) -> {
        })
        .build();

    // Assert
    assertThat(failsafeAccept).isNotNull();
    checkLoggerSetWithValue(failsafeAccept, LOGGER);
  }

  @Test
  public void failsafeForAccept_noPerformingMethodSet_fails() {
    final FailsafeAccept.Builder<String, Exception> failsafeBuilder = FailsafeStream.failsafeForAccept(LOGGER);
    assertThat(failsafeBuilder).isNotNull();

    // Act & Assert
    assertThatThrownBy(() -> failsafeBuilder.build().create())
        .withFailMessage("Expected exception to be thrown here, since the performing method reference was not set!")
        .hasMessageContaining("method reference not set")
        .hasMessageContaining("not sufficiently initialized");
  }

  private void checkLoggerSetWithValue(final Object failsafe, final Logger expectedLoggerValue)
      throws NoSuchFieldException, IllegalAccessException {
    final Field loggerField = failsafe.getClass().getDeclaredField("logger");
    loggerField.setAccessible(true);
    final Logger actualLogger = (Logger) loggerField.get(failsafe);
    assertThat(expectedLoggerValue).isEqualTo(actualLogger);
  }

}