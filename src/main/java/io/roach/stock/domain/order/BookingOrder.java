package io.roach.stock.domain.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.roach.stock.domain.account.Account;
import io.roach.stock.domain.common.AbstractEntity;
import io.roach.stock.domain.common.MoneyType;
import io.roach.stock.domain.product.Product;
import io.roach.stock.domain.common.Money;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CompositeType;

@Entity
@Table(name = "booking_order")
public class BookingOrder extends AbstractEntity<UUID> {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false)
    @PrimaryKeyJoinColumn
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "total_amount", nullable = false)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "total_currency", length = 3, nullable = false))
    })
    @CompositeType(MoneyType.class)
    private Money totalPrice;

    @Column(name = "order_type", length = 10)
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "placed_at", nullable = false, updatable = false)
    private LocalDateTime placedDate;

    @Column(name = "approved_at")
    private LocalDateTime approvalDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
    private List<BookingOrderItem> items = new ArrayList<>();

    protected BookingOrder() {
    }

    public BookingOrder(UUID id,
                        Account account,
                        OrderType type,
                        Product product,
                        int quantity,
                        Money totalPrice,
                        LocalDateTime approvalDate) {
        this.id = id;
        this.account = account;
        this.orderType = type;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.approvalDate = approvalDate;
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public BookingOrder withRandomID() {
        this.id = UUID.randomUUID();
        return this;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    public BookingOrder setAccount(Account account) {
        this.account = account;
        return this;
    }

    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public BookingOrder setProduct(Product product) {
        this.product = product;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public BookingOrder setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public BookingOrder setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BookingOrder setOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public LocalDateTime getPlacedDate() {
        return placedDate;
    }

    public BookingOrder setPlacedDate(LocalDateTime placedDate) {
        this.placedDate = placedDate;
        return this;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public BookingOrder setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
        return this;
    }

    @JsonIgnore
    public List<BookingOrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BookingOrderItem addOrderItem(Account account, Money amount) {
        BookingOrderItem item = new BookingOrderItem(amount, account, this);
        items.add(item);
        return item;
    }
}
