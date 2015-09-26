package com.company.view;

public class ConsoleView {

    public static void startOutput(final String EDL) {
        System.out.println();
        System.out.println("Starting with " + EDL);
        System.out.println();
    }

    public static void errorOutput() {
        System.out.println("LTO not mount at /LTO! You need use \"ltfs -o eject /LTO\" after cartrige injecting. Exit");
    }

    public static void errorArgsOutput() {
        System.out.println("Enter path to scan and path to EDL");
    }

}
