package io.roach.stock.domain.account;

import io.roach.stock.domain.common.BusinessException;

public class NoSuchAccountException extends BusinessException {
    public NoSuchAccountException(String message) {
        super(message);
    }
}
