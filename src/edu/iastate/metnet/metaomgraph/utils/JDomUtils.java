package edu.iastate.metnet.metaomgraph.utils;

import org.jdom.Verifier;

public class JDomUtils {
    public JDomUtils() {
    }

    public static String convertToValidElementName(String convertMe) {
        if (Verifier.checkElementName(convertMe) == null) {
            return convertMe;
        }
        System.out.println(Verifier.checkElementName(convertMe) + " (" +
                convertMe + ")");
        String result = "";
        for (int x = 0; x < convertMe.length(); x++) {
            char nextChar = convertMe.charAt(x);
            String addMe = nextChar + "";
            switch (nextChar) {
                case '<':
                    addMe = "&lt;";
                    break;
                case '>':
                    addMe = "&gt;";
                    break;
                case ' ':
                    addMe = "_";
                    break;
                case '[':
                case '{':
                    addMe = "(";
                    break;
                case ']':
                case '}':
                    addMe = ")";
            }

            if (Verifier.checkElementName(addMe) == null) {
                result = result + addMe;
            }
        }
        return result;
    }
}
