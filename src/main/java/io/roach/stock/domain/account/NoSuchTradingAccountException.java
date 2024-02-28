package io.roach.stock.domain.account;

import java.util.UUID;

public class NoSuchTradingAccountException extends NoSuchAccountException {
    public NoSuchTradingAccountException(UUID id) {
        super("No trading account with ID: " + id);
    }
}
