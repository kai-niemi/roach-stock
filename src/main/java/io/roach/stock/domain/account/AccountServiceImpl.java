package io.roach.stock.domain.account;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.stock.annotation.TransactionMandatory;
import io.roach.stock.util.Money;
import io.roach.stock.domain.portfolio.Portfolio;
import io.roach.stock.domain.portfolio.PortfolioRepository;

@Service
@TransactionMandatory
public class AccountServiceImpl implements AccountService {
    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Override
    public SystemAccount createSystemAccount(UUID systemAccountId, String name, Money balance) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "wrong tx state");

        SystemAccount account = new SystemAccount(systemAccountId, name, balance);
        systemAccountRepository.save(account);
        return account;
    }

    @Override
    public TradingAccount createTradingAccount(UUID systemAccountId, UUID tradingAccountId, String name,
                                               Money balance) {
        return createTradingAccount(systemAccountId, tradingAccountId, name, balance, true);
    }

    @Override
    public TradingAccount createTradingAccount(UUID parentAccountId,
                                               UUID tradingAccountId,
                                               String name,
                                               Money balance,
                                               boolean withPortfolio) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "wrong tx state");

        Assert.notNull(parentAccountId, "parentAccountId");
        Assert.notNull(tradingAccountId, "tradingAccountId");
        Assert.notNull(balance, "balance is null");

        // Get lazy-init proxy by reference to avoid lookup
        SystemAccount parentAccount = systemAccountRepository.getReferenceById(parentAccountId);

        // Fork system account
        TradingAccount tradingAccount = new TradingAccount(tradingAccountId, name, balance, parentAccount);
        tradingAccountRepository.save(tradingAccount);

        if (withPortfolio) {
            Portfolio portfolio = tradingAccount.createPortfolio();
            portfolio.setDescription("Portfolio for " + tradingAccount.getName());
            portfolioRepository.save(portfolio);
        }

        return tradingAccount;
    }


    @Override
    public Money getBalance(UUID id) {
        Assert.notNull(id, "Account id is null");
        return accountRepository.getBalanceById(id).orElseThrow(() -> new NoSuchAccountException(
                "with id: " + id));
    }

    @Override
    public SystemAccount getSystemAccountById(UUID id) {
        Assert.notNull(id, "Account id is null");
        return systemAccountRepository.findById(id)
                .orElseThrow(() -> new NoSuchSystemAccountException(id));
    }

    @Override
    public TradingAccount getTradingAccountById(UUID id, boolean fetchPortfolio) {
        Assert.notNull(id, "Account id is null");

        if (fetchPortfolio) {
            return tradingAccountRepository.findByIdWithPortfolio(id)
                    .orElseThrow(() -> new NoSuchTradingAccountException(id));
        }
        return tradingAccountRepository.findById(id)
                .orElseThrow(() -> new NoSuchTradingAccountException(id));
    }

    @Override
    public Page<TradingAccount> findTradingAccountsByPage(UUID parentId, Pageable page) {
        return tradingAccountRepository.findAccountsByPage(parentId, page);
    }

    @Override
    public Page<TradingAccount> findTradingAccountsByRandom(Pageable page) {
        return tradingAccountRepository.findAccountsByRandom(page);
    }

    @Override
    public Page<TradingAccount> findTradingAccountsByPage(Pageable page, boolean onlyWithHoldings) {
        if (onlyWithHoldings) {
            return tradingAccountRepository.findAccountsWithHoldingsByPage(page);
        }
        return tradingAccountRepository.findAccountsByPage(page);
    }

    @Override
    public Page<SystemAccount> findSystemAccountsByPage(Pageable page) {
        return systemAccountRepository.findAll(page);
    }
}
