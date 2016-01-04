package com.company.test.core;

import com.company.core.Scan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ScanTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void testScanWithoutFiles() throws Exception {
        URL url = this.getClass().getResource("scan");
        String correctFilePath = url.getFile().replace("%20", " ");
        List<String> edl = new ArrayList<>();
        edl.add("A001_C003");
        edl.add("test");
        edl.add("test1.mov");
        edl.add("test3");
        edl.add("A026R6S5");
        List<String> expectedStringsWithoutFiles = new ArrayList<>();
        String[] expected = new String[]{"A026R6S5", "A001_C003.RDC", "test1.mov"};
        expectedStringsWithoutFiles.addAll(Arrays.asList(expected));
        Collections.sort(expectedStringsWithoutFiles);
        boolean checkFiles = false;
        Scan scan = new Scan(edl, Paths.get(correctFilePath));
        List<Path> paths = scan.getFromSource(checkFiles);
        List<String> stringsFromPaths = paths.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(expectedStringsWithoutFiles, stringsFromPaths);



    }

    @Test
    public void testScanWithFiles() throws Exception {
        URL url = this.getClass().getResource("scan");
        String correctFilePath = url.getFile().replace("%20", " ");
        List<String> edl = new ArrayList<>();
        edl.add("A001_C003");
        edl.add("test");
        edl.add("test1.mov");
        edl.add("test3");
        edl.add("A026R6S5");
        List<String> expectedStringsWithFiles = new ArrayList<>();
        String[] expectedWith = new String[]{"A026R6S5", "A026R6S5.mov", "A001_C003.mov", "A001_C003_001.R3D",
                "A001_C003_002.R3D", "A001_C003.RDC", "test.mov", "test1.mov", "test3.ban"};
        expectedStringsWithFiles.addAll(Arrays.asList(expectedWith));
        Collections.sort(expectedStringsWithFiles);
        boolean checkFiles = true;
        Scan scan = new Scan(edl, Paths.get(correctFilePath));
        List<Path> pathsWithFiles = scan.getFromSource(checkFiles);
        List<String> stringsFromPathsWithFiles = pathsWithFiles.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(expectedStringsWithFiles, stringsFromPathsWithFiles);
    }

}