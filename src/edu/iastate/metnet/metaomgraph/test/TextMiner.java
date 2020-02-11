package edu.iastate.metnet.metaomgraph.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextMiner {
    private static final String delimiter = "\t;;;\t\t\t;\t;\t\t\t;\t;;;;;;\t";

    public TextMiner() {
    }

    public static String[][] mineTextForTargets(InputStream source, TargetString[] targets) throws IOException {
        return mineTextForTargets(new java.io.InputStreamReader(source), targets);
    }

    public static String[][] mineTextForTargets(Reader source, TargetString[] targets) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader in;
        if ((source instanceof BufferedReader)) {
            in = (BufferedReader) source;
        } else {
            in = new BufferedReader(source);
        }
        String thisLine;
        while ((thisLine = in.readLine()) != null) {
            result.append(thisLine + " ");
        }
        return mineTextForTargets(result.toString(), targets);
    }

    public static String[][] mineTextForTargets(String source, TargetString[] targets) {
        Pattern[] patterns = new Pattern[targets.length];
        Matcher[] matchers = new Matcher[targets.length];
        for (int i = 0; i < targets.length; i++) {


            String regex = targets[i].startValue + ".*?" + targets[i].endValue;
            patterns[i] = Pattern.compile(regex);
            matchers[i] = patterns[i].matcher(source);
        }
        String[][] result = new String[targets.length][];
        String[] hits = new String[targets.length];
        for (int i = 0; i < matchers.length; i++) {
            while (matchers[i].find()) {
                String thisHit = matchers[i].group();
                boolean matches = true;
                for (String thisRegex : targets[i].regexps) {
                    if (matches) {
                        matches = thisHit.contains(thisRegex);
                    }
                }


                if (matches) {
                    if ((hits[i] == null) || (hits[i].equals(""))) {
                        hits[i] = thisHit;
                    } else {
                        int tmp208_206 = i;
                        String[] tmp208_204 = hits;
                        tmp208_204[tmp208_206] = (tmp208_204[tmp208_206] + "\t;;;\t\t\t;\t;\t\t\t;\t;;;;;;\t" + thisHit);
                    }
                }
            }
        }

        for (int i = 0; i < result.length; i++) {
            if (hits[i] != null) {
                result[i] = hits[i].split("\t;;;\t\t\t;\t;\t\t\t;\t;;;;;;\t");
            } else {
                result[i] = null;
            }
        }

        return result;
    }

    public static String[][] oldMineTextForTargets(String source, TargetString[] targets) throws IOException {
        String[][] result = new String[targets.length][];
        BufferedReader in = new BufferedReader(new java.io.StringReader(source));

        String[] starters = new String[targets.length];
        String[] enders = new String[targets.length];
        for (int i = 0; i < targets.length; i++) {
            starters[i] = targets[i].startValue;
            enders[i] = targets[i].endValue;
        }

        String thisLine;
        String[] hits = new String[targets.length];

        while ((thisLine = in.readLine()) != null) {
            for (int i = 0; i < starters.length; i++) {
                if (thisLine.contains(starters[i])) {
                    in.mark(20000);
                    String thisHit = thisLine;
                    String nextLine = in.readLine();
                    while (!thisHit.contains(enders[i])
                            && thisHit.length() < 20000 && nextLine != null) {
                        thisHit += "\n" + nextLine;
                    }
                    boolean matches = true;
                    for (String thisRegex : targets[i].regexps) {
                        if (matches) {
                            matches = thisHit.contains(thisRegex);
                            // if (!matches) {
                            // System.out.println(thisHit+" didn't match
                            // "+thisRegex);
                            // }
                        }
                    }
                    if (matches) {
                        // System.out.println(thisHit);
                        if (hits[i] == null || hits[i].equals("")) {
                            hits[i] = thisHit;
                        } else {
                            hits[i] += delimiter + thisHit;
                        }
                    }
                    in.reset();
                }
            }
        }
        for (int i = 0; i < result.length; i++) {
            if (hits[i] != null) {
                result[i] = hits[i].split(delimiter);
            } else {
                result[i] = null;
            }
        }
        return result;
    }


    public static class TargetString {
        public String startValue;


        public String endValue;


        public String[] regexps;


        public TargetString(String startValue, String endValue, String[] regexps) {
            this.startValue = startValue;
            this.endValue = endValue;
            this.regexps = regexps;
        }

        public TargetString(String startValue, String endValue, String regex) {
            this.startValue = startValue;
            this.endValue = endValue;
            regexps = new String[]{regex};
        }

        @Override
		public boolean equals(Object obj) {
            if ((obj instanceof TargetString)) {
                TargetString compMe = (TargetString) obj;
                boolean result = (startValue.equals(startValue)) &&
                        (endValue.equals(endValue)) &&
                        (regexps.length == regexps.length);
                int i = 0;
                while ((result) && (i < regexps.length)) {
                    result = regexps[i].equals(regexps[i]);
                    i++;
                }
                return result;
            }
            return false;
        }
    }
}
