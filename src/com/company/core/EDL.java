package com.company.core;

import com.company.dry.Assert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EDL {

    private final String SHORT_REEL_NAME = "(?<=\\d\\d\\d)(.*)(?=\\sV)";
    private final String REEL_NAME_FROM_SOURCE_FILE = "(?<=SOURCE FILE:)(.*)";
    private final String REEL_NAME_FROM_CLIP_NAME = "(?<=CLIP NAME:)(.*)";

    private Path EDL;
    public EDL(final Path EDL) {
        Assert.requirePath(EDL);
        this.EDL = EDL;
    }

    public List<String> getInput(final boolean shortReelNames) {
        List<String> linesFromEDL = getLinesFromEDL(this.EDL);
        if (linesFromEDL.isEmpty())
            return new ArrayList<>();
        else
            return extractFromEDL(getPatterns(shortReelNames), linesFromEDL);
    }

    private List<String> getLinesFromEDL(Path edl) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(edl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private List<Pattern> getPatterns(final boolean shortReelNames) {
        Pattern reelNames = Pattern.compile(SHORT_REEL_NAME);
        Pattern sourceFiles = Pattern.compile(REEL_NAME_FROM_SOURCE_FILE);
        Pattern clipNames = Pattern.compile(REEL_NAME_FROM_CLIP_NAME);
        List<Pattern> patterns = new ArrayList<>();
        if (shortReelNames)
            patterns.add(reelNames);
        patterns.add(sourceFiles);
        patterns.add(clipNames);
        return patterns;
    }

    private List<String> extractFromEDL(List<Pattern> patterns, List<String> lines) {
        return patterns.parallelStream()
                .flatMap(pattern -> extractFromLines(pattern, lines).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> extractFromLines(Pattern pattern, List<String> lines) {
        List<String> strOut = new ArrayList<>();
        lines.forEach(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                strOut.add(matcher.group().trim());
            }
        });
        return new ArrayList<>(strOut);
    }

}
