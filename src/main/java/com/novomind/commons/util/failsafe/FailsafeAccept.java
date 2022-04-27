package com.novomind.commons.util.failsafe;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.novomind.commons.util.ThrowingConsumer;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder(setterPrefix = "use", builderClassName = "Builder")
class FailsafeAccept<A, E extends Exception> implements FailsafeOperation<Consumer<A>> {

  private final ThrowingConsumer<A, E> consumer;
  @lombok.Builder.Default
  private final BiConsumer<A, Throwable> exceptionConsumer = null;
  /**
   * Is set internally prior to this builder.
   */
  @Deprecated
  private final Logger logger;

  @Override
  public Consumer<A> create() {
    if (consumer == null) {
      throw new IllegalStateException("The necessary method reference not set. Failsafe is not sufficiently initialized!");
    }
    return internalCreateTest(consumer, logger, exceptionConsumer);
  }

  private static <A, E extends Exception> Consumer<A> internalCreateTest(
      @Nonnull final ThrowingConsumer<A, E> consumer,
      @Nullable final Logger logger,
      @Nullable final BiConsumer<A, Throwable> exceptionConsumer) {
    return value -> {
      try {
        consumer.consume(value);
      } catch (final Throwable e) {
        if (logger != null) {
          logger.error(String.format("An error occurred evaluating '%s'.", value), e);
        }
        if (exceptionConsumer != null) {
          exceptionConsumer.accept(value, e);
        }
      }
    };
  }

  public static class Builder<A, E extends Exception> extends FailsafeOperationBuilderWithLogger<Builder<A, E>, Consumer<A>> {

    /**
     * This is a convenience method for {@link #useConsumer(ThrowingConsumer)}
     */
    public Builder<A, E> consume(@Nonnull final ThrowingConsumer<A, E> consumer) {
      if (this.consumer != null) {
        throw new IllegalStateException("The predicate is already set.");
      }
      return useConsumer(consumer);
    }

    @Nullable
    @Override
    protected Object getPerformingElement() {
      return consumer;
    }
  }
}
