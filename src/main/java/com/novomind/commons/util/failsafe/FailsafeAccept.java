package com.novomind.commons.util.failsafe;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.novomind.commons.util.ThrowingConsumer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(setterPrefix = "use", builderClassName = "Builder")
class FailsafeAccept<A, E extends Exception> implements FailsafeOperation<Consumer<A>> {

  private final ThrowingConsumer<A, E> consumer;
  @lombok.Builder.Default
  private final BiConsumer<A, Throwable> exceptionConsumer = null;
  /**
   * Is set internally prior to this builder.<br/>
   * Actually not deprecated.
   */
  @Deprecated
  private final Logger logger;

  @Override
  @Nonnull
  public Consumer<A> create() {
    if (consumer == null) {
      throw new IllegalStateException("The necessary method reference not set. Failsafe is not sufficiently initialized!");
    }
    return internalCreateAccept(consumer, logger, exceptionConsumer);
  }

  @Nonnull
  private static <A, E extends Exception> Consumer<A> internalCreateAccept(
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

  /**
   * Special lombok-Builder extension
   * @param <A>
   * @param <E>
   */
  public static class Builder<A, E extends Exception> extends FailsafeOperationBuilderWithLogger<Builder<A, E>, Consumer<A>> {

    /**
     * This is a convenience method for {@link #useConsumer(ThrowingConsumer)}
     */
    @Nonnull
    public Builder<A, E> consume(@Nonnull final ThrowingConsumer<A, E> consumer) {
      if (this.consumer != null) {
        throw new IllegalStateException("The consumer is already set.");
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
