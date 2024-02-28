package io.roach.stock.domain.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.roach.stock.util.Money;
import io.roach.stock.domain.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Embeddable
public class PortfolioItem {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    protected PortfolioItem() {
    }

    protected PortfolioItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @JsonIgnore
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getProductValue() {
        return product.getSellPrice().multiply(quantity);
    }
}
