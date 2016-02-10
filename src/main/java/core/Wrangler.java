package core;

import exceptions.NotMountedException;
import view.ConsoleView;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Wrangler {

    public static void scan(final List<String> fromEDL, final String source, final String scanTo,
                            final boolean turbo, final boolean lto, final boolean checkFiles) {
        Finder finder = new Finder(fromEDL);
        List<Path> fromSource = new ArrayList<>();
        try {
            fromSource.addAll(finder.getFromSource(Paths.get(source), checkFiles));
        } catch (NotMountedException e) {
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        ConsoleView.matched(fromSource);
        if (turbo || lto) {
            fromSource.stream().forEach(s -> copy(s, scanTo, turbo));
        } else {
            fromSource.parallelStream().forEach(s -> copy(s, scanTo, false));
        }
    }

    private static void copy(final Path path, final String pathTo, final boolean turbo) {
        ProcessBuilder processBuilder;
        if (turbo) {
            processBuilder = new ProcessBuilder("cxfscp", "-gru", path.toString(), pathTo);
        }   else {
            processBuilder = new ProcessBuilder("cp", "-anv", path.toString(), pathTo);
        }
        processBuilder.redirectErrorStream(true);
        Process p;
        try {
            p = processBuilder.start();
            Scanner scanner = new Scanner(p.getInputStream());
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
