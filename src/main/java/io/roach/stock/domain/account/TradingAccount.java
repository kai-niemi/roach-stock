package io.roach.stock.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.roach.stock.domain.common.Money;
import io.roach.stock.domain.portfolio.Portfolio;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import org.springframework.util.Assert;

import java.util.UUID;

@Entity
@DiscriminatorValue("trading")
public class TradingAccount extends Account {
    /**
     * Parent account which is null for system accounts, non-null for trading accounts.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private SystemAccount parentAccount;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private UUID parentAccountId;

    /**
     * The portfolio tied to this account. Other side is responsible for the relationship.
     */
    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    @JsonIgnore
    private Portfolio portfolio;

    protected TradingAccount() {
    }

    public TradingAccount(UUID id, String name, Money balance, SystemAccount parentAccount) {
        super(id, name, balance);
        Assert.notNull(id, "parentAccount is null");
        this.parentAccount = parentAccount;
        this.parentAccountId = parentAccount.getId();
    }

    @JsonIgnore
    public UUID getParentAccountId() {
        return parentAccountId;
    }

    @JsonIgnore
    public SystemAccount getParentAccount() {
        return parentAccount;
    }

    public Portfolio createPortfolio() {
        this.portfolio = new Portfolio(this);
        return portfolio;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public String toString() {
        return "TradingAccount{" +
                ", parentAccountId=" + parentAccountId +
                "} " + super.toString();
    }
}
