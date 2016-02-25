package core;

import helpers.Assert;
import helpers.FileHelper;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EDL {

    private final String SHORT_REEL_NAME = "(?<=\\d\\d\\d)(.*)(?=\\sV)";
    private final String REEL_NAME_FROM_SOURCE_FILE = "(?<=SOURCE FILE:)(.*)";
    private final String REEL_NAME_FROM_CLIP_NAME = "(?<=CLIP NAME:)(.*)";

    private final Path EDL;

    public EDL(final Path EDL) {
        Assert.requirePath(EDL);
        this.EDL = EDL;
    }

    public List<String> getInput(final boolean shortReelNames, final boolean namesFromLines) {
        final List<String> linesFromEDL = FileHelper.lines(this.EDL);

        if (linesFromEDL.isEmpty()) return new ArrayList<>();
        else if (namesFromLines) return linesFromEDL;
        else return extractFromEDL(getPatterns(shortReelNames), linesFromEDL);
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

    private List<String> extractFromEDL(final List<Pattern> patterns, final List<String> lines) {
        return patterns.parallelStream()
                .flatMap(pattern -> extractFromLines(pattern, lines).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> extractFromLines(final Pattern pattern, final List<String> lines) {
        Set<String> strOut = new HashSet<>();
        lines.forEach(line -> {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                strOut.add(matcher.group().trim());
            }
        });
        return new ArrayList<>(strOut);
    }

}
