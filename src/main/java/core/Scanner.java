package core;

import exceptions.NotMountedException;
import view.ConsoleView;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Scanner {

    public static void scan(final String EDL, final String source, final String scanTo,
                            final boolean turbo, final boolean lto, final boolean shortReels, boolean checkFiles) {
        EDL input = new EDL(Paths.get(EDL));
        List<String> fromEDL = input.getInput(shortReels);
        ConsoleView.fromEDLOutput(fromEDL);
        Finder finder = new Finder(fromEDL);
        List<Path> fromSource = null;
        try {
            fromSource = finder.getFromSource(Paths.get(source), checkFiles);
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        ConsoleView.matched(fromSource);
//        if (turbo || lto) {
//            fromSource.stream().forEach(s -> copy(s,scanTo, turbo));      TODO
//        } else {
//            fromSource.parallelStream().forEach(s -> copy(s,scanTo, false));
//        }

    }

//    private static void copy(final Path path, final String pathTo, final boolean turbo) {
////        final String pathFrom = path.replaceAll(" ","\\ ");
//        ProcessBuilder chmod = new ProcessBuilder("chmod", "777", "-R", pathTo);
//        try {
//            Process toChmod = chmod.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ProcessBuilder processBuilder;
//        if (turbo) {
//            processBuilder = new ProcessBuilder("cxfscp", "-gru", path.toString(), pathTo);
//        }   else {
//            processBuilder = new ProcessBuilder("cp", "-anv", path.toString(), pathTo);
//        }
//        processBuilder.redirectErrorStream(true);
//        Process p;
//        try {
//            p = processBuilder.start();
//            Scanner scanner = new Scanner(p.getInputStream());    TODO
//            while (scanner.hasNext()) {
//                System.out.println(scanner.nextLine());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
