package io.roach.stock.domain.portfolio;

import io.roach.stock.domain.common.BusinessException;

import java.util.UUID;

/**
 * Business exception thrown if a referenced portfolio does not exist.
 */
public class NoSuchPortfolioException extends BusinessException {
    public NoSuchPortfolioException(UUID id) {
        super("No portfolio found for account with ID: " + id);
    }
}

