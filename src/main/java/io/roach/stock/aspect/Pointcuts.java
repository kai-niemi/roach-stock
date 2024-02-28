package io.roach.stock.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.roach.stock.annotation.Retryable;
import io.roach.stock.annotation.TransactionBoundary;

/**
 * Shared pointcut expressions for all service layer AOP advices.
 */
@Aspect
public class Pointcuts {
    /**
     * Pointcut expression matching all transactional boundaries in service layer.
     */
    @Pointcut("execution(public * *(..)) "
            + "&& @annotation(transactionBoundary)")
    public void anyTransactionBoundaryOperation(TransactionBoundary transactionBoundary) {
    }

    /**
     * Pointcut expression matching all retryable transactional boundaries in service layer.
     */
    @Pointcut("execution(public * *(..)) "
            + "&& @annotation(retryable)")
    public void retryableBoundaryOperation(Retryable retryable) {
    }
}

