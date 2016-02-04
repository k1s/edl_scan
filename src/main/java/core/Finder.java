package main.java.core;

import main.java.dry.Assert;
import main.java.exceptions.NotMountedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Finder {

    private final List<String> fromEDL;

    public Finder (final List<String> fromEDLList) {
        Assert.require(!fromEDLList.isEmpty());
        this.fromEDL = fromEDLList.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public List<Path> getFromSource(final Path source, final boolean checkFiles) throws NotMountedException {
        checkSource(source);
        final Set<Path> filesWalk = Collections.newSetFromMap(new ConcurrentHashMap<>());
        return customWalk(source, filesWalk, checkFiles);
    }

    private void checkSource(final Path source) throws NotMountedException {
        Assert.requirePath(source);
        final File root = new File(String.valueOf(source));
        if (root.list().length == 0)
            throw new NotMountedException();
    }

    private List<Path> customWalk(final Path source, final Set<Path> filesWalk, final boolean checkFiles) {
        try {
            Files.list(source)
                    .parallel()
                    .filter(path -> !path.toString().contains(".DS_Store"))
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            checkDirName(path, filesWalk);
                            customWalk(path, filesWalk, checkFiles);
                        }
                        if (Files.isRegularFile(path)) {
                            checkFileName(path, filesWalk, checkFiles);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(filesWalk);
    }

    private void checkDirName(final Path fileName, final Set<Path> filesWalk) {
        this.fromEDL.forEach(str -> {
            if (pathToLowerCase(fileName).contains(str))
                filesWalk.add(fileName);
        });
    }

    private void checkFileName(final Path fileName, final Set<Path> filesWalk, final boolean checkFiles) {
        if (this.fromEDL.contains(pathToLowerCase(fileName)))
            filesWalk.add(fileName);
        if (checkFiles)
            checkDirName(fileName, filesWalk);
    }

    private String pathToLowerCase(final Path path) {
        return path.getFileName().toString().toLowerCase();
    }

}
