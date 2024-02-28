package io.roach.stock.domain.product;

import java.util.UUID;

import io.roach.stock.util.Money;

public class ProductBuilder {
    private UUID id;

    private UUID foreignId;

    private String reference;

    private Money buyPrice;

    private Money sellPrice;

    public ProductBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withForeignId(UUID foreignId) {
        this.foreignId = foreignId;
        return this;
    }

    public ProductBuilder withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public ProductBuilder withBuyPrice(Money buyPrice) {
        this.buyPrice = buyPrice;
        return this;
    }

    public ProductBuilder withSellPrice(Money sellPrice) {
        this.sellPrice = sellPrice;
        return this;
    }

    public Product build() {
        if (foreignId == null) {
            this.foreignId = id;
        }
        return new Product(id, foreignId, reference, buyPrice, sellPrice);
    }
}