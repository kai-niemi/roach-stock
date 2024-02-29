package io.roach.stock.doubles;

import java.util.UUID;

import io.roach.stock.domain.product.Product;
import io.roach.stock.domain.common.Money;

import static io.roach.stock.domain.common.Money.euro;

/**
 * Account and product test doubles.
 */
public abstract class TestDoubles {
    public static final UUID SYSTEM_ACCOUNT_A = UUID.randomUUID();

    public static final UUID USER_ACCOUNT_ALICE = UUID.randomUUID();

    public static final UUID USER_ACCOUNT_BOB = UUID.randomUUID();

    public static final UUID USER_ACCOUNT_BOBBY_TABLES = UUID.randomUUID();

    public static final Product APPLE_A
            = Product.builder().withId(UUID.randomUUID()).withReference("Apple A").withBuyPrice(euro("30.50"))
            .withSellPrice(euro("29.85")).build();

    public static final Product APPLE_B
            = Product.builder().withId(UUID.randomUUID()).withReference("Apple B").withBuyPrice(euro("20.50"))
            .withSellPrice(euro("19.50")).build();

    public static final Product APPLE_C
            = Product.builder().withId(UUID.randomUUID()).withReference("Apple C").withBuyPrice(euro("10.50"))
            .withSellPrice(euro("9.50")).build();

    public static final Product NOKIA_A
            = Product.builder().withId(UUID.randomUUID()).withReference("Nokia A").withBuyPrice(euro("1.55"))
            .withSellPrice(euro("1.60")).build();

    public static final Product NOKIA_B
            = Product.builder().withId(UUID.randomUUID()).withReference("Nokia B").withBuyPrice(euro("1.56"))
            .withSellPrice(euro("1.62")).build();

    public static final Product NOKIA_C
            = Product.builder().withId(UUID.randomUUID()).withReference("Nokia C").withBuyPrice(euro("1.57"))
            .withSellPrice(euro("1.63")).build();

    public static final Product MOTOROLA_A
            = Product.builder().withId(UUID.randomUUID()).withReference("Motorola A").withBuyPrice(euro("4.65"))
            .withSellPrice(euro("4.73")).build();

    public static final Product MOTOROLA_B
            = Product.builder().withId(UUID.randomUUID()).withReference("Motorola B").withBuyPrice(euro("4.75"))
            .withSellPrice(euro("4.83")).build();

    public static final Product MOTOROLA_C
            = Product.builder().withId(UUID.randomUUID()).withReference("Motorola C").withBuyPrice(euro("4.85"))
            .withSellPrice(euro("4.93")).build();

    public static final Product[] ALL_PRODUCTS = {
            TestDoubles.APPLE_A, TestDoubles.APPLE_B, TestDoubles.APPLE_C,
            TestDoubles.NOKIA_A, TestDoubles.NOKIA_B, TestDoubles.NOKIA_C,
            TestDoubles.MOTOROLA_A, TestDoubles.MOTOROLA_B, TestDoubles.MOTOROLA_C
    };

    public static final Money TRADER_INITIAL_BALANCE = euro("10000000.00");

    public static final Money USER_INITIAL_BALANCE = euro("2000000.00");

    private TestDoubles() {
    }
}
