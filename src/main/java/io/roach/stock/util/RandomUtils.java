package io.roach.stock.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import io.roach.stock.domain.common.Money;

public abstract class RandomUtils {
    private RandomUtils() {
    }

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public static Money randomMoneyBetween(double low, double high, Currency currency) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return Money.of(String.format(Locale.US, "%.2f", random.nextDouble(low, high)), currency);
    }

    public static String randomString(int min) {
        byte[] buffer = new byte[min];
        ThreadLocalRandom random = ThreadLocalRandom.current();
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }

    public static <E> E selectRandom(E[] collection) {
        return selectRandom(Arrays.asList(collection));
    }

    public static <E> E selectRandom(Collection<E> collection) {
        List<E> givenList = new ArrayList<>(collection);
        return givenList.get(new SecureRandom().nextInt(givenList.size()));
    }

    @SuppressWarnings("unchecked")
    public static <K> K selectRandom(Set<K> set) {
        Object[] keys = set.toArray();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return (K) keys[random.nextInt(keys.length)];
    }

    @SuppressWarnings("unchecked")
    public static <K, V> K selectRandom(Map<K, V> set) {
        Object[] keys = set.keySet().toArray();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return (K) keys[random.nextInt(keys.length)];
    }
}
