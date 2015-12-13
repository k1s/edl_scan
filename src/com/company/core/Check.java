package com.company.core;

import com.company.exceptions.NotMountedException;
import com.company.view.ConsoleView;


import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class Check {

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length == 0) {
            System.out.println("java -jar edl_scan.jar scan EDL source destination – scan from EDL with parallel cp");
            System.out.println("java -jar edl_scan.jar scanlto EDL source destination – scan from EDL with cp");
            System.out.println("java -jar edl_scan.jar superscan EDL source destination – scan from EDL with cxfscp");
            System.out.println("java -jar edl_scan.jar checklogs EDL source – find from EDL in logs");
            System.out.println("java -jar edl_scan.jar checkscan EDL source – find from EDL in source, also can be used for verifying scan");
            System.out.println("java -jar edl_scan.jar checkscanlogs EDL source logs – Verifying scan folder from EDL and shows not founded from logs");
            System.exit(-1);
        }

        String begin = Calendar.getInstance().getTime().toString();

        if (args[0].equals("scan")) {
            System.out.println("SCAN");
          String EDL = args[1];
          String source = args[2];
          String scanTo = args[3];
          scan(EDL, source, scanTo, false, false);
        }

        if (args[0].equals("scanlto")) {
            System.out.println("SCAN");
            String EDL = args[1];
            String source = args[2];
            String scanTo = args[3];
            scan(EDL, source, scanTo, false, true);
            umount();

        }

        if (args[0].equals("superscan")) {
            System.out.println("SUPERSCAN");
            String EDL = args[1];
            String source = args[2];
            String scanTo = args[3];
            scan(EDL, source, scanTo, true, false);
        }

        if (args[0].equals("checklogs")) {
            System.out.println("CHECKLOGS");
          String EDL = args[1];
          String source = args[2];
            EDL input = new EDL();
            ConsoleView.startOutput(EDL);
//          checkLogs(input.getInput(EDL, false), source);
            List<String> notFoundedAtAll = new ArrayList<>();
            checkLogs(input.getInput(EDL, true), source, notFoundedAtAll);
        }

        if (args[0].equals("checkscan")) {
            System.out.println("CHECKSCAN");
            String EDL = args[1];
            String source = args[2];
            EDL input = new EDL();
            checkScan(input.getInput(EDL, false), source);
//            List<String> notFoundedAtAll = new ArrayList<>();
//            checkLogs(input.getInput(EDL, true), source, notFoundedAtAll);
        }

        if (args[0].equals("checkscanlogs")) {
            System.out.println("CHECKSCANLOGS");
            String EDL = args[1];
            String source = args[2];
            String logs = args[3];
            EDL input = new EDL();
//            checkScanLogs(input.getInput(EDL, false), source, logs);
            checkScanLogs(input.getInput(EDL, true), source, logs);
        }

        String end = Calendar.getInstance().getTime().toString();
        System.out.println("BEGIN " + begin);
        System.out.println("END " + end);

    }

    private static void checkScanLogs(final ArrayList<String> fromEDL, final String source, final String logs) {
        List<String> notFounded = checkScanNotFound(fromEDL, source);
        CopyOnWriteArrayList<String> notFoundedAtAll = new CopyOnWriteArrayList<>(notFounded);
        checkLogs(notFounded, logs, notFoundedAtAll);
        System.out.println("NOT FOUND AT ALL " + notFoundedAtAll);
    }

    private static void checkScan(final ArrayList<String> fromEDL, final String scanDir) {
        System.out.println("NOT FOUND: " + checkScanNotFound(fromEDL, scanDir));
    }

    private static List<String> checkScanNotFound(final ArrayList<String> fromEDL, final String scanDir) {
        ConsoleView.fromEDLOutput(fromEDL);
        Scan scan = new Scan();
        ArrayList<String> fromSource = null;
        try {
            fromSource = scan.checkScan(fromEDL, scanDir);
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        ArrayList<String> fromSourceTOScan = fromSource;
        return fromEDL
                .parallelStream()
                .filter(s -> !fromSourceTOScan.contains(s)).collect(Collectors.toList());
    }

    private static void checkLogs(final List<String> fromEDL, final String source, final List<String> notFoundedAtAll) {
        ConsoleView.fromEDLOutput(fromEDL);
        fromEDL.parallelStream().forEach(s -> {
            seeLogs(source, s, notFoundedAtAll);
        });
    }

    private static void scan(final String EDL, final String source, final String scanTo, final boolean turbo, final boolean lto) {
        com.company.core.EDL input = new EDL();
        ConsoleView.startOutput(EDL);
//        ArrayList<String> fromEDL = input.getInput(EDL, false);
        ArrayList<String> fromEDL = input.getInput(EDL, true);
        ConsoleView.fromEDLOutput(fromEDL);
        Scan scan = new Scan();
        ArrayList<String> fromSource = null;
        try {
            fromSource = scan.getFromSource(fromEDL, source);
        } catch (NotMountedException e) {
            e.printStackTrace();
            ConsoleView.errorOutput();
            System.exit(-1);
        }
        ConsoleView.matched(fromSource);
        if (turbo || lto) {
            fromSource.stream().forEach(s -> copy(s,scanTo, turbo));
        } else {
            fromSource.parallelStream().forEach(s -> copy(s,scanTo, turbo));
        }

    }

    private static void umount() {
        ProcessBuilder umount = new ProcessBuilder("umount", "/LTO");
        try {
            Process umounter = umount.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copy(final String path, final String pathTo, final boolean turbo) {
        final String pathFrom = path.replaceAll(" ","\\ ");
        ProcessBuilder chmod = new ProcessBuilder("chmod", "777", "-R", pathTo);
        try {
            Process toChmod = chmod.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessBuilder processBuilder;
        if (turbo) {
            processBuilder = new ProcessBuilder("cxfscp", "-gru", pathFrom, pathTo);
        }   else {
            processBuilder = new ProcessBuilder("cp", "-anv", pathFrom, pathTo);
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
