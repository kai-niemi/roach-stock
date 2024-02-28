package io.roach.stock.domain.order;


import io.roach.stock.domain.common.BusinessException;

public class NegativeQuantityException extends BusinessException {
    public NegativeQuantityException(String message) {
        super(message);
    }
}