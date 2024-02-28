package io.roach.stock.domain.order;

import io.roach.stock.domain.common.BusinessException;

import java.util.UUID;

/**
 * Business exception thrown if an account has insufficient funds.
 */
public class NegativeBalanceException extends BusinessException {
    public NegativeBalanceException(UUID id) {
        super("Negative balance for account with ID: " + id);
    }
}
