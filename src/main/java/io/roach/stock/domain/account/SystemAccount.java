package io.roach.stock.domain.account;

import io.roach.stock.domain.common.Money;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("system")
public class SystemAccount extends Account {
    protected SystemAccount() {
    }

    public SystemAccount(UUID id, String name, Money balance) {
        super(id, name, balance);
    }
}
