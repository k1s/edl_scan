package core;

import helpers.FileHelper;
import view.*;
import exceptions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Checker {

    private final static String LINE_SEPARATOR = "   |---->   ";

    public List<String> checkLogs(final List<String> fromEDL, final String source) {
        Map<Path, List<String>> foundInFiles = searchFromEDLInFiles(Paths.get(source), fromEDL);
        return foundInFiles.entrySet().parallelStream()
                .map(entry -> "\n " + entry.getKey().toFile().getName() + LINE_SEPARATOR + entry.getValue().toString())
                .collect(Collectors.toList());

    }

    public void checkScanLogs(final List<String> fromEDL, final String source, final String logs,
                                     boolean checkFiles) {
        List<String> notFounded = checkScanNotFound(fromEDL, source, checkFiles);
        List<String> notFoundedAtAll = new CopyOnWriteArrayList<>(notFounded);
        checkLogs(notFounded, logs);
        System.out.println("NOT FOUND AT ALL " + notFoundedAtAll);
    }

    public static void checkScan(final List<String> fromEDL, final String scanDir, boolean checkFiles) {
        List<String> notFounded = checkScanNotFound(fromEDL, scanDir, checkFiles);
        System.out.println("NOT FOUND: " + notFounded);
    }

    private static List<String> checkScanNotFound(final List<String> fromEDL, final String scanDir,
                                                  boolean checkFiles) {
        ConsoleView.fromEDLOutput(fromEDL);
        Finder finder = new Finder(fromEDL);
        List<Path> fromSource = new ArrayList<>();
        try {
            fromSource.addAll(finder.getFromSource(Paths.get(scanDir), checkFiles));
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        List<String> fromSourceTOScan = fromEDL
                                        .parallelStream()
                                        .filter(s -> !fromSource.contains(s))
                                        .collect(Collectors.toList());
        return fromSourceTOScan;
    }

    private Map<Path,List<String>> searchFromEDLInFiles(final Path path, final List<String> fromEDL) {
        return searchInFiles(filesFilter(path), fromEDL);
    }


    private Map<Path, List<String>> searchInFiles(final Stream<Path> paths, final List<String> EDLstrings) {
        Map<Path, List<String>> foundInFiles = new HashMap<>();
        paths.parallel()
                    .forEach(file -> {
                        List<String> founds = searchFileForStrings(EDLstrings, file);
                        if (!founds.isEmpty())
                            foundInFiles.put(file, founds);
                    });

        return foundInFiles;
    }

    private Stream<Path> filesFilter(final Path path) {
        return files(path).parallel()
                .filter(p -> !p.toString().contains(".DS_Store") && !p.toFile().isDirectory());
    }

    private Stream<Path> files(final Path path) {
        Stream<Path> filesStream = Stream.empty();
        try {
            filesStream = Files.walk(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesStream;
    }

    private List<String> searchFileForStrings(final List<String> EDLstrings, final Path path) {
            return FileHelper.lines(path).stream().parallel()
                    .filter(line -> validContain(line, EDLstrings))
                    .map(line -> extractFoundFromLine(line, EDLstrings))
                    .distinct()
                    .collect(Collectors.toList());
    }

    private String extractFoundFromLine(String line, List<String> search) {
        return search.stream()
                .filter(line::contains)
                .findFirst()
                .get();
    }

    private boolean validContain(final String line, final List<String> search) {
        boolean contains = search.stream()
                .anyMatch(line::contains);
        return contains
                && !line.contains(".WAV")
                && !line.contains(".XML")
                && !line.contains(".wav")
                && !line.contains(".xml");
    }
}
