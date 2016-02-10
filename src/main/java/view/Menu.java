package view;

import core.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Menu {


    public static void main(String[] args) throws IOException, InterruptedException {

        String begin = Calendar.getInstance().getTime().toString();

        if (args.length == 0) {
            System.out.println();
            System.out.println("java -jar edl_scan.jar scanlto EDL destination source – scan from EDL with cp");
            System.out.println("java -jar edl_scan.jar scan EDL destination sources  – scan from EDL with parallel cp");
            System.out.println("java -jar edl_scan.jar superscan EDL destination  sources – scan from EDL with cxfscp");
            System.out.println();
            System.out.println("java -jar edl_scan.jar checklogs EDL source – find from EDL in logs");
            System.out.println("checks only UTF files, others would be skipped");
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

        Runner runner = new Runner(varargs);
        runner.run();

        String end = Calendar.getInstance().getTime().toString();

        System.out.println("BEGIN " + begin);
        System.out.println("END " + end);
    }

}
