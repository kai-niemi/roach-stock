package io.roach.stock.unittest;


import java.time.LocalDateTime;
import java.util.UUID;

import io.roach.stock.domain.order.OrderRequest;
import io.roach.stock.domain.order.OrderType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;

import io.roach.stock.util.Money;
import io.roach.stock.domain.account.AccountService;
import io.roach.stock.domain.account.SystemAccount;
import io.roach.stock.domain.account.TradingAccount;
import io.roach.stock.domain.order.BookingOrder;
import io.roach.stock.domain.order.OrderService;
import io.roach.stock.domain.portfolio.PortfolioService;
import io.roach.stock.doubles.TestDoubles;

import static io.roach.stock.util.Money.euro;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test that demonstrates the core functionality of the Trading System.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TradingUnitTest {
    private static final SystemAccount PARENT = new SystemAccount(
            UUID.randomUUID(),
            "USER:A", Money.euro("1000.00"));

    private static final TradingAccount ACCOUNT_A = new TradingAccount(
            UUID.randomUUID(),
            "USER:A", Money.euro("1000.00"), PARENT);

    private static final UUID ACCOUNT_A_ID = ACCOUNT_A.getId();

    private static final Money ACCOUNT_A_ORIGINAL_BALANCE = euro("1000.00");

    private AccountService accountServiceMock;

    private OrderService orderServiceMock;

    private PortfolioService portfolioServiceMock;

    @BeforeAll
    public void setupMocks() {
        accountServiceMock = Mockito.mock(AccountService.class);

        Mockito.when(accountServiceMock.getBalance(ACCOUNT_A_ID))
                .thenReturn(ACCOUNT_A_ORIGINAL_BALANCE)
                .thenReturn(Money.euro("904.10"));

        orderServiceMock = Mockito.mock(OrderService.class);

        Mockito.when(orderServiceMock
                        .placeOrder(Mockito.any(OrderRequest.class)))
                .thenReturn(null);

        Mockito.when(orderServiceMock.getOrderByRef(Mockito.eq("buy-1"))).thenReturn(
                new BookingOrder(UUID.randomUUID(),
                        ACCOUNT_A,
                        OrderType.BUY,
                        TestDoubles.APPLE_A, 5,
                        TestDoubles.APPLE_A.getBuyPrice().multiply(5),
                        LocalDateTime.now())
        );
        Mockito.when(orderServiceMock.getOrderByRef(Mockito.eq("buy-2"))).thenReturn(
                new BookingOrder(UUID.randomUUID(),
                        ACCOUNT_A, OrderType.BUY,
                        TestDoubles.NOKIA_A, 2,
                        TestDoubles.NOKIA_A.getBuyPrice().multiply(2),
                        LocalDateTime.now())
        );
        Mockito.when(orderServiceMock.getOrderByRef(Mockito.eq("sell-1"))).thenReturn(
                new BookingOrder(UUID.randomUUID(),
                        ACCOUNT_A, OrderType.SELL,
                        TestDoubles.APPLE_A, 2,
                        TestDoubles.APPLE_A.getSellPrice().multiply(2),
                        LocalDateTime.now())
        );

        portfolioServiceMock = Mockito.mock(PortfolioService.class);

        Mockito.when(portfolioServiceMock.getTotalValue(Mockito.eq(ACCOUNT_A_ID)))
                .thenReturn(euro("92.75"));
    }

    @Test
    public void whenPlacingOrders_Expect_CorrectAccountBalances() {
        // Initial balance
        assertEquals(ACCOUNT_A_ORIGINAL_BALANCE,
                accountServiceMock.getBalance(ACCOUNT_A_ID));

        orderServiceMock.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_A_ID)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(5)
                .ref("buy-1")
                .build()
        );

        orderServiceMock.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_A_ID)
                .buy(TestDoubles.NOKIA_A.getReference())
                .unitPrice(TestDoubles.NOKIA_A.getBuyPrice())
                .quantity(2)
                .ref("buy-2")
                .build()
        );

        orderServiceMock.placeOrder(OrderRequest.builder()
                .bookingAccount(ACCOUNT_A_ID)
                .sell(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getSellPrice())
                .quantity(2)
                .ref("sell-1")
                .build()
        );

        assertEquals(
                ACCOUNT_A_ORIGINAL_BALANCE.plus(
                        TestDoubles.APPLE_A.getBuyPrice().multiply(5).negate(),
                        TestDoubles.NOKIA_A.getBuyPrice().multiply(2).negate(),
                        TestDoubles.APPLE_A.getSellPrice().multiply(2)
                ),
                accountServiceMock.getBalance(ACCOUNT_A_ID));

        assertEquals(
                TestDoubles.APPLE_A.getSellPrice().multiply(3).plus(
                        TestDoubles.NOKIA_A.getSellPrice().multiply(2)),
                portfolioServiceMock.getTotalValue(ACCOUNT_A_ID));
    }
}
