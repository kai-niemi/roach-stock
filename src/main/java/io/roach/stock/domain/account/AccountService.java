package io.roach.stock.domain.account;

import java.util.Currency;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.roach.stock.domain.common.Money;

/**
 * Defines the business service for managing trading accounts.
 */
public interface AccountService {
    /**
     * Create a new system account with an initial balance.
     *
     * @param systemAccountId reference for system account
     * @param name            account name
     * @param balance         the initial account balance and currency
     */
    SystemAccount createSystemAccount(UUID systemAccountId, String name, Money balance);

    /**
     * Create a new trading account with an initial balance.
     *
     * @param systemAccountId  reference to parent system account (must exist)
     * @param tradingAccountId a unique account reference scoped to the client namespace
     * @param name             account name
     * @param balance          the initial account balance and currency
     */
    TradingAccount createTradingAccount(UUID systemAccountId, UUID tradingAccountId, String name, Money balance);

    TradingAccount createTradingAccount(UUID systemAccountId, UUID tradingAccountId, String name, Money balance,
                                        boolean withPortfolio);

    /**
     * Get the current balance for a given account.
     *
     * @param id a unique account reference scoped to the client namespace
     * @return the account balance
     */
    Money getBalance(UUID id);

    Money getSystemAccountTotalBalance(Currency currency);

    Money getTradingAccountTotalBalance(Currency currency);

    SystemAccount getSystemAccountById(UUID id);

    TradingAccount getTradingAccountById(UUID id, boolean fetchPortfolio);

    Page<TradingAccount> findTradingAccountsByRandom(Pageable page);
}
