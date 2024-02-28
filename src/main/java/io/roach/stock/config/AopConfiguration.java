package io.roach.stock.config;

import io.roach.stock.aspect.TransactionDecoratorAspect;
import io.roach.stock.aspect.TransactionRetryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for all cross-cutting AOP aspects.
 */
@Configuration
public class AopConfiguration {
    @Bean
    public TransactionRetryAspect retryableOperationAspect() {
        return new TransactionRetryAspect();
    }

    @Bean
    public TransactionDecoratorAspect transactionHintsAspect() {
        return new TransactionDecoratorAspect();
    }
}
