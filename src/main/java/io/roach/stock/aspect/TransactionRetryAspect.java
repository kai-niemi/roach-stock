package io.roach.stock.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.stock.annotation.AdvisorOrder;
import io.roach.stock.annotation.Retryable;

/**
 * AOP aspect that applies retries of failed transactions in the main business services.
 * This aspect intercepts and handles concurrency failures such as deadlock looser,
 * pessimistic and optimistic locking failures.
 *
 * @see Retryable
 */
@Aspect
@Order(AdvisorOrder.TX_RETRY_ADVISOR)
public class TransactionRetryAspect {
    public static <A extends Annotation> A findAnnotation(ProceedingJoinPoint pjp, Class<A> annotationType) {
        return AnnotationUtils
                .findAnnotation(pjp.getSignature().getDeclaringType(), annotationType);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Consumer<Integer> retryConsumer = integer -> {
    };

    public void setRetryConsumer(Consumer<Integer> retryConsumer) {
        this.retryConsumer = retryConsumer;
    }

    @Around(value = "Pointcuts.retryableBoundaryOperation(retryable)",
            argNames = "pjp,retryable")
    public Object doAroundRetryable(ProceedingJoinPoint pjp, Retryable retryable)
            throws Throwable {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "tx active");

        // Grab from type if needed (for non-annotated methods)
        if (retryable == null) {
            retryable = findAnnotation(pjp, Retryable.class);
        }

        int numCalls = 0;

        final Instant callTime = Instant.now();

        do {
            final Throwable throwable;

            try {
                numCalls++;
                Object rv = pjp.proceed();
                if (numCalls > 1) {
                    retryConsumer.accept(numCalls - 1);
                    logger.info(
                            "Transient error recovered after %d of %d retries (%s)"
                                    .formatted(numCalls - 1,
                                            retryable.retryAttempts(),
                                            Duration.between(callTime, Instant.now()).toString()));
                }
                return rv;
            } catch (UndeclaredThrowableException ex) {
                throwable = ex.getUndeclaredThrowable();
            } catch (Exception ex) {
                throwable = ex;
            }

            Throwable cause = NestedExceptionUtils.getMostSpecificCause(throwable);
            if (cause instanceof SQLException) {
                SQLException sqlException = (SQLException) cause;
                if ("40001".equals(sqlException.getSQLState())
                        || "40P01".equals(sqlException.getSQLState())) { // Deadlock loser in PSQL
                    handleTransientException(sqlException, numCalls, pjp.getSignature().toShortString(),
                            retryable.maxBackoff());
                    continue;
                }
            }

            logger.error("Non-recoverable exception in retry loop", throwable);

            throw throwable;

        } while (numCalls < retryable.retryAttempts());

        throw new ConcurrencyFailureException(
                "Too many transient errors (%d) for method [%s]. Giving up!"
                        .formatted(numCalls, pjp.getSignature().toShortString()));
    }

    private void handleTransientException(SQLException ex, int numCalls, String method, long maxBackoff) {
        try {
            long backoffMillis = Math.min((long) (Math.pow(2, numCalls)
                    + ThreadLocalRandom.current().nextInt(1000)), maxBackoff);

            if (logger.isWarnEnabled()) {
                logger.warn("Transient error detected (backoff {}ms) in call {} to '{}': {}",
                        backoffMillis, numCalls, method, ex.getMessage());
            }

            TimeUnit.MILLISECONDS.sleep(backoffMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
