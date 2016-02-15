package helpers;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Assert {
    public static void require(final Boolean condition) {
        if (!condition)
            throw new IllegalArgumentException();
    }

    public static void require(final Boolean condition, final String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void requirePath(final Path path) {
        if (!Files.exists(path))
            throw new IllegalArgumentException(path + "    Path does not exist");
    }
}
