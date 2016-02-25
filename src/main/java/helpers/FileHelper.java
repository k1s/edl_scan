package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    public static List<String> lines(final Path path) {

        CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.IGNORE);

        List<String> strings = new ArrayList<>();
        try (Reader reader = Channels.newReader(FileChannel.open(path), dec, -1);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
             strings.addAll(bufferedReader.lines()
             .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    public static List<Path> files(Path source) {
        List<Path> filesList = new ArrayList<>();
        try {
            filesList.addAll(Files.list(source)
                    .filter(path -> !path.toString().contains(".DS_Store") &
                                    !path.toString().contains(".Trashes"))
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesList;
    }
}
