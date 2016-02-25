import core.EDL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class EDLTest {

    private EDL testEDL;
    List<String> expectedStrings = new ArrayList<>();

    @Before
    public void createTest() {
        URL url = this.getClass().getClassLoader().getResource("test.edl");
        String correctFilePath = url != null ? url.getFile().replace("%20", " ") : "";
        String[] expected = new String[]{"A066C004_150602_R1KA", "C070_C001_0715GR", "A051C015_141126_R56W",
                "A050C003_141126_R56W", "ST5A3328.MOV", "MVI_4321"};
        this.expectedStrings.addAll(Arrays.asList(expected));
        Collections.sort(this.expectedStrings);
        this.testEDL = new EDL(Paths.get(correctFilePath));
    }

    public void runTest(boolean reels) {
        List<String> stringsFromEDL = this.testEDL.getInput(reels, false);
        Collections.sort(stringsFromEDL);
        assertEquals(this.expectedStrings, stringsFromEDL);
    }

    @Test
    public void testGetInputWithoutShortReels() {
        final boolean reels = false;
        runTest(reels);
    }

    @Test
    public void testGetInputWithShortReels() {
        String[] expected = new String[]{"A066R1KA", "A041R1KA", "A050R56W", "A051R56W","ST5A3328"};
        this.expectedStrings.addAll(Arrays.asList(expected));
        Collections.sort(this.expectedStrings);
        runTest(true);
    }

    @Test
    public void testGetLines() {
        String[] expected = new String[]{"A112_C008_1013WY", "K003_C003_1016RM", "A129_C005_1025LP"};
        List<String> expecteds = Arrays.asList(expected);
        Collections.sort(expecteds);
        URL url = this.getClass().getClassLoader().getResource("lines_sample");
        String correctFilePath = url != null ? url.getFile().replace("%20", " ") : "";
        EDL linesEDL = new EDL(Paths.get(correctFilePath));
        List<String> stringsFromEDL = linesEDL .getInput(false, true);
        Collections.sort(stringsFromEDL);
        assertEquals(expecteds, stringsFromEDL);
    }

}