package core;

import view.*;
import exceptions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Checker {

    private final List<String> found = new ArrayList<>();


    public void checkLogs(final List<String> fromEDL, final String source, final List<String> notFoundedAtAll) {
        ConsoleView.fromEDLOutput(fromEDL);
        fromEDL.parallelStream().forEach(s -> {
            seeLogs(Paths.get(source), s, notFoundedAtAll);
        });
    }

    public void checkScanLogs(final List<String> fromEDL, final String source, final String logs,
                                     boolean checkFiles) {
        List<String> notFounded = checkScanNotFound(fromEDL, source, checkFiles);
        List<String> notFoundedAtAll = new CopyOnWriteArrayList<>(notFounded);
        checkLogs(notFounded, logs, notFoundedAtAll);
        System.out.println("NOT FOUND AT ALL " + notFoundedAtAll);
    }

    public static void checkScan(final List<String> fromEDL, final String scanDir, boolean checkFiles) {
        List<String> notFounded = checkScanNotFound(fromEDL, scanDir, checkFiles);
        System.out.println("NOT FOUND: " + notFounded);
    }

    public List<String> getFound() {
        return this.found;
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

    private void seeLogs(final Path path, final String strEDL, List<String> notFoundedAtAll) {
        try {
            Files.list(path).parallel()
                    .filter(p -> !p.toString().contains(".DS_Store"))
                    .forEach(file -> {
                        if (file.toFile().isDirectory()) {
                            seeLogs(file, strEDL, notFoundedAtAll);
                        }
                        if (file.toFile().isFile()) {
                            seeFileForStrings(strEDL, file, notFoundedAtAll);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void seeFileForStrings(final String search, final Path path, final List<String> notFoundedAtAll) {
        try {
            Files.lines(path).parallel()
                    .forEach(str -> {
                        if (str.contains(search)
                            && !str.contains(".WAV")
                            && !str.contains(".XML")
                            && !str.contains(".wav")
                            && !str.contains(".xml")) {
                            String nextFound = search + " is founded in " + path + ": " + str;
                            this.found.add(nextFound);
                            System.out.println(nextFound);
                        }
                notFoundedAtAll.remove(search);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
