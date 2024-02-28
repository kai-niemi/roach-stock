package io.roach.stock.domain.account;

import io.roach.stock.AbstractIntegrationTest;
import io.roach.stock.util.Money;
import io.roach.stock.doubles.TestDoubles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("it")
public class AccountServiceTest extends AbstractIntegrationTest {
    @Autowired
    private AccountService accountService;

    @BeforeAll
    public void setupTest() {
        doublesService.removeTestDoubles();
    }

    @Test
    @Transactional
    @Commit
    @Order(1)
    public void whenCreatingAccounts_thenSucceed() {
        assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        accountService.createSystemAccount(TestDoubles.SYSTEM_ACCOUNT_A, "TRADER:A",
                Money.euro("10000000.00"));

        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_ALICE,
                "TRADER:B", Money.euro("1500.00"));

        accountService.createTradingAccount(TestDoubles.SYSTEM_ACCOUNT_A, TestDoubles.USER_ACCOUNT_BOB,
                "TRADER:C", Money.euro("1500.00"));
    }

    @Test
    @Transactional
    @Commit
    @Order(2)
    public void whenFindingAccountById_thenReturnBalance() {
        Money b1 = accountService.getBalance(TestDoubles.SYSTEM_ACCOUNT_A);
        assertEquals(Money.euro("10000000.00"), b1);

        Money b2 = accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE);
        assertEquals(Money.euro("1500.00"), b2);

        Money b3 = accountService.getBalance(TestDoubles.USER_ACCOUNT_BOB);
        assertEquals(Money.euro("1500.00"), b3);
    }

    @Test
    @Transactional
    @Commit
    @Order(3)
    public void whenFindingAccounts_thenSucceed() {
        Account a1 = accountService.getSystemAccountById(TestDoubles.SYSTEM_ACCOUNT_A);
        assertEquals(Money.euro("10000000.00"), a1.getBalance());

        Account a2 = accountService.getTradingAccountById(TestDoubles.USER_ACCOUNT_ALICE, false);
        assertEquals(Money.euro("1500.00"), a2.getBalance());
    }
}
