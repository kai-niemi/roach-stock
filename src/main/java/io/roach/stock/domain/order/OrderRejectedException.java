package io.roach.stock.domain.order;

import io.roach.stock.domain.common.BusinessException;

/**
 * Exception thrown if an order is rejected by a violation of market rules. Usually by
 * setting a product sell price that is 5% over market price or a buy order that
 * is 5% below market price.
 */
public class OrderRejectedException extends BusinessException {
    public OrderRejectedException(String message) {
        super(message);
    }
}
