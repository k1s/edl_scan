package com.company.core;

import com.company.dry.Assert;
import com.company.exceptions.NotMountedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Scan {

    private final List<String> fromEDL;

    public Scan(List<String> fromEDL) {
        Assert.require(!fromEDL.isEmpty());
        this.fromEDL = fromEDL.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        fromEDL.add("");
    }

    public List<Path> getFromSource(final Path source, final boolean checkFiles) throws NotMountedException {
        checkSource(source);
        Set<Path> filesWalk = Collections.newSetFromMap(new ConcurrentHashMap<>());
        return customWalk(source, filesWalk, checkFiles);
    }

    private void checkSource(final Path source) throws NotMountedException {
        Assert.requirePath(source);
        File root = new File(String.valueOf(source));
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
