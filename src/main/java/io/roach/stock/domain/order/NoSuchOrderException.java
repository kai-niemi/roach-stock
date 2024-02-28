package io.roach.stock.domain.order;

import io.roach.stock.domain.common.BusinessException;

import java.util.UUID;

/**
 * Business exception thrown if a referenced order does not exist.
 */
public class NoSuchOrderException extends BusinessException {
    public NoSuchOrderException(UUID id) {
        super("No order found with ID '" + id + "'");
    }

    public NoSuchOrderException(String ref) {
        super("No order found with reference '" + ref + "'");
    }
}
