package io.roach.stock.domain.order;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.CompositeType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.stock.domain.account.Account;
import io.roach.stock.domain.common.AbstractEntity;
import io.roach.stock.domain.common.Money;
import io.roach.stock.domain.common.MoneyType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking_order_item")
public class BookingOrderItem extends AbstractEntity<BookingOrderItem.Id> {
    @EmbeddedId
    private BookingOrderItem.Id id = new BookingOrderItem.Id();

    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "transfer_amount", nullable = false)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "transfer_currency", length = 3, nullable = false))
    })
    @CompositeType(MoneyType.class)
    private Money amount;

    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "running_balance", nullable = false)),
            @AttributeOverride(name = "currency",
                    column = @Column(name = "running_currency", length = 3, nullable = false))
    })
    @CompositeType(MoneyType.class)
    private Money runningBalance;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "account_id", referencedColumnName = "id", updatable = false, insertable = false)
    })
    private Account account;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "order_id", referencedColumnName = "id", updatable = false, insertable = false)
    })
    private BookingOrder order;

    protected BookingOrderItem() {
    }

    protected BookingOrderItem(Money amount, Account account, BookingOrder order) {
        this.amount = amount;
        this.account = account;
        this.order = order;
        this.runningBalance = account.getBalance();
        this.id.accountId = account.getId();
        this.id.orderId = order.getId();
    }

    @Override
    @JsonIgnore
    public Id getId() {
        return id;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getRunningBalance() {
        return runningBalance;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "account_id")
        private UUID accountId;

        @Column(name = "order_id")
        private UUID orderId;

        protected Id() {
        }

        public UUID getAccountId() {
            return accountId;
        }

        public UUID getOrderId() {
            return orderId;
        }

        public void setAccountId(UUID accountId) {
            this.accountId = accountId;
        }

        public void setOrderId(UUID orderId) {
            this.orderId = orderId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Id id = (Id) o;

            if (!accountId.equals(id.accountId)) {
                return false;
            }
            return orderId.equals(id.orderId);
        }

        @Override
        public int hashCode() {
            int result = accountId.hashCode();
            result = 31 * result + orderId.hashCode();
            return result;
        }
    }
}
