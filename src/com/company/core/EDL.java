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

    private List<String> lines;

    public EDL(final Path EDL) {
        Assert.requirePath(EDL);
        try {
            this.lines = Files.readAllLines(EDL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getInput(final boolean shortReelNames) throws Exception{
        Pattern reelNames = Pattern.compile(SHORT_REEL_NAME);
        Pattern sourceFiles = Pattern.compile(REEL_NAME_FROM_SOURCE_FILE);
        Pattern clipNames = Pattern.compile(REEL_NAME_FROM_CLIP_NAME);
        List<Pattern> patterns = new ArrayList<>();
        if (shortReelNames)
            patterns.add(reelNames);
        patterns.add(sourceFiles);
        patterns.add(clipNames);

        List<Callable<List<String>>> callables = new ArrayList<>();
        patterns.forEach(p -> callables.add(new Extractor(p)));

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<List<String>>> future = executorService.invokeAll(callables);

        Set<String> futures = new HashSet<>();

        for (Future<List<String>> f : future) {
            futures.addAll(f.get());
        }

        return new ArrayList<>(futures);
    }

    private class Extractor implements Callable {

        final private Pattern pattern;
        public Extractor(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public  List<String> call() throws Exception {
            List<String> strOut = new ArrayList<>();
            EDL.this.lines.forEach(line -> {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    strOut.add(matcher.group().trim());
                }
            });
            return new ArrayList<>(strOut);
        }
    }
}
