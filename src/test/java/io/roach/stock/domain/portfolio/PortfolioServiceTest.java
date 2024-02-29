package io.roach.stock.domain.portfolio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.roach.stock.domain.account.AccountService;
import io.roach.stock.domain.common.Money;
import io.roach.stock.annotation.NotTransactional;
import io.roach.stock.doubles.TestDoubles;
import io.roach.stock.AbstractIntegrationTest;

@Tag("it")
public class PortfolioServiceTest extends AbstractIntegrationTest {
    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AccountService accountService;

    @BeforeAll
    public void setupTest() {
        doublesService.removeTestDoubles();

        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_A, "TRADER:A", Money.euro("10000000.00"));
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_ALICE,
                "ALICE", Money.euro("1500.00"));
        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_BOB,
                "BOB", Money.euro("1500.00"));
    }

    @Test
    @NotTransactional
    @Order(1)
    public void whenFindingPortfolio_thenReturnEntity() {
        Portfolio p1 = portfolioService.getPortfolioById(TestDoubles.USER_ACCOUNT_ALICE);
        Assertions.assertNotNull(p1);
        Assertions.assertEquals(Money.euro("0.00"), p1.getTotalValue());
    }

    @Test
    @NotTransactional
    @Order(2)
    public void whenFindingPortfolio_thenThrowNoSuchPortfolioException() {
        Assertions.assertThrows(NoSuchPortfolioException.class, () -> {
            portfolioService.getPortfolioById(TestDoubles.SYSTEM_ACCOUNT_A);
            Assertions.fail("Must not succeed");
        });
    }
}
