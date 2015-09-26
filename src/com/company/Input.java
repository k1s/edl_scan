package com.company;

import java.io.*;
import java.util.ArrayList;

public class Input {

    public ArrayList<String> getInput(final String EDL) {

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
                if (strIn.length() > 1 && strIn.matches("[0-9].*")) {
                    strDev = strIn.split("\\s");
                    strOut.add(strDev[2]);
                }

                if (strIn.length() > 1 && strIn.matches(".*CLIP NAME.*")) {
                    strDev = strIn.split("\\s");
                    strOut.add(strDev[5]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strOut;

    }


}
