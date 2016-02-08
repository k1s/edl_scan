package helpers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public final class Assert {
    public static void require(final Boolean condition) {
        if (!condition)
            throw new IllegalArgumentException();
    }

    public static void require(Boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static <T> void require(Predicate<T> predicate, T testVal) {
        if (!predicate.test(testVal))
            throw new IllegalArgumentException();
    }

    public static <T> void require(Predicate<T> predicate, T testVal, String message) {
        if (!predicate.test(testVal))
            throw new IllegalArgumentException(message);
    }

    public static void requirePath(Path path) {
        if (!Files.exists(path))
            throw new IllegalArgumentException("Path does not exist");
    }
}
