package main.java.test.core;

import main.java.core.EDL;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EDLTest {

    private EDL testEDL;
    List<String> expectedStrings = new ArrayList<>();

    @Before
    public void createTest() {
        URL url = this.getClass().getResource("test.edl");
        String correctFilePath = url.getFile().replace("%20", " ");
        String[] expected = new String[]{"A066C004_150602_R1KA", "C070_C001_0715GR", "A051C015_141126_R56W",
                "A050C003_141126_R56W", "ST5A3328.MOV", "MVI_4321"};
        this.expectedStrings.addAll(Arrays.asList(expected));
        Collections.sort(this.expectedStrings);
        this.testEDL = new EDL(Paths.get(correctFilePath));
    }

    public void runTest(boolean checkFiles) {
        List<String> stringsFromEDL = this.testEDL.getInput(checkFiles);
        Collections.sort(stringsFromEDL);
        assertEquals(this.expectedStrings, stringsFromEDL);
    }

    @Test
    public void testGetInputWithoutShortReels() {
        final boolean checkFiles = false;
        runTest(checkFiles);
    }

    @Test
    public void testGetInputWithShortReels() {
        String[] expected = new String[]{"A066R1KA", "A041R1KA", "A050R56W", "A051R56W","ST5A3328"};
        this.expectedStrings.addAll(Arrays.asList(expected));
        Collections.sort(this.expectedStrings);
        runTest(true);
    }

}