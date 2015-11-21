package com.company.view;

import java.util.ArrayList;

public class ConsoleView {

    public static void startOutput(final String EDL) {
        System.out.println();
        System.out.println("Starting with " + EDL);
        System.out.println();
    }

    public static void fromEDLOutput(final ArrayList<String> fromEDL) {
        System.out.println();
        System.out.println("From EDL: " + fromEDL);
        System.out.println();
    }

    public static void matched(final ArrayList<String> fromSource) {
        System.out.println();
        System.out.println("Matched: " + fromSource);
        System.out.println();
    }

    public static void errorOutput() {
        System.out.println("LTO not mount at /LTO! You need use \"ltfs -o eject /LTO\" after cartrige injecting. Exit");
    }

}
