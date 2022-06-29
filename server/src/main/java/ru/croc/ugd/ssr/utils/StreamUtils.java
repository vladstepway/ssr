package ru.croc.ugd.ssr.utils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * utils.
 */
public class StreamUtils {
    /**
     * distinct by key.
     * @param keyExtractor keyExtractor
     * @param <T> param
     * @return result
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * not.
     * @param predicate predicate
     * @param <T> param
     * @return result
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    /**
     * indexExists.
     * @param list list
     * @param index index
     * @return result
     */
    public static boolean indexExists(final List list, final int index) {
        return index >= 0 && index < list.size();
    }


    public static class OptionalConsumer<T> {
        private Optional<T> optional;

        private OptionalConsumer(Optional<T> optional) {
            this.optional = optional;
        }

        public static <T> OptionalConsumer<T> of(Optional<T> optional) {
            return new OptionalConsumer<>(optional);
        }

        public OptionalConsumer<T> ifPresent(Consumer<T> consumer) {
            optional.ifPresent(consumer);
            return this;
        }

        public OptionalConsumer<T> ifNotPresent(Runnable runnable) {
            if (!optional.isPresent()) {
                runnable.run();
            }
            return this;
        }
    }

    public static <T> Optional<T> or(Optional<T> optional, Supplier<Optional<T>> fallback) {
        return optional.isPresent() ? optional : fallback.get();
    }
}
