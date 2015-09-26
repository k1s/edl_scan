package com.company;

import com.company.exceptions.LTONotMountedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LTOCheck {

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length != 2) {
            System.out.println("Enter path to scan and path to EDL. ltoumom!");
            System.exit(0);
        }

        String PATH = args[0];

        String EDL = args[1];

        Input input = new Input();

        System.out.println();
        System.out.println("Starting with " + EDL);
        System.out.println();

        ArrayList<String> fromEDL = input.getInput(EDL);

        LTOScan ltoScan = new LTOScan();

        ArrayList<String> fromLTO = null;
        try {
            fromLTO = ltoScan.getFromLTO(PATH);
        } catch (LTONotMountedException e) {
            e.printStackTrace();
            System.out.println("LTO not mount! Exit");
            System.exit(-1);
        }

        check(fromLTO, fromEDL, PATH);

    }

    private static void check(final ArrayList<String> fromLTO, final ArrayList<String> fromEDL, final String PATH) {

        for (String s1 : fromLTO) {
            String sub1 = s1.substring(s1.lastIndexOf("/") + 1, s1.lastIndexOf("."));
            if (fromEDL.contains(sub1)) {
                System.out.println(sub1 + " matched");
                System.out.println("Start copying " + s1 + " to " + PATH);
                copy(s1, PATH);
                System.out.println();
            }

        }

    }

    private static void copy(final String PathFrom, final String PathTo) {

        final String command = "cp -av " + PathFrom + " " + PathTo;
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            Scanner scanner = new Scanner(p.getInputStream());
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
