package core;

import helpers.Assert;
import view.ConsoleView;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Runner {

    private List<String> args;
    private Checker checker;
    private boolean useReelNames;
    private boolean findFiles;
    private String EDL;
    private List<String> EDLstrings;

    public Runner(final List<String> args) {
        this.checker = new Checker();
        this.args = args;
        this.useReelNames = args.stream()
                .anyMatch(s -> s.equals("usereels"));
        this.findFiles = args.stream()
                .anyMatch(s -> s.equals("findfiles"));
        this.args = args;
        this.EDL = args.get(1);
        Assert.requirePath(Paths.get(this.EDL));
        EDL input = new EDL(Paths.get(this.EDL));
        this.EDLstrings = input.getInput(this.useReelNames);
        Assert.require(!this.EDLstrings.isEmpty(), "Cannot extract something from EDL");
    }

    public void run() {

        ConsoleView.startOutput(this.EDL);
        System.out.println();
        ConsoleView.fromEDLOutput(this.EDLstrings);

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
        List<String> strs = checker.checkLogs(this.EDLstrings, source);
        System.out.println("FOUND IN LOGS   " + strs);
    }

    private void checkScanRun() {
        String source = this.args.get(2);
        List<String> strs = checker.checkScan(this.EDLstrings, source);
        System.out.println("NOT FOUND IN SCAN   " + strs);
    }

    private void checkScanLogsRun() {
        String source = this.args.get(2);
        String logs = args.get(3);
        String str = checker.checkScanLogs(this.EDLstrings, source, logs);
        System.out.println(str);
    }


    private void runCase(final boolean turbo, final boolean lto) {
        String scanTo = args.get(2);
        List<String> sources = getSources(args);
        runCheck(scanTo, sources, turbo, lto);
    }

    private List<String> getSources(final List<String> args) {
        return new ArrayList<>(args.subList(3, args.size()));
    }

    private void runCheck(final String destination, final List<String> sources, final boolean turbo, final boolean lto) {
        ConsoleView.fromEDLOutput(this.EDLstrings);
        System.out.println("SOURCES " + sources);
        sources.stream()
                .forEach(source -> {
                    System.out.println();
                    System.out.println("SCAN " + source);
                    Wrangler.scan(this.EDLstrings, source, destination, turbo, lto, this.findFiles);
                });
    }
}
