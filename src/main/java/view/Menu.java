package main.java.view;

import main.java.core.Checker;
import main.java.core.EDL;
import main.java.core.Scanner;
import main.java.dry.Assert;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Menu {

    private List<String> args;
    private boolean useReelNames;
    private boolean findFiles;
    private String EDL;

    public Menu(List<String> args) {
        this.args = args;
        this.EDL = args.get(1);
        Assert.requirePath(Paths.get(this.EDL));
        this.useReelNames = args.stream()
                                .anyMatch(s -> s.equals("usereels"));
        this.findFiles = args.stream()
                             .anyMatch(s -> s.equals("findfiles"));
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        String begin = Calendar.getInstance().getTime().toString();

        if (args.length == 0) {
            System.out.println();
            System.out.println("java -jar edl_scan.jar scanlto EDL destination source – scan from EDL with cp");
            System.out.println("java -jar edl_scan.jar scan EDL destination sources  – scan from EDL with parallel cp");
            System.out.println("java -jar edl_scan.jar superscan EDL destination  sources – scan from EDL with cxfscp");
            System.out.println();
            System.out.println("java -jar edl_scan.jar checklogs EDL source – find from EDL in logs");
            System.out.println("java -jar edl_scan.jar checkscan EDL source – find from EDL in source," +
                    " also can be used for verifying scan");
            System.out.println("java -jar edl_scan.jar checkscanlogs EDL source logs – verifying scan " +
                    "folder from EDL and shows not founded from logs");
            System.out.println();
            System.out.println("You can add options to the end of query:");
            System.out.printf("usereels – also search for reel names");
            System.out.printf("findfiles – search for distinct files names; be careful and dont use it " +
                    "with large sequences like from ARRI");
            System.exit(-1);
        }

        List<String> varargs = Arrays.asList(args);

        Menu menu = new Menu(varargs);
        menu.run();

        String end = Calendar.getInstance().getTime().toString();

        System.out.println("BEGIN " + begin);
        System.out.println("END " + end);

    }

    private void run() {

        ConsoleView.startOutput(this.EDL);

        switch (args.get(0)) {
            case "scan": {
                System.out.println("SCAN");
                runCase(false, false);
                break;
            }
            case "scanlto": {
                System.out.println("SCANLTO");
                runCase(false, true);
                break;
            }
            case "superscan": {
                System.out.println("SUPERSCAN");
                runCase(true, false);
                break;
            }
            case "checklogs": {
                System.out.println("CHECKLOGS");
                checkLogsRun();
                break;
            }
            case "checkscan": {
                System.out.println("CHECKSCAN");
                checkScanRun();
                break;
            }
            case "checkscanlogs": {
                System.out.println("CHECKSCANLOGS");
                checkScanLogsRun();
                break;
            }
        }
    }

    private void checkLogsRun() {
        String source = this.args.get(2);
        EDL input = new EDL(Paths.get(this.EDL));
        Checker.checkLogs(input.getInput(this.useReelNames), source, new ArrayList<>());
    }

    private void checkScanRun() {
        String source = this.args.get(2);
        EDL input = new EDL(Paths.get(this.EDL));
        Checker.checkScan(input.getInput(this.useReelNames), source, this.findFiles);
    }

    private void checkScanLogsRun() {
        String source = this.args.get(2);
        EDL input = new EDL(Paths.get(this.EDL));
        String logs = args.get(3);
        Checker.checkScanLogs(input.getInput(this.useReelNames), source, logs, this.findFiles);
    }


    private void runCase(boolean turbo, boolean lto) {
        String scanTo = args.get(2);
        List<String> sources = getSources(args);
        runCheck(scanTo, sources, turbo, lto);
    }

    private List<String> getSources(List<String> args) {
        return new ArrayList<>(args.subList(3, args.size()));
    }

    private void runCheck(String destination, List<String> sources, boolean turbo, boolean lto) {
        System.out.println("SOURCES " + sources);
        sources.stream()
                .forEach(source -> {
                    System.out.println();
                    System.out.println("SCAN " + source);
                    Scanner.scan(this.EDL, source, destination, turbo, lto, this.useReelNames, this.findFiles);
                });
    }



}
