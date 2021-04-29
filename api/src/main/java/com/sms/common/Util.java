package com.sms.common;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class Util {

    private Util() {
    }

    public static <K, V> List<V> getOrEmpty(Map<K, ? extends List<V>> map, K key) {
        List<V> value = map.get(key);
        return value == null
                ? Collections.emptyList()
                : value;
    }

    public static <T> Optional<T> getFirst(List<T> list) {
        return list.isEmpty()
                ? Optional.empty()
                : Optional.of(list.get(0));
    }

    public static <K, V> Optional<V> getOpt(Map<K, V> map, K key) {
        return Optional.ofNullable(map.get(key));
    }

    public static <K, V> V getOrThrow(Map<K, V> map, K key, Supplier<? extends RuntimeException> throwable) {
        return Optional.ofNullable(map.get(key)).orElseThrow(throwable);
    }
}
