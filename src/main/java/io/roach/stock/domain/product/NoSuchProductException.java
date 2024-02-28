package io.roach.stock.domain.product;

import io.roach.stock.domain.common.BusinessException;

import java.util.UUID;

/**
 * Business exception thrown if a referenced account does not exist.
 */
public class NoSuchProductException extends BusinessException {
    public NoSuchProductException(UUID id) {
        super("No product found with id '" + id + "'");
    }

    public NoSuchProductException(String productRef) {
        super("No product found with reference '" + productRef + "'");
    }
}

