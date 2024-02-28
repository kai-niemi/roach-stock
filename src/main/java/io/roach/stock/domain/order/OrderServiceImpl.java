package io.roach.stock.domain.order;

import io.roach.stock.annotation.TransactionMandatory;
import io.roach.stock.domain.account.NoSuchSystemAccountException;
import io.roach.stock.domain.account.NoSuchTradingAccountException;
import io.roach.stock.domain.account.SystemAccount;
import io.roach.stock.domain.account.SystemAccountRepository;
import io.roach.stock.domain.account.TradingAccount;
import io.roach.stock.domain.account.TradingAccountRepository;
import io.roach.stock.domain.portfolio.Portfolio;
import io.roach.stock.domain.product.NoSuchProductException;
import io.roach.stock.domain.product.Product;
import io.roach.stock.domain.product.ProductRepository;
import io.roach.stock.util.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@TransactionMandatory
public class OrderServiceImpl implements OrderService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemAccountRepository systemAccountRepository;

    @Autowired
    private TradingAccountRepository tradingAccountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public BookingOrder placeOrder(OrderRequest request) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "bad tx state");
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getOrderRef(), "order ref is null");

        // Idempotency check
        Optional<BookingOrder> order = orderRepository.findByReference(request.getOrderRef());
        if (order.isPresent()) {
            return order.get();
        }

        Product product = productRepository.getByReference(request.getProductRef())
                .orElseThrow(() -> new NoSuchProductException(request.getProductRef()));

        validateOrder(request, product);

        TradingAccount tradingAccount = tradingAccountRepository.getByIdForUpdate(request.getBookingAccountId())
                .orElseThrow(() -> new NoSuchTradingAccountException(request.getBookingAccountId()));

        SystemAccount systemAccount = systemAccountRepository.getByIdForUpdate(tradingAccount.getParentAccountId())
                .orElseThrow(() -> new NoSuchSystemAccountException(tradingAccount.getParentAccountId(),
                        tradingAccount.getId()));

        updatePortfolio(request, product, tradingAccount.getPortfolio());

        return createOrder(request, product, tradingAccount, systemAccount);
    }

    private void validateOrder(OrderRequest request, Product product) {
        switch (request.getOrderType()) {
            case BUY:
                BigDecimal buyLimit = orderRepository.getLimit("buy");

                Money lowestPrice = product.getBuyPrice().multiply(buyLimit);
                if (request.getUnitPrice().isLessThan(lowestPrice)) {
                    throw new OrderRejectedException("Unit price " + request.getUnitPrice()
                            + " is under buy limit " + lowestPrice + " for "
                            + product.getReference());
                }
                break;
            case SELL:
                BigDecimal sellLimit = orderRepository.getLimit("sell");

                Money highestPrice = product.getSellPrice().multiply(sellLimit);
                if (request.getUnitPrice().isGreaterThan(highestPrice)) {
                    throw new OrderRejectedException("Unit price " + request.getUnitPrice()
                            + " is above sell limit " + highestPrice + " for "
                            + product.getReference());
                }
                break;
            default:
                throw new OrderRejectedException("Unknown order type: " + request.getOrderType());
        }
    }

    private void updatePortfolio(OrderRequest request, Product product, Portfolio portfolio) {
        if (portfolio == null) {
            return;
        }
        switch (request.getOrderType()) {
            case BUY:
                Assert.isTrue(request.getQuantity() > 0, "Negative quantity");
                portfolio.addItem(product, request.getQuantity());
                break;
            case SELL:
                AtomicInteger qty = new AtomicInteger();

                portfolio
                        .getItems()
                        .stream()
                        .filter(portfolioItem ->
                                Objects.requireNonNull(portfolioItem.getProduct().getId()).equals(product.getId()))
                        .forEach(portfolioItem -> qty.addAndGet(portfolioItem.getQuantity()));

//                Integer qty = portfolioRepository.sumQuantityByProductId(portfolio.getId(), product.getId());
                if (qty.get() - request.getQuantity() < 0) {
                    throw new NegativeQuantityException("Negative portfolio balance (%d-%d=%d) for product %s (%s)"
                            .formatted(
                                    qty.get(),
                                    request.getQuantity(),
                                    qty.get() - request.getQuantity(),
                                    product.getId(),
                                    product.getReference()));
                }
                portfolio.addItem(product, -request.getQuantity());
                break;
            default:
                throw new OrderRejectedException("Unknown order type: " + request.getOrderType());
        }
    }

    private BookingOrder createOrder(OrderRequest request, Product product, TradingAccount tradingAccount,
                                     SystemAccount systemAccount) {
        // Market price is the total price for a given product and quantity
        final Money totalPrice = request.getUnitPrice().multiply(request.getQuantity());

        // Update account balances first
        updateAccountBalances(tradingAccount, systemAccount, request.getOrderType(), totalPrice);

        // Create and persist order
        BookingOrder bookingOrder = new BookingOrder()
                .withRandomID()
                .setAccount(tradingAccount)
                .setProduct(product)
                .setTotalPrice(totalPrice)
                .setReference(request.getOrderRef())
                .setOrderType(request.getOrderType())
                .setQuantity(request.getQuantity())
                .setApprovalDate(LocalDateTime.now())
                .setPlacedDate(LocalDateTime.now());

        switch (request.getOrderType()) {
            case SELL:
                // Credit booking account, debit system account
                bookingOrder.addOrderItem(tradingAccount, totalPrice);
                bookingOrder.addOrderItem(systemAccount, totalPrice.negate());
                break;
            case BUY:
                // Debit booking account, credit system account
                bookingOrder.addOrderItem(tradingAccount, totalPrice.negate());
                bookingOrder.addOrderItem(systemAccount, totalPrice);
                break;
            default:
                throw new OrderRejectedException("Unknown order type: " + request.getOrderType());
        }

        // Invariant check
        Money sum = Money.zero(totalPrice.getCurrency());
        for (BookingOrderItem itemEntity : bookingOrder.getItems()) {
            sum = sum.plus(itemEntity.getAmount());
        }

        if (!sum.equals(Money.zero(totalPrice.getCurrency()))) {
            throw new OrderRejectedException("Sum of order legs does not equal zero");
        }

        bookingOrder = orderRepository.save(bookingOrder);

        return bookingOrder;
    }

    private void updateAccountBalances(TradingAccount tradingAccount,
                                       SystemAccount systemAccount,
                                       OrderType orderType,
                                       Money totalPrice) {
        switch (orderType) {
            case SELL:
                // Credit customer account, debit system account
                tradingAccount.setBalance(tradingAccount.getBalance().plus(totalPrice));
                systemAccount.setBalance(systemAccount.getBalance().minus(totalPrice));
                break;
            case BUY:
                // Debit customer account, credit system account
                tradingAccount.setBalance(tradingAccount.getBalance().minus(totalPrice));
                systemAccount.setBalance(systemAccount.getBalance().plus(totalPrice));
                break;
            default:
                throw new OrderRejectedException("Unknown order type: " + orderType);
        }

        // Invariant check

        if (tradingAccount.getBalance().isNegative()) {
            throw new NegativeBalanceException(tradingAccount.getId());
        }

        if (systemAccount.getBalance().isNegative()) {
            throw new NegativeBalanceException(systemAccount.getId());
        }
    }

    @Override
    public BookingOrder getOrderByRef(String orderRef) {
        return orderRepository.findByReference(
                orderRef).orElseThrow(() -> new NoSuchOrderException(orderRef));
    }

    @Override
    public BookingOrder getOrderById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoSuchOrderException(id));
    }

    @Override
    public List<BookingOrder> findOrdersByAccountId(UUID accountId) {
        return orderRepository.findByAccountId(accountId);
    }

    @Override
    public Page<BookingOrder> findOrderPage(Pageable page) {
        return orderRepository.findAll(page);
    }

    @Override
    public List<BookingOrderItem> findOrderItemsByAccountId(UUID orderId, UUID accountId) {
        return orderItemRepository.findByOrderId(orderId, accountId);
    }
}
