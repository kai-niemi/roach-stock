package io.roach.stock.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.roach.stock.util.Money.SEK;
import static io.roach.stock.util.Money.kronor;
import static io.roach.stock.util.Money.of;
import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    public void whenUsingBinaryArithmetics_expectSuccess() {
        assertEquals(
                of("100.00", SEK),
                of("80.00", SEK).plus(of("20.00", SEK)));

        assertEquals(
                of("19.50", SEK),
                of("10.05", SEK).plus(of("9.95", SEK), of("-0.50", SEK)));

        assertEquals(
                of("80.00", SEK),
                of("100.00", SEK).minus(of("20.00", SEK)));

        assertEquals(
                of("100.00", SEK),
                of("10.00", SEK).multiply(10));

        assertEquals(
                of("20.00", SEK),
                of("100.00", SEK).divide(5));

        assertEquals(
                of("16.67", SEK),
                of("100.00", SEK).divideAndRound(6));

        assertEquals(
                of("0.00", SEK),
                of("100.00", SEK).remainder(100));
    }

    @Test
    public void whenUsingComparisonOperators_expectSuccess() {
        assertTrue(of("110.00", SEK).isGreaterThan(of("100.00", SEK)));
        assertTrue(of("100.00", SEK).isGreaterThanOrEqualTo(of("100.00", SEK)));
        assertTrue(of("99.00", SEK).isLessThan(of("100.00", SEK)));
        assertTrue(of("100.00", SEK).isLessThanOrEqualTo(of("100.00", SEK)));
        assertTrue(of("100.00", SEK).isSameCurrency(of("100.00", SEK)));
    }

    @Test
    public void whenUsingUnaryOperators_expectExpectImmutability() {
        Money m = of("15.00", "SEK");
        assertNotSame(m, m.negate().negate());
        assertEquals(m, m.negate().negate());
    }

    @Test
    public void whenMixingCurrencies_expectFail() {
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").minus(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").plus(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").isGreaterThan(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").isGreaterThanOrEqualTo(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").isLessThan(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
        assertThrows(CurrencyMismatchException.class, () -> {
            of("15.00", "SEK").isLessThanOrEqualTo(of("0.00", "USD"));
            Assertions.fail("Must not succeed");
        });
    }

    @Test
    public void binaryArithmetics() {
        Assertions.assertEquals(
                kronor("100.00"),
                kronor("80.00").plus(kronor("20.00")));

        Assertions.assertEquals(
                kronor("19.50"),
                kronor("10.05").plus(kronor("9.95"), kronor("-0.50")));

        Assertions.assertEquals(
                kronor("80.00"),
                kronor("100.00").minus(kronor("20.00")));

        Assertions.assertEquals(
                kronor("100.00"),
                kronor("10.00").multiply(10));

        Assertions.assertEquals(
                kronor("20.00"),
                kronor("100.00").divide(5));

        Assertions.assertEquals(
                kronor("0.00"),
                kronor("100.00").remainder(100));

        Assertions.assertEquals(
                kronor("100.00"),
                kronor("100.00").max(kronor("80.00")));

        Assertions.assertEquals(
                kronor("80.00"),
                kronor("100.00").min(kronor("80.00")));

        Assertions.assertTrue(kronor("110.00").isGreaterThan(kronor("100.00")));
        Assertions.assertTrue((kronor("100.00").isGreaterThanOrEqualTo(kronor("100.00"))));
        Assertions.assertTrue(kronor("99.00").isLessThan(kronor("100.00")));
        Assertions.assertTrue(kronor("100.00").isLessThanOrEqualTo(kronor("100.00")));
        Assertions.assertTrue(kronor("100.00").isSameCurrency(kronor("100.00")));
    }

    @Test
    public void unaryArithmetics() {
        Assertions.assertEquals(
                kronor("-100.00"),
                kronor("100.00").negate());

        Assertions.assertTrue(kronor("-100.00").isNegative());
        Assertions.assertTrue(kronor("+100.00").isPositive());
        Assertions.assertTrue(kronor("-0.00").isZero());
        Assertions.assertTrue(kronor("0.00").isZero());
        Assertions.assertTrue(kronor("+0.00").isZero());
    }

    @Test
    public void failOnMalformedAmount() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0");
        });
    }

    @Test
    public void failOnWrongFractionDigits() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0.0");
        });
    }

    @Test
    public void failOnWrongFractionDigits2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0.000");
        });
    }
}
