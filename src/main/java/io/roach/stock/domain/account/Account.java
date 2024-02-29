package io.roach.stock.domain.account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.roach.stock.domain.common.MoneyType;
import io.roach.stock.domain.common.Money;
import io.roach.stock.domain.common.AbstractAuditableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.Assert;

import java.util.UUID;

@Entity
@Table(name = "account")
@DynamicInsert
@DynamicUpdate
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "account_type",
        discriminatorType = DiscriminatorType.STRING,
        length = 15
)
public abstract class Account extends AbstractAuditableEntity<UUID> {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    })
    @CompositeType(MoneyType.class)
    private Money balance;

    protected Account() {
    }

    public Account(UUID id, String name, Money balance) {
        Assert.notNull(id, "id is null");
        Assert.notNull(name, "name is null");
        Assert.notNull(balance, "balance is null");

        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    @Override
    @JsonIgnore
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Money getBalance() {
        return balance;
    }

    public Account setBalance(Money balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                "} " + super.toString();
    }
}
