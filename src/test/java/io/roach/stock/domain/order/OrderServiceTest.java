package io.roach.stock.domain.order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.stock.AbstractIntegrationTest;
import io.roach.stock.doubles.TestDoubles;

import static io.roach.stock.domain.common.Money.euro;

@Tag("it")
public class OrderServiceTest extends AbstractIntegrationTest {
    @Autowired
    private OrderService orderService;

    @BeforeAll
    public void setupTest() {
        doublesService.removeTestDoubles();
        doublesService.createTestDoubles();
    }

    @Test
    @Transactional
    @Commit
    @Order(1)
    public void whenCreatingOrder_thenSucceed() {
        Assertions.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());

        BookingOrder o1 = orderService.placeOrder(OrderRequest.builder()
                .bookingAccount(TestDoubles.USER_ACCOUNT_ALICE)
                .buy(TestDoubles.APPLE_A.getReference())
                .unitPrice(TestDoubles.APPLE_A.getBuyPrice())
                .quantity(5)
                .build()
        );

        Assertions.assertEquals(euro("152.50"), o1.getTotalPrice());
    }
}