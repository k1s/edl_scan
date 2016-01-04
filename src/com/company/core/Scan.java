package com.company.core;

import com.company.exceptions.NotMountedException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Scan {

    private final List<String> fromEDL;
    private final Path source;

    public Scan(List<String> fromEDL, Path source) {
        this.fromEDL = fromEDL.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        this.source = source;
    }

    public List<Path> getFromSource(final boolean checkFiles) throws NotMountedException {
        File root = new File(String.valueOf(source));
        if (root.list().length == 0)
            throw new NotMountedException();
        Set<Path> filesWalk = Collections.newSetFromMap(new ConcurrentHashMap<>());
        return customWalk(source, filesWalk, checkFiles);
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
        fromEDL.forEach(str -> {
            if (fileName.getFileName().toString().toLowerCase().contains(str))
                filesWalk.add(fileName);
        });
    }

    private void checkFileName(final Path fileName, final Set<Path> filesWalk, final boolean checkFiles) {
        if (fromEDL.contains(fileName.getFileName().toString().toLowerCase()))
            filesWalk.add(fileName);
        if (checkFiles)
            checkDirName(fileName, filesWalk);
    }

}
