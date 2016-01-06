package com.company.core;

import com.company.DRY.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EDL {

    private final String SHORT_REEL_NAME = "(?<=\\d\\d\\d)(.*)(?=\\sV)";
    private final String REEL_NAME_FROM_SOURCE_FILE = "(?<=SOURCE FILE:)(.*)";
    private final String REEL_NAME_FROM_CLIP_NAME = "(?<=CLIP NAME:)(.*)";

    private volatile Set<String> strOut = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private List<String> lines;

    public EDL(final Path EDL) {
        Assert.require(path -> Files.exists(path), EDL, "Path to EDL does not exist");
        try {
            this.lines = Files.readAllLines(EDL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getInput(final boolean shortReelNames) {
        Pattern reelNames = Pattern.compile(SHORT_REEL_NAME);
        Pattern sourceFiles = Pattern.compile(REEL_NAME_FROM_SOURCE_FILE);
        Pattern clipNames = Pattern.compile(REEL_NAME_FROM_CLIP_NAME);
        ExecutorService executorService = Executors.newCachedThreadPool();
        if (shortReelNames)
            executorService.execute(new Thread(() -> extractFromLines(reelNames)));
        executorService.execute(new Thread(() -> extractFromLines(clipNames)));
        executorService.execute(new Thread(() -> extractFromLines(sourceFiles)));
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return outStrings();
    }

    private void extractFromLines(Pattern pattern) {
                this.lines.forEach(line -> {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        this.strOut.add(matcher.group().trim());
                    }
                });
    }

    private List<String> outStrings() {
        return new ArrayList<>(this.strOut);
    }
}
