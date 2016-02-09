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
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Checker {

    private final static String LINE_SEPARATOR = "   |---->   ";

    public List<String> checkLogs(final List<String> fromEDL, final String source) {
        Map<String, List<String>> foundInFiles = searchFromEDLInLogFiles(Paths.get(source), fromEDL);
        return foundInLogsFilesToList(foundInFiles);
    }

    public String checkScanLogs(final List<String> fromEDL, final String source, final String logs) { //TODO
        List<String> notFound = checkScanDirectoryForFilesFromEDL(fromEDL, source);
        Map<String, List<String>> foundInLogFiles = searchFromEDLInLogFiles(Paths.get(logs), notFound);
        String nf = ("\nNOT FOUND " + notFound);
        String finl = ("\nFOUND IN LOGS " + foundInLogFiles);
        String nfaa = ("\nNOT FOUND AT ALL " + notFoundAtAll(foundInLogFiles, notFound));
        return nf + finl + nfaa;
    }

    public List<String> checkScan(final List<String> fromEDL, final String scanDir) {
            return checkScanDirectoryForFilesFromEDL(fromEDL, scanDir);
    }

    private List<String> foundInLogsFilesToList(final Map<String, List<String>> foundInFiles) {
        return foundInFiles.entrySet().parallelStream()
                .map(entry -> "\n" + entry.getKey() + LINE_SEPARATOR + entry.getValue().toString())
                .collect(Collectors.toList());
    }

    private List<String> notFoundAtAll(final Map<String, List<String>> foundInLogFiles, final List<String> notFound) {
        List<String> inLogs = foundInLogFiles.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Set<String> notFoundSet = new TreeSet<>(notFound);
        Set<String> inLogsSet = new TreeSet<>(inLogs);
        notFoundSet.removeAll(inLogsSet);
        return new ArrayList<>(notFoundSet);
    }
    private List<String> checkScanDirectoryForFilesFromEDL(final List<String> fromEDL, final String scanDir) {
        boolean checkFiles = true;
        Finder finder = new Finder(fromEDL);
        List<Path> fromSourcePaths = new ArrayList<>();
        try {
            fromSourcePaths.addAll(finder.getFromSource(Paths.get(scanDir), checkFiles));
        } catch (NotMountedException e) {
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        List<String> fromSource = fromSourcePaths.stream()
                .map(Path::toString)
                .collect(Collectors.toList());
        return findFilesFromEDLInListFromScanFolder(fromEDL, fromSource);
    }

    private List<String> findFilesFromEDLInListFromScanFolder(final List<String> fromEDL, final List<String> fromScan) {
        BiFunction<List<String>, String, Boolean> checkSourceList = (list, line) ->
                list.stream()
                .anyMatch(x -> x.contains(line));

        return fromEDL.parallelStream()
                .filter(str -> !checkSourceList.apply(fromScan, str))
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> searchFromEDLInLogFiles(final Path path, final List<String> fromEDL) {
        return searchInFiles(filesFilter(path), fromEDL);
    }


    private Map<String, List<String>> searchInFiles(final Stream<Path> paths, final List<String> EDLstrings) {
        Map<String, List<String>> getStringsForEachFileAndDeleteDuplicates =
                paths.parallel()
                .collect(Collectors.toMap(
                        path -> path.toFile().getName(),
                        path -> searchFileForStrings(EDLstrings, path),
                        (list1, list2) -> Stream.concat(list1.stream(), list2.stream())
                                                .distinct()
                                                .collect(Collectors.toList())
                        ));

        return  getStringsForEachFileAndDeleteDuplicates
                .entrySet()
                .parallelStream()
                .filter(entry -> !(entry.getValue().isEmpty()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
