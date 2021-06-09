package com.sms.api.common;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Util {

    private Util() {
    }

    public static <V> void ifNotEmpty(Collection<V> collection, Consumer<Collection<V>> consumer) {
        if (!collection.isEmpty()) {
            consumer.accept(collection);
        }
    }

    public static <K, V> List<V> getAll(Map<K, V> map, Set<K> keys) {
        List<V> list = new ArrayList<>();
        for (K key : keys) {
            list.add(map.get(key));
        }
        return list;
    }

    public static <K, V> Map<K, V> index(Collection<V> collection, Function<V, K> keyFunction) {
        return collection.stream().collect(Collectors.toMap(keyFunction, Function.identity()));
    }

    public static <T> Collector<T, ?, List<T>> collectSorted(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new), l-> { l.sort(c); return l; });
    }

    public static <T, R> List<R> map(List<T> collection, Function<T, R> mapper) {
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public static <T, R> Set<R> map(Set<T> collection, Function<T, R> mapper) {
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    public static <T> T pop(List<T> list) {
        return list.remove(list.size() - 1);
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
