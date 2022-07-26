package com.novomind.commons.util.failsafe;

import java.util.Comparator;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.novomind.commons.util.ThrowingComparator;
import com.novomind.commons.util.TriConsumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(setterPrefix = "use", builderClassName = "Builder")
class FailsafeSort<A, E extends Exception> implements FailsafeOperation<Comparator<A>> {

  private final ThrowingComparator<A, E> comparator;
  @lombok.Builder.Default
  private final Supplier<Integer> defaultValueSupplier = () -> 0;
  @lombok.Builder.Default
  private final TriConsumer<A, A, Throwable> exceptionConsumer = null;
  /**
   * Is set internally prior to this builder.
   */
  @Deprecated
  private final Logger logger;

  @Override
  public Comparator<A> create() {
    if (comparator == null) {
      throw new IllegalStateException("The necessary method reference not set. Failsafe is not sufficiently initialized!");
    }
    return internalCreateSorted(comparator, defaultValueSupplier, logger, exceptionConsumer);
  }

  @Nonnull
  private static <A, E extends Exception> Comparator<A> internalCreateSorted(
      @Nonnull final ThrowingComparator<A, E> comparator,
      @Nonnull final Supplier<Integer> defaultValue,
      @Nullable final Logger logger,
      @Nullable final TriConsumer<A, A, Throwable> exceptionConsumer) {
    return (leftValue, rightValue) -> {
      try {
        return comparator.compare(leftValue, rightValue);
      } catch (final Throwable e) {
        if (logger != null) {
          logger.error(String.format("An error occurred comparing [%s vs. %s].", leftValue, rightValue), e);
        }
        if (exceptionConsumer != null) {
          exceptionConsumer.accept(leftValue, rightValue, e);
        }
      }
      return defaultValue.get();
    };
  }

  public static class Builder<A, E extends Exception> extends FailsafeOperationBuilderWithLogger<Builder<A, E>, Comparator<A>> {

    /**
     * This is a convenience method for {@link #useComparator(ThrowingComparator)}
     */
    @Nonnull
    public Builder<A, E> compare(@Nonnull final ThrowingComparator<A, E> comparator) {
      if (this.comparator != null) {
        throw new IllegalStateException("The comparator is already set.");
      }
      return useComparator(comparator);
    }

    @Nonnull
    public FailsafeSort.Builder<A, E> useDefaultValue(final int defaultValue) {
      if (defaultValueSupplier$value != null) {
        throw new IllegalStateException("The defaultValueSupplier is already set.");
      }
      useDefaultValueSupplier(() -> defaultValue);
      return this;
    }

    @Nullable
    @Override
    protected Object getPerformingElement() {
      return comparator;
    }
  }
}
