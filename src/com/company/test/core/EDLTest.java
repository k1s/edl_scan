package com.company.test.core;

import com.company.core.EDL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;



public class EDLTest {

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
    public void out() {
        URL url = this.getClass().getResource("test.edl");
        System.out.println(url);
        assertEquals(url.toString(), outContent.toString());
    }

    @Test
    public void testGetInput() throws Exception {
        EDL testEDL = new EDL();
        URL url = this.getClass().getResource("test.edl");
        String correctFilePath = url.getFile().replace("%20", " ");
        List<String> expectedStrings = new ArrayList<>();
        String[] expected = new String[]{"A066R1KA", "A066C004_150602_R1KA", "MVI_4321",
        "A041R1KA", "C070_C001_0715GR", "A050R56W", "A051R56W", "A051C015_141126_R56W",
        "A050C003_141126_R56W", "ST5A3328", "ST5A3328.MOV"};
        expectedStrings.addAll(Arrays.asList(expected));
        Collections.sort(expectedStrings);
        boolean useShortReelNames = true;
        List<String> stringsFromEDL = testEDL.getInput(correctFilePath, useShortReelNames);
        Collections.sort(stringsFromEDL);
        assertEquals(expectedStrings, stringsFromEDL);
    }
}