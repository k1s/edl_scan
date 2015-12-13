package com.company.core;

import java.io.*;
import java.net.URL;
import java.util.*;

public class EDL {

    private static final String SHORT_REEL_NAME = "[0-9].*";
    private static final String REEL_NAME_FROM_SOURCE_FILE = ".*SOURCE FILE.*";
    private static final String REEL_NAME_FROM_CLIP_NAME = ".*CLIP NAME.*";

    private static final int SHORT_REEL_NAME_POSITION = 2;
    private static final int REEL_NAME_FROM_SOURCE_FILE_POSITION = 3;

    public ArrayList<String> getInput(final String EDL, final boolean shortReelNames) {

        Set<String> strOut = new HashSet<>();
        try (BufferedReader bufRead = new BufferedReader(new FileReader(EDL))) {
            String strIn;
            while ((strIn = bufRead.readLine()) != null)   {
                checkStrings(strIn, strOut, shortReelNames);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(strOut);
    }

    private void checkStrings(final String strIn, final Set<String> strOut, final boolean shortReelNames) {
        if (shortReelNames) {
            checkString(strIn, SHORT_REEL_NAME, SHORT_REEL_NAME_POSITION, strOut);
        }
        checkString(strIn, REEL_NAME_FROM_SOURCE_FILE, REEL_NAME_FROM_SOURCE_FILE_POSITION, strOut);
        checkString(strIn, REEL_NAME_FROM_CLIP_NAME, REEL_NAME_FROM_CLIP_NAME_POSITION(strIn), strOut);
    }

    private int REEL_NAME_FROM_CLIP_NAME_POSITION(final String string) {
        return string.split("\\s").length-1;
    }

    private void checkString(final String forCheck, final String matcher, final int position, final Set<String> set) {
        if (forCheck.length() > 1 && forCheck.matches(matcher)) {
            String[] strDev = forCheck.split("\\s");
            int validPositionFromLine = strDev.length-1 < position ? strDev.length-1 : position;
            if (strDev[validPositionFromLine] != null)
                set.add(strDev[validPositionFromLine]);
        }
    }
}
