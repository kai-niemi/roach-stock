package io.roach.stock.domain.account;

import java.util.UUID;

/**
 * Business exception thrown if a referenced account does not exist.
 */
public class NoSuchSystemAccountException extends NoSuchAccountException {
    public NoSuchSystemAccountException(UUID id) {
        super("No system account with ID " + id);
    }

    public NoSuchSystemAccountException(UUID id, UUID refId) {
        super("No system account with ID " + id + " for trading account ID: " + refId);
    }
}
