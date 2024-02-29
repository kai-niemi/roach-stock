package io.roach.stock.domain.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

public class MoneyType implements CompositeUserType<Money> {
    public static final MoneyType INSTANCE = new MoneyType();

    @Override
    public Object getPropertyValue(Money component, int property) throws HibernateException {
        return switch (property) {
            case 0 -> component.getAmount();
            case 1 -> component.getCurrency();
            default -> throw new HibernateException("Illegal property index: " + property);
        };
    }

    @Override
    public Money instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {
        final BigDecimal amount = values.getValue(0, BigDecimal.class);
        final Currency currency = values.getValue(1, Currency.class);
        if (amount == null && currency == null) {
            return null;
        }
        return new Money(amount, currency);
    }

    public static class MoneyEmbeddable {
        private BigDecimal amount;

        private Currency currency;
    }

    @Override
    public Class<?> embeddable() {
        return MoneyEmbeddable.class;
    }

    @Override
    public Class<Money> returnedClass() {
        return Money.class;
    }

    @Override
    public boolean equals(Money x, Money y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Money x) {
        return Objects.hashCode(x);
    }

    @Override
    public Money deepCopy(Money value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Money value) {
        return value;
    }

    @Override
    public Money assemble(Serializable cached, Object owner) {
        return (Money) cached;
    }

    @Override
    public Money replace(Money detached, Money managed, Object owner) {
        return detached;
    }
}