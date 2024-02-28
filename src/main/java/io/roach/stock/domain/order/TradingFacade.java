package io.roach.stock.domain.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import io.roach.stock.annotation.Retryable;
import io.roach.stock.annotation.TransactionBoundary;
import io.roach.stock.domain.account.AccountService;
import io.roach.stock.domain.account.TradingAccount;
import io.roach.stock.domain.product.Product;

@Service
public class TradingFacade {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AccountService accountService;

    @TransactionBoundary
    @Retryable
    public BookingOrder placeOrder(OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @TransactionBoundary
    @Retryable
    public List<BookingOrder> placeSellOrdersForAllHoldings(UUID tradingAccountId) {
        TradingAccount tradingAccount =
                accountService.getTradingAccountById(tradingAccountId, true);

        Map<Product, AtomicInteger> map = new HashMap<>();

        tradingAccount
                .getPortfolio()
                .getItems()
                .forEach(portfolioItem -> map.computeIfAbsent(portfolioItem.getProduct(),
                        product -> new AtomicInteger()).addAndGet(portfolioItem.getQuantity()));

        List<BookingOrder> orderList = new ArrayList<>();

        map.forEach((product, qty) -> {
            if (qty.get() > 0) {
                OrderRequest orderRequest = OrderRequest.builder()
                        .bookingAccount(tradingAccount.getId())
                        .sell(product.getReference())
                        .unitPrice(product.getSellPrice())
                        .quantity(qty.get())
                        .ref(UUID.randomUUID().toString())
                        .build();
                orderList.add(orderService.placeOrder(orderRequest));
            }
        });

        return orderList;
    }

    @TransactionBoundary
    @Retryable
    public List<BookingOrder> placeSellOrdersForHoldings(UUID tradingAccountId,
                                                         Pair<Product, Integer> pair) {
        TradingAccount tradingAccount =
                accountService.getTradingAccountById(tradingAccountId, true);

        List<BookingOrder> orderList = new ArrayList<>();

        OrderRequest orderRequest = OrderRequest.builder()
                .bookingAccount(tradingAccount.getId())
                .sell(pair.getFirst().getReference())
                .unitPrice(pair.getFirst().getSellPrice())
                .quantity(pair.getSecond())
                .ref(UUID.randomUUID().toString())
                .build();

        orderList.add(orderService.placeOrder(orderRequest));

        return orderList;
    }
}
