package com.company.core;

import com.company.exceptions.NotMountedException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Scan {

    public ArrayList<String> getFromSource(final ArrayList<String> fromEDL, final String source) throws NotMountedException {
        File root = new File(source);
        if (root.list().length == 0)
            throw new NotMountedException();
        ArrayList<String> filesWalk = new ArrayList<String>();
        return customWalk(fromEDL, source, filesWalk);
    }

    public ArrayList<String> customWalk(final ArrayList<String> fromEDL, final String source, final ArrayList<String> filesWalk) {

        File root = new File(source);
        File[] list = root.listFiles();
        List<File> files = new ArrayList<>();
        if (list != null) {
            files = Arrays.asList(list);
        }
        files.parallelStream().filter(f -> !f.getAbsolutePath().contains(".DS_Store")).forEach(f -> {
            String fileName = String.valueOf(f.getAbsoluteFile());
            if ( f.isDirectory() ) {
                checkDirName(fileName, filesWalk, fromEDL);
                customWalk(fromEDL, f.getAbsolutePath(), filesWalk);
            }
            if (f.isFile()) {
                checkFileName(fileName, filesWalk, fromEDL);
            }
        });

        return filesWalk;

    }

    public ArrayList<String> checkScan(final ArrayList<String> fromEDL, final String source) throws NotMountedException {

        File root = new File(source);
        File[] list = root.listFiles();
        if (list != null)
            if (list.length == 0)
                throw new NotMountedException();
        ArrayList<String> strings = new ArrayList<String>();

        return checkScanWalk(fromEDL, source, strings);

    }

    public ArrayList<String> checkScanWalk(final ArrayList<String> fromEDL, final String source, final ArrayList<String> strings) {


        File root = new File(source);
        File[] list = root.listFiles();
        List<File> files = new ArrayList<>();
        if (list != null) {
            files = Arrays.asList(list);
        }

        files.parallelStream().filter(f -> !f.getAbsolutePath().contains(".DS_Store")).forEach(f -> {
            //TODO one function for all of them
            String fileName = String.valueOf(f.getAbsoluteFile());
            if ( f.isDirectory() ) {
                checkName(fileName, strings, fromEDL);
                checkScanWalk(fromEDL, f.getAbsolutePath(), strings);
            }
            if (f.isFile()) {
                checkName(fileName, strings, fromEDL);
            }
        });

        return strings;
    }

    private void checkName(final String fileName, final ArrayList<String> strings, final ArrayList<String> fromEDL) {
        fromEDL.parallelStream().forEach(s -> {
            if ((fileName.toLowerCase().contains(s.toLowerCase()) || fileName.contains(s)) && !strings.contains(s))
                strings.add(s);
        });
    }

    private void checkDirName(final String fileName, final ArrayList<String> filesWalk, final ArrayList<String> fromEDL) {
        String toCheck = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        fromEDL.parallelStream().forEach(s -> {
            if (toCheck.contains(s) || toCheck.toLowerCase().contains(s.toLowerCase()))
                filesWalk.add(fileName);
        });
    }

    private void checkFileName(final String fileName, final ArrayList<String> filesWalk, final ArrayList<String> fromEDL) {
        String toCheck = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
        //TODO ADD OPTION TO CHECK FILES LIKE .MOV
        List<String> fromEDLLow = fromEDL.parallelStream().map(String::toLowerCase).collect(Collectors.toList());
        if (fromEDL.contains(toCheck) || fromEDLLow.contains(toCheck.toLowerCase()))
            filesWalk.add(fileName);

    }

}
