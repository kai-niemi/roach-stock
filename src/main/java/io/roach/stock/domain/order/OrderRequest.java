package io.roach.stock.domain.order;

import io.roach.stock.domain.common.Money;

import java.util.UUID;

/**
 * Request object describing the details of a product buy or sell order.
 */
public final class OrderRequest {
    private UUID idempotencyKey;

    private UUID bookingAccountId;

    private OrderType orderType;

    private String productRef;

    private int quantity;

    private Money unitPrice;

    private OrderRequest() {
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getBookingAccountId() {
        return bookingAccountId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public String getProductRef() {
        return productRef;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public static AccountBuilder builder() {
        return new BuilderImpl();
    }

    public interface AccountBuilder {
        /**
         * @param accountRef a unique account reference scoped to the client namespace
         * @return this builder
         */
        OrderTypeBuilder bookingAccount(UUID accountRef);
    }

    public interface OrderTypeBuilder {
        /**
         * @param productRef a valid product reference
         * @return this builder
         */
        PriceBuilder buy(String productRef);

        /**
         * @param productRef a valid product reference
         * @return this builder
         */
        PriceBuilder sell(String productRef);

        PriceBuilder order(OrderType type, String productRef);
    }

    public interface PriceBuilder {
        /**
         * @param unitPrice the price per unit
         * @return this builder
         */
        QuantityBuilder unitPrice(Money unitPrice);
    }

    public interface QuantityBuilder {
        /**
         * @param quantity product quantity, must be > 0
         * @return this builder
         */
        OrderBuilder quantity(int quantity);
    }

    public interface OrderBuilder {
        OrderBuilder ref(UUID idempotencyKey);

        OrderRequest build();
    }

    private static final class BuilderImpl
            implements AccountBuilder, OrderTypeBuilder,
            PriceBuilder, QuantityBuilder, OrderBuilder {

        private final OrderRequest request = new OrderRequest();

        @Override
        public OrderBuilder ref(UUID idempotencyKey) {
            request.idempotencyKey = idempotencyKey;
            return this;
        }

        @Override
        public PriceBuilder buy(String productRef) {
            return order(OrderType.BUY, productRef);
        }

        @Override
        public PriceBuilder sell(String productRef) {
            return order(OrderType.SELL, productRef);
        }

        @Override
        public PriceBuilder order(OrderType type, String productRef) {
            request.orderType = type;
            request.productRef = productRef;
            return this;
        }

        @Override
        public QuantityBuilder unitPrice(Money unitPrice) {
            request.unitPrice = unitPrice;
            return this;
        }

        @Override
        public OrderBuilder quantity(int quantity) {
            request.quantity = quantity;
            return this;
        }

        @Override
        public OrderTypeBuilder bookingAccount(UUID accountId) {
            request.bookingAccountId = accountId;
            return this;
        }

        @Override
        public OrderRequest build() {
            return request;
        }
    }
}
