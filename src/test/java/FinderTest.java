import core.Finder;
import exceptions.NotMountedException;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FinderTest {

    private final List<String> expected = new ArrayList<>();
    private Path path;
    private Finder finder;

    @Before
    public void createScan() {
        URL url = this.getClass().getClassLoader().getResource("scan/");
        this.path = Paths.get(url.getFile().replace("%20", " "));
        List<String> edl = new ArrayList<>();
        edl.add("A001_C003");
        edl.add("test");
        edl.add("test1.mov");
        edl.add("test3");
        edl.add("A026R6S5");
        edl.add("A026C005");
        this.finder = new Finder(edl);
    }

    public void runTest(boolean checkFiles) {
        Collections.sort(this.expected);
        final List<Path> paths = new ArrayList<>();
        try {
            paths.addAll(finder.getFromSource(this.path, checkFiles));
        } catch (NotMountedException e) {
            e.printStackTrace();
            fail();
        }
        final List<String> stringsFromPaths = paths.stream()
                .map(Path::getFileName)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(this.expected, stringsFromPaths);
    }

    @Test
    public void testScanWithoutFiles() throws Exception {
        String[] expectedArray = new String[]{"A026C005_150518_R6S5", "A026R6S5", "A001_C003.RDC", "test1.mov"};
        this.expected.addAll(Arrays.asList(expectedArray));
        runTest(false);
    }

    @Test
    public void testScanWithFiles() throws Exception {
        String[] expectedWith = new String[]{"A026R6S5", "A026R6S5.mov", "A001_C003.mov", "A001_C003_001.R3D",
                "A001_C003_002.R3D", "A001_C003.RDC", "test.mov", "test1.mov", "test3.ban",
        "A026C005_150518_R6S5", "A026C005_150518_R6S5_101.ari"};
        this.expected.addAll(Arrays.asList(expectedWith));
        runTest(true);
    }

}