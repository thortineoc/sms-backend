package com.sms.api.common;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list, Function<T, U> compareBy) {
        return list.stream()
                .sorted(Comparator.comparing(compareBy))
                .collect(Collectors.toList());
    }

    public static void ignoreException(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignored) {}
    }

    public static void runAll(Runnable... r) {
        for (Runnable runnable : r) {
            ignoreException(runnable);
        }
    }
}
