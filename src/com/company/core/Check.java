package com.company.core;

import com.company.exceptions.NotMountedException;
import com.company.view.ConsoleView;


import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class Check {

    public static void scan(final String EDL, final String source, final String scanTo,
                            final boolean turbo, final boolean lto, final boolean shortReels, boolean checkFiles) {
        EDL input = new EDL(Paths.get(EDL));
        List<String> fromEDL = input.getInput(shortReels);
        ConsoleView.fromEDLOutput(fromEDL);
        Scan scan = new Scan(fromEDL);
        List<Path> fromSource = null;
        try {
            fromSource = scan.getFromSource(Paths.get(source), checkFiles);
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        ConsoleView.matched(fromSource);
        if (turbo || lto) {
            fromSource.stream().forEach(s -> copy(s,scanTo, turbo));
        } else {
            fromSource.parallelStream().forEach(s -> copy(s,scanTo, false));
        }

    }

    private static void copy(final Path path, final String pathTo, final boolean turbo) {
//        final String pathFrom = path.replaceAll(" ","\\ ");
        ProcessBuilder chmod = new ProcessBuilder("chmod", "777", "-R", pathTo);
        try {
            Process toChmod = chmod.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void checkScanLogs(final List<String> fromEDL, final String source, final String logs, boolean checkFiles) {
        List<String> notFounded = checkScanNotFound(fromEDL, source, checkFiles);
        List<String> notFoundedAtAll = new CopyOnWriteArrayList<>(notFounded);
        checkLogs(notFounded, logs, notFoundedAtAll);
        System.out.println("NOT FOUND AT ALL " + notFoundedAtAll);
    }

    public static void checkScan(final List<String> fromEDL, final String scanDir, boolean checkFiles) {
        System.out.println("NOT FOUND: " + checkScanNotFound(fromEDL, scanDir, checkFiles));
    }

    private static List<String> checkScanNotFound(final List<String> fromEDL, final String scanDir, boolean checkFiles) {
        ConsoleView.fromEDLOutput(fromEDL);
        Scan scan = new Scan(fromEDL);
        List<Path> fromSource = null;
        try {
            fromSource = scan.getFromSource(Paths.get(scanDir), checkFiles);
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        List<Path> fromSourceTOScan = fromSource;
        return fromEDL
                .parallelStream()
                .filter(s -> !fromSourceTOScan.contains(s)).collect(Collectors.toList());
    }

    public static void checkLogs(final List<String> fromEDL, final String source, final List<String> notFoundedAtAll) {
        ConsoleView.fromEDLOutput(fromEDL);
        fromEDL.parallelStream().forEach(s -> {
            seeLogs(source, s, notFoundedAtAll);
        });
    }

    private static void seeLogs(final String path, final String strEDL, List<String> notFoundedAtAll) {
        File root = new File(path);
        File[] list = root.listFiles();
        List<File> files = new ArrayList<>();
        if (list != null) {
            files = Arrays.asList(list);
        }
        files.parallelStream().filter(f -> !f.getAbsolutePath().contains(".DS_Store")).forEach(f -> {
            if (f.isDirectory() && f.getAbsolutePath() != null) {
                seeLogs(f.getAbsolutePath(), strEDL, notFoundedAtAll);
            }
            if (f.isFile()) {
                seeFileForStrings(strEDL, f.getAbsolutePath(), notFoundedAtAll);
            }
        });
    }

    private static void seeFileForStrings(final String search, final String path, final List<String> notFoundedAtAll) {
        try (BufferedReader bufRead = new BufferedReader(new FileReader(path))) {
            String strIn;
            while ((strIn = bufRead.readLine()) != null) if (!strIn.contains(".ari")
                    && !strIn.contains(".WAV")
                    && !strIn.contains(".XML")
                    && !strIn.contains(".ARI")
                    && !strIn.contains(".wav")
                    && !strIn.contains(".xml")
                    && strIn.contains(search)) {
                System.out.println(search + " is founded in " + path + ": " + strIn);
                notFoundedAtAll.remove(search);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
