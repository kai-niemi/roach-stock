package io.roach.stock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for business service methods that utilize an optimistic transaction locking strategy.
 * A method that is annotated as Retryable will automatically be candidate for re-invocation on
 * concurrency failures such as deadlock looser, optimistic locking failures, etc.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Retryable {
    /**
     * @return number of times to retry aborted transient data access exceptions with exponential backoff (up to 5s per cycle). Zero or negative value disables retries.
     */
    int retryAttempts() default 30;

    /**
     * @return max backoff time in millis
     */
    long maxBackoff() default 15000;
}
