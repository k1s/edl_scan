package com.company;

import java.io.*;
import java.util.ArrayList;

public class Input {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Enter path to scan and path to EDL. ltoumom!");
            System.exit(0);
        }

        String PATH = args[0];

        String EDL = args[1];

        System.out.println("Starting with " + EDL);
        System.out.println();

        FileReader input = null;
        try {
            input = new FileReader(EDL);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufRead = new BufferedReader(input);

        String strIn;
        String[] strDev;
        ArrayList<String> strOut = new ArrayList<String>();

        try {
            while ((strIn = bufRead.readLine()) != null)   {
//                if (strIn != null || strIn.equals("") || strIn.length() > 4) {
//                    strIn = strIn.substring(5, 16);
                if (strIn.length() > 1 && strIn.matches("[0-9].*")) {
                    strDev = strIn.split("\\s");//.substring(sp, strIn.length() - 1).split(" ")[0];
                    strOut.add(strDev[2]);
                }

                if (strIn.length() > 1 && strIn.matches(".*CLIP NAME.*")) {
                    strDev = strIn.split("\\s");//.substring(sp, strIn.length() - 1).split(" ")[0];
                    strOut.add(strDev[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(strOut);

//        //String command = "touch " + PATH + "newfile.test";
//
//        try {
//            Runtime.getRuntime().exec(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }


}
