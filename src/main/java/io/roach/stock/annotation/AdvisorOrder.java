package io.roach.stock.annotation;

import org.springframework.core.Ordered;

public interface AdvisorOrder {
    int TX_ATTRIBUTES_ADVISOR = Ordered.LOWEST_PRECEDENCE - 2;

    int TX_ADVISOR = Ordered.LOWEST_PRECEDENCE - 3;

    int TX_RETRY_ADVISOR = Ordered.LOWEST_PRECEDENCE - 4;
}
