package com.novomind.commons.util.failsafe;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.novomind.commons.util.ThrowingFunction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(setterPrefix = "use", builderClassName = "Builder")
class FailsafeMap<A, B, E extends Exception> implements FailsafeOperation<Function<A, B>> {

  private final ThrowingFunction<A, B, E> mapper;
  @lombok.Builder.Default
  private final Supplier<B> defaultValueSupplier = () -> null;
  @lombok.Builder.Default
  private final BiConsumer<A, Throwable> exceptionConsumer = null;
  /**
   * Is set internally prior to this builder.
   */
  @Deprecated
  private final Logger logger;

  @Override
  public Function<A, B> create() {
    if (mapper == null) {
      throw new IllegalStateException("The necessary method reference not set. Failsafe is not sufficiently initialized!");
    }
    return internalCreateApply(mapper, defaultValueSupplier, logger, exceptionConsumer);
  }

  private static <A, B, E extends Exception> Function<A, B> internalCreateApply(
      @NonNull final ThrowingFunction<A, B, E> function,
      @NonNull final Supplier<B> defaultValue,
      @Nullable final Logger logger,
      @Nullable final BiConsumer<A, Throwable> exceptionConsumer) {
    return input -> {
      try {
        return function.apply(input);
      } catch (final Throwable e) {
        if (logger != null) {
          logger.error(String.format("An error occurred processing [%s].", input), e);
        }
        if (exceptionConsumer != null) {
          exceptionConsumer.accept(input, e);
        }
      }
      return defaultValue.get();
    };
  }

  public static class Builder<A, B, E extends Exception> extends
      FailsafeOperationBuilderWithLogger<Builder<A, B, E>, Function<A, B>> {

    /**
     * This is a convenience method for {@link #useMapper(ThrowingFunction)}
     */
    @Nonnull
    public Builder<A, B, E> map(@Nonnull final ThrowingFunction<A, B, E> mapper) {
      if (this.mapper != null) {
        throw new IllegalStateException("The mapper is already set.");
      }
      return useMapper(mapper);
    }

    @Nonnull
    public Builder<A, B, E> useDefaultValue(@Nullable final B defaultValue) {
      if (defaultValueSupplier$value != null) {
        throw new IllegalStateException("The defaultValueSupplier is already set.");
      }
      useDefaultValueSupplier(() -> defaultValue);
      return this;
    }

    @Nullable
    @Override
    protected Object getPerformingElement() {
      return mapper;
    }
  }
}
