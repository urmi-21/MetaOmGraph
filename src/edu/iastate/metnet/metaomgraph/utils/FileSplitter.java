package edu.iastate.metnet.metaomgraph.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSplitter {
    public FileSplitter() {
    }

    public static File[] splitFile(File source, File dest1, File dest2, File metadataFile, Collection<Integer> cols1, Collection<Integer> cols2, String delimiter)
            throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(source));
        TreeMap<Integer, Integer> colMap = new TreeMap();

        BufferedWriter out1 = new BufferedWriter(new FileWriter(dest1));
        BufferedWriter out2 = new BufferedWriter(new FileWriter(dest2));

        int col1Index = 0;
        int col2Index = 0;
        boolean firstLine = true;
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter);
            out1.write(splitLine[0]);
            out2.write(splitLine[0]);
            for (int i = 1; i < splitLine.length; i++) {
                if ("null".equals(splitLine[i])) {
                    splitLine[i] = "";
                }
                if (cols1.contains(Integer.valueOf(i))) {
                    out1.write(delimiter + splitLine[i]);
                    if (firstLine) {
                        colMap.put(Integer.valueOf(i - 1), Integer.valueOf(col1Index++));
                    }
                } else if (cols2.contains(Integer.valueOf(i))) {
                    out2.write(delimiter + splitLine[i]);
                    if (firstLine) {
                        colMap.put(Integer.valueOf(i - 1), Integer.valueOf(col2Index++));
                    }
                }
            }
            firstLine = false;
            out1.newLine();
            out2.newLine();
        }
        in.close();
        out1.close();
        out2.close();
        if (metadataFile == null) {
            return new File[]{dest1, dest2};
        }
        in = new BufferedReader(new FileReader(metadataFile));
        File mdDest1 = new File(Utils.removeExtension(dest1.getAbsolutePath()) +
                " - metadata.xml");
        File mdDest2 = new File(Utils.removeExtension(dest2.getAbsolutePath()) +
                " - metadata.xml");
        StringBuffer finalMD1 = new StringBuffer();
        StringBuffer finalMD2 = new StringBuffer();
        Pattern regex = Pattern.compile(" col=\"(\\d*)\"", 128);
        while ((thisLine = in.readLine()) != null) {
            Matcher regexMatcher1 = regex.matcher(thisLine);
            Matcher regexMatcher2 = regex.matcher(thisLine);
            if ((regexMatcher1.find()) && (regexMatcher2.find())) {
                do {
                    Integer thisCol;
                    try {
                        thisCol = Integer.valueOf(regexMatcher1.group(1));
                    } catch (NumberFormatException nfe) {
                        thisCol = Integer.valueOf(-1);
                    }
                    System.out.println("Column value detected: " + thisCol);
                    if (thisCol.intValue() >= 0) {
                        Integer newCol = colMap.get(thisCol);
                        if (cols1.contains(Integer.valueOf(thisCol.intValue() + 1))) {
                            regexMatcher1.appendReplacement(finalMD1, " col=\"" +
                                    newCol + "\"");
                            regexMatcher2.appendReplacement(finalMD2, "");
                        } else if (cols2.contains(Integer.valueOf(thisCol.intValue() + 1))) {
                            regexMatcher2.appendReplacement(finalMD2, " col=\"" +
                                    newCol + "\"");
                            regexMatcher1.appendReplacement(finalMD1, "");
                        } else {
                            System.err.println("Unknown column: " + thisCol);
                        }
                    }
                    regexMatcher1.appendTail(finalMD1);
                    regexMatcher2.appendTail(finalMD2);
                    finalMD1.append("\n");
                    finalMD2.append("\n");
                    if (!regexMatcher1.find()) {
                        break;
                    }
                } while (


                        regexMatcher2.find());
            } else {
                finalMD1.append(thisLine + "\n");
                finalMD2.append(thisLine + "\n");
            }
        }

        in.close();
        out1 = new BufferedWriter(new FileWriter(mdDest1));
        out2 = new BufferedWriter(new FileWriter(mdDest2));
        out1.write(finalMD1.toString());
        out2.write(finalMD2.toString());
        out1.close();
        out2.close();
        return new File[]{dest1, dest2, mdDest1, mdDest2};
    }


    public static File[] splitAndNormalize(File source, File nonloggedFile, File loggedFile, File metadataFile, Collection<Integer> nonloggedCols, Collection<Integer> loggedCols, String delimiter, double[] factors)
            throws IOException {
        System.out.println("Splitting and normalizing");
        System.out.println("Factors: " + factors.length);
        System.out.println("Nonlogged: " + nonloggedCols.size());
        System.out.println("Logged: " + loggedCols.size());
        System.out.println("Total: " + (
                loggedCols.size() + nonloggedCols.size()));
        BufferedReader in = new BufferedReader(new FileReader(source));

        BufferedWriter nonloggedOut = new BufferedWriter(new FileWriter(nonloggedFile));
        BufferedWriter loggedOut = new BufferedWriter(new FileWriter(loggedFile));

        TreeMap<Integer, Integer> colMap = new TreeMap();
        int nonloggedIndex = 0;
        int loggedIndex = 0;
        DecimalFormat formatter = new DecimalFormat("#.####");
        boolean firstLine = true;
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            String[] splitLine = thisLine.split(delimiter);
            nonloggedOut.write(splitLine[0]);
            loggedOut.write(splitLine[0]);
            for (int i = 1; i < splitLine.length; i++) {
                if ("null".equals(splitLine[i])) {
                    splitLine[i] = "";
                }
                if (nonloggedCols.contains(Integer.valueOf(i))) {
                    if (factors != null) {
                        try {
                            double val = Double.parseDouble(splitLine[i]);

                            val *= factors[(i - 1)];
                            nonloggedOut.write(delimiter +
                                    formatter.format(val));
                        } catch (NumberFormatException nfe) {
                            nonloggedOut.write(delimiter + splitLine[i]);
                        } finally {
                            if (firstLine) {
                                colMap.put(Integer.valueOf(i - 1), Integer.valueOf(nonloggedIndex++));
                            }
                        }
                    } else {
                        nonloggedOut.write(delimiter + splitLine[i]);
                        if (firstLine) {
                            colMap.put(Integer.valueOf(i - 1), Integer.valueOf(nonloggedIndex++));
                        }
                    }
                } else if (loggedCols.contains(Integer.valueOf(i))) {
                    loggedOut.write(delimiter + splitLine[i]);
                    if (firstLine) {
                        colMap.put(Integer.valueOf(i - 1), Integer.valueOf(loggedIndex++));
                    }
                }
            }
            firstLine = false;
            nonloggedOut.newLine();
            loggedOut.newLine();
        }
        in.close();
        nonloggedOut.close();
        loggedOut.close();
        if (metadataFile == null) {
            return new File[]{nonloggedFile, loggedFile};
        }
        in = new BufferedReader(new FileReader(metadataFile));
        File nonloggedMetadataFile = new File(
                Utils.removeExtension(nonloggedFile.getAbsolutePath()) +
                        " - metadata.xml");
        File loggedMetadataFile = new File(Utils.removeExtension(loggedFile
                .getAbsolutePath()) + " - metadata.xml");
        StringBuffer nonloggedMetadata = new StringBuffer();
        StringBuffer loggedMetadata = new StringBuffer();
        Pattern regex = Pattern.compile(" col=\"(\\d*)\"", 128);
        while ((thisLine = in.readLine()) != null) {
            Matcher nonloggedMatcher = regex.matcher(thisLine);
            Matcher loggedMatcher = regex.matcher(thisLine);
            if ((nonloggedMatcher.find()) && (loggedMatcher.find())) {
                do {
                    Integer thisCol;
                    try {
                        thisCol = Integer.valueOf(loggedMatcher.group(1));
                    } catch (NumberFormatException nfe) {
                        thisCol = Integer.valueOf(-1);
                    }
                    System.out.println("Column value detected: " + thisCol);
                    if (thisCol.intValue() >= 0) {
                        Integer newCol = colMap.get(thisCol);
                        if (nonloggedCols.contains(Integer.valueOf(thisCol.intValue() + 1))) {
                            nonloggedMatcher.appendReplacement(
                                    nonloggedMetadata, " col=\"" + newCol +
                                            "\"");
                            loggedMatcher.appendReplacement(loggedMetadata, "");
                        } else if (loggedCols.contains(Integer.valueOf(thisCol.intValue() + 1))) {
                            loggedMatcher.appendReplacement(loggedMetadata,
                                    " col=\"" + newCol + "\"");
                            nonloggedMatcher.appendReplacement(
                                    nonloggedMetadata, "");
                        } else {
                            System.err.println("Unknown column: " + thisCol);
                        }
                    }
                    nonloggedMatcher.appendTail(nonloggedMetadata);
                    nonloggedMetadata.append("\n");
                    loggedMatcher.appendTail(loggedMetadata);
                    loggedMetadata.append("\n");

                    if (!nonloggedMatcher.find()) {
                        break;
                    }
                } while (


                        loggedMatcher.find());
            } else {
                loggedMetadata.append(thisLine + "\n");
                nonloggedMetadata.append(thisLine + "\n");
            }
        }
        in.close();
        nonloggedOut = new BufferedWriter(new FileWriter(nonloggedMetadataFile));
        loggedOut = new BufferedWriter(new FileWriter(loggedMetadataFile));
        nonloggedOut.write(nonloggedMetadata.toString());
        loggedOut.write(loggedMetadata.toString());
        nonloggedOut.close();
        loggedOut.close();
        return new File[]{nonloggedFile, loggedFile, nonloggedMetadataFile,
                loggedMetadataFile};
    }
}
