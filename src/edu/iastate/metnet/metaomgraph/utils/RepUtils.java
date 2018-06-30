package edu.iastate.metnet.metaomgraph.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RepUtils {
    public RepUtils() {
    }

    public static ArrayList<ArrayList<String>> groupSamples(java.util.Collection<String> sampleNames) {
        ArrayList<String> names = new ArrayList(sampleNames);
        ArrayList<ArrayList<String>> result = new ArrayList();
        while (names.size() > 0) {
            String thisName = names.remove(0);
            ArrayList<String> thisGroup = new ArrayList();
            thisGroup.add(thisName);
            for (int i = 0; i < names.size(); i++) {
                if (areReps(thisName, names.get(i))) {
                    thisGroup.add(names.get(i));
                    names.remove(i);
                    i--;
                }
            }
            result.add(thisGroup);
        }
        return result;
    }


    public static boolean areReps(String s1, String s2) {
        /*
		 * Infected_hypocotyls_12h_rep1 Infected_hypocotyls_48h_rep1
		 * Infected_hypocotyls_24h_rep3 Infected_hypocotyls_48h_rep2
		 * Infected_hypocotyls_24h_rep2 Infected_hypocotyls_48h_rep4
		 * Infected_hypocotyls_48h_rep3 Infected_hypocotyls_12h_rep2
		 * Infected_hypocotyls_24h_rep1 Infected_hypocotyls_48h_rep5
		 * Infected_hypocotyls_12h_rep3
		 */
		/*
		 * Na����ve Huh-7 Na퀌�ve Huh-7
		 * 
		 * 0.2�?�?M MNNG 0.2�?�?M MNNG
		 */
        // TODO: If difference is ", biological rep", match
        String n1 = s1.replaceAll("[\\s\\-\\=_:]|����|퀌�|�\\?�\\?|�\\?�\\?",
                "").toLowerCase();
        n1 = Utils.removeExtendedChars(n1);
        String n2 = s2.replaceAll("[\\s\\-\\=_:]|����|퀌�|�\\?�\\?|�\\?�\\?",
                "").toLowerCase();
        n2 = Utils.removeExtendedChars(n2);
        n1 = n1.replaceAll("ath1", "");
        n2 = n2.replaceAll("ath1", "");
        String diff = getStringDiff(n1, n2);
        Matcher m1 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n1);
        Matcher m2 = Pattern.compile("rep(?:licate)?\\d+?").matcher(n2);
        boolean match = false;
        if (m1.find() && m2.find()) {
            int end1 = n1.lastIndexOf("rep");
            int end2 = n2.lastIndexOf("rep");
            String name1 = n1.substring(0, end1);
            String name2 = n2.substring(0, end2);
            String num1 = n1.substring(end1);
            String num2 = n2.substring(end2);
            String numDiff = getStringDiff(num1, num2);
            String nameDiff = getStringDiff(name1, name2);
            if (numDiff.length() > 0) {
                // TODO Problem: if chip names differ after the rep number, they
                // still count as reps. Rare, but possible.
                // Other problems: (ATGE_100_A, ATGE_100_B, ATGE_100_C) and
                // (AtGen_6-9621_Heatstress(3h)+21hrecovery-Roots-24.0h_Rep1,
                // AtGen_6-9622_Heatstress(3h)+21hrecovery-Roots-24.0h_Rep2)
                if (name1.equals(name2)) {
                    match = true;
                } else {
                    try {
                        // System.out.println(name1+" - "+name2+" = "+nameDiff);
                        Integer.parseInt(nameDiff);
                        // Problem: "something 0h rep 1" matches
                        // "something 4h rep 1"
                        // But: "Yang_1-1_WT(COL)-1_Rep1_ATH1" should match
                        // "Yang_1-2_WT(COL)-2_Rep2_ATH1"
                        match = true;
                        Integer[] diffs = getDiffLocations(n1, n2);
                        for (int d : diffs) {
                            if (d + 1 >= name1.length()
                                    && d + 1 >= name2.length()) {
                                continue;
                            }
                            if (n1.length() >= n2.length()) {
                                if (n1.charAt(d + 1) == 'h'
                                        || n1.charAt(d + 1) == 'd') {
                                    match = false;
                                }
                            } else {
                                if (n2.charAt(d + 1) == 'h'
                                        || n2.charAt(d + 1) == 'd') {
                                    match = false;
                                }
                            }
                            if (d - 1 < 0) {
                                continue;
                            }
                            if (!(Character.isDigit(n1.charAt(d - 1)) && Character
                                    .isDigit(n2.charAt(d - 1)))) {
                                match = false;
                            }
                        }
                    } catch (NumberFormatException nfe) {
                    }
                }
            }
        } else if (n1.substring(0, n1.length() - 1).equals(
                n2.substring(0, n2.length() - 1))) {
            match = true;
            Integer[] diffs = getDiffLocations(n1, n2);
            for (int d : diffs) {
                if (d + 1 >= n1.length() && d + 1 >= n2.length()) {
                    continue;
                }
                if (n1.length() >= n2.length()) {
                    if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') {
                        match = false;
                    }
                } else {
                    if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') {
                        match = false;
                    }
                }
            }
            // } else if (diff.length() == 1) {
            // match = true;
        } else if (diff.length() == 1) {
            try {
                Integer.parseInt(diff);
                match = true;
                Integer[] diffs = getDiffLocations(n1, n2);
                for (int d : diffs) {
                    if (d + 1 >= n1.length() && d + 1 >= n2.length()) {
                        continue;
                    }
                    if (n1.length() >= n2.length()) {
                        if (n1.charAt(d + 1) == 'h' || n1.charAt(d + 1) == 'd') {
                            match = false;
                        }
                    } else {
                        if (n2.charAt(d + 1) == 'h' || n2.charAt(d + 1) == 'd') {
                            match = false;
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
            }
        }
        return match;
    }

    public static Integer[] getDiffLocations(String s1, String s2) {
        ArrayList<Integer> diffs = new ArrayList();

        for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diffs.add(Integer.valueOf(i));
            }
        }


        return diffs.toArray(new Integer[0]);
    }

    public static String getStringDiff(String s1, String s2) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; (i < s1.length()) && (i < s2.length()); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                result.append(s2.charAt(i));
            } else if (result.length() != 0) {
                result.append(" ");
            }
        }
        if (s1.length() > s2.length()) {
            result.append(s1.substring(s2.length()));
        } else if (s2.length() > s1.length()) {
            result.append(s2.substring(s1.length()));
        }
        return result.toString().trim();
    }

    public static String makeGroupName(String sampleName) {
        String result = sampleName.replaceAll(

                "(?i)[_.]?replicate\\W*\\d+\\z|[_.]?rep\\W*\\d+\\z",
                "");
        if (!result.equals(sampleName)) {
            return Utils.superClean(result);
        }
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(".*(?=\\W?\\d$)", 194);

        java.util.regex.Matcher regexMatcher = regex.matcher(sampleName);
        regexMatcher.find();
        try {
            result = regexMatcher.group();
        } catch (Exception localException) {
        }

        if (result != null) {
            return Utils.superClean(result);
        }
        return Utils.superClean(sampleName);
    }
}
