package com.novomind.commons.util.failsafe;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.novomind.commons.util.ThrowingPredicate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(setterPrefix = "use", builderClassName = "Builder")
class FailsafeTest<A, E extends Exception> implements FailsafeOperation<Predicate<A>> {

  private final ThrowingPredicate<A, E> predicate;
  @lombok.Builder.Default
  private final BooleanSupplier defaultValueSupplier = () -> false;
  @lombok.Builder.Default
  private final BiConsumer<A, Throwable> exceptionConsumer = null;
  /**
   * Is set internally prior to this builder.
   */
  @Deprecated
  private final Logger logger;

  @Override
  @Nonnull
  public Predicate<A> create() {
    if (predicate == null) {
      throw new IllegalStateException("The necessary method reference not set. Failsafe is not sufficiently initialized!");
    }
    return internalCreateTest(predicate, defaultValueSupplier, logger, exceptionConsumer);
  }

  @Nonnull
  private static <A, E extends Exception> Predicate<A> internalCreateTest(
      @Nonnull final ThrowingPredicate<A, E> predicate,
      @Nonnull final BooleanSupplier defaultValue,
      @Nullable final Logger logger,
      @Nullable final BiConsumer<A, Throwable> exceptionConsumer) {
    return value -> {
      try {
        return predicate.test(value);
      } catch (final Throwable e) {
        if (logger != null) {
          logger.error(String.format("An error occurred evaluating '%s'.", value), e);
        }
        if (exceptionConsumer != null) {
          exceptionConsumer.accept(value, e);
        }
      }
      return defaultValue.getAsBoolean();
    };
  }

  public static class Builder<A, E extends Exception> extends FailsafeOperationBuilderWithLogger<Builder<A, E>, Predicate<A>> {

    /**
     * This is a convenience method for {@link #usePredicate(ThrowingPredicate)}
     */
    @Nonnull
    public Builder<A, E> filter(@Nonnull final ThrowingPredicate<A, E> predicate) {
      if (this.predicate != null) {
        throw new IllegalStateException("The predicate is already set.");
      }
      return usePredicate(predicate);
    }

    @Nonnull
    public FailsafeTest.Builder<A, E> useDefaultValue(final boolean defaultValue) {
      if (defaultValueSupplier$value != null) {
        throw new IllegalStateException("The defaultValueSupplier is already set.");
      }
      useDefaultValueSupplier(() -> defaultValue);
      return this;
    }

    @Nullable
    @Override
    protected Object getPerformingElement() {
      return predicate;
    }
  }
}
