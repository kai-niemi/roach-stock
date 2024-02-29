package io.roach.stock.domain.common;

import java.util.Currency;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {
    @Override
    public String convertToDatabaseColumn(Currency attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(String data) {
        if (data == null) {
            return null;
        }
        return Currency.getInstance(data);
    }
}
