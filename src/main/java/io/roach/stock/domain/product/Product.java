package io.roach.stock.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.roach.stock.domain.common.AbstractEntity;
import io.roach.stock.domain.common.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "product")
public class Product extends AbstractEntity<UUID> {
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    @Id
    private UUID id;

    @Column(name = "foreign_id")
    private UUID foreignId;

    @Column(name = "reference", length = 128, unique = true, nullable = false)
    private String reference;

    @Column(name = "buy_price", nullable = false)
    private BigDecimal buyPrice;

    @Column(name = "sell_price", nullable = false)
    private BigDecimal sellPrice;

    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    protected Product() {
    }

    public Product(UUID id, UUID foreignId, String reference, Money buyPrice, Money sellPrice) {
        if (!buyPrice.isSameCurrency(sellPrice)) {
            throw new IllegalArgumentException("Mixed currency");
        }
        this.id = id;
        this.foreignId = foreignId;
        this.reference = reference;
        this.buyPrice = buyPrice.getAmount();
        this.sellPrice = sellPrice.getAmount();
        this.currency = buyPrice.getCurrency();
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String productRef) {
        this.reference = productRef;
    }

    public Money getBuyPrice() {
        return Money.of(buyPrice, currency);
    }

    public Money getSellPrice() {
        return Money.of(sellPrice, currency);
    }

    public void setBuyPrice(Money buyPrice) {
        this.buyPrice = buyPrice.getAmount();
    }

    public void setSellPrice(Money sellPrice) {
        this.sellPrice = sellPrice.getAmount();
    }

    public Currency getCurrency() {
        return currency;
    }

    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", currency=" + currency +
                "} " + super.toString();
    }
}
