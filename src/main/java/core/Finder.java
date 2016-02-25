package core;

import exceptions.NotMountedException;
import helpers.Assert;
import helpers.FileHelper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Finder {

    private List<String> fromEDL;

    public Finder (final List<String> fromEDLList) {
        Assert.require(!fromEDLList.isEmpty());
        this.fromEDL = fromEDLList.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public List<Path> getFromSource(final Path source, final boolean checkFiles) throws NotMountedException {
        checkSource(source);
        Set<Path> filesWalk = Collections.newSetFromMap(new ConcurrentHashMap<>());
        List<Path> fromSource = FileHelper.files(source);
        return customWalk(fromSource, filesWalk, checkFiles);
    }

    private void checkSource(final Path source) throws NotMountedException {
        Assert.requirePath(source);
        final File root = new File(String.valueOf(source));
        if (root.list().length == 0)
            throw new NotMountedException();
    }

    private List<Path> customWalk(final List<Path> source, final Set<Path> filesWalk, final boolean checkFiles) {
        source.parallelStream()
                            .forEach(path -> {
                                if (Files.isDirectory(path)) {
                                    checkDirName(path, filesWalk);
                                    customWalk(FileHelper.files(path), filesWalk, checkFiles);
                                }
                                if (Files.isRegularFile(path)) {
                                    checkFileName(path, filesWalk, checkFiles);
                                }
                            });

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
