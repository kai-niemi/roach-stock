package io.roach.stock.functionaltests;

import io.roach.stock.AbstractIntegrationTest;
import io.roach.stock.domain.account.AccountService;
import io.roach.stock.domain.order.BookingOrder;
import io.roach.stock.domain.order.OrderRequest;
import io.roach.stock.domain.order.OrderService;
import io.roach.stock.domain.portfolio.PortfolioService;
import io.roach.stock.doubles.TestDoubles;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static io.roach.stock.doubles.TestDoubles.USER_INITIAL_BALANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("it")
public class TradingFunctionalTest extends AbstractIntegrationTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PortfolioService portfolioService;

    @BeforeAll
    public void setupTest() {
        logger.info("Removing test doubles..");
        doublesService.removeTestDoubles();
        logger.info("Creating test doubles..");
        doublesService.createTestDoubles();
    }

    @Test
    @Transactional
    @Commit
    public void whenPlacingOrders_thenReturnCorrectBalances() {
        assertEquals(USER_INITIAL_BALANCE, accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));

        BookingOrder o1 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(5)
                .build()
        );
        assertEquals(TestDoubles.APPLE_A.getBuyPrice().multiply(5), o1.getTotalPrice());

        BookingOrder o2 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .buy(TestDoubles.NOKIA_A.getReference())
                .unitPrice(TestDoubles.NOKIA_A.getBuyPrice())
                .quantity(2)
                .build()
        );
        assertEquals(TestDoubles.NOKIA_A.getBuyPrice().multiply(2), o2.getTotalPrice());

        BookingOrder o3 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .sell(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getSellPrice())
                .quantity(2)
                .build()
        );
        assertEquals(TestDoubles.APPLE_A.getSellPrice().multiply(2), o3.getTotalPrice());

        assertEquals(
                USER_INITIAL_BALANCE.plus(
                        TestDoubles.APPLE_A.getBuyPrice().multiply(5).negate(),
                        TestDoubles.NOKIA_A.getBuyPrice().multiply(2).negate(),
                        TestDoubles.APPLE_A.getSellPrice().multiply(2)
                ),
                accountService.getBalance(TestDoubles.USER_ACCOUNT_ALICE));

        assertEquals(
                TestDoubles.APPLE_A.getSellPrice().multiply(3).plus(
                        TestDoubles.NOKIA_A.getSellPrice().multiply(2)
                ),
                portfolioService.getTotalValue(TestDoubles.USER_ACCOUNT_ALICE));
    }
}
