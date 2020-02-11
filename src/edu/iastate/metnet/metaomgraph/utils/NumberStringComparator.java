package edu.iastate.metnet.metaomgraph.utils;

import java.util.Comparator;
import java.util.regex.Pattern;


public class NumberStringComparator<T>
        implements Comparator<T> {
    public NumberStringComparator() {
    }

    @Override
	public int compare(T arg0, T arg1) {
        if (arg0 == null) {
            if (arg1 == null) {
                return 0;
            }
            return -1;
        }

        if (arg1 == null) {
            return 1;
        }
        String s1 = arg0.toString();
        String s2 = arg1.toString();
        Pattern regex = Pattern.compile("\\d", 194);

        if ((!regex.matcher(s1).find()) || (!regex.matcher(s2).find())) {
            return s1.compareTo(s2);
        }
        int index1 = 0;
        int index2 = 0;
        while ((index1 < s1.length()) || (index2 < s2.length())) {
            if (index1 >= s1.length()) {
                return -1;
            }
            if (index2 >= s2.length()) {
                return 1;
            }
            char c1 = s1.charAt(index1++);
            char c2 = s2.charAt(index2++);
            if ((Character.isDigit(c1)) && (Character.isDigit(c2))) {
                StringBuilder num1 = new StringBuilder(c1);
                StringBuilder num2 = new StringBuilder(c2);
                if (index1 < s1.length()) {
                    c1 = s1.charAt(index1);
                } else {
                    c1 = '\000';
                }
                if (index2 < s2.length()) {
                    c2 = s2.charAt(index2);
                } else {
                    c2 = '\000';
                }
                while ((Character.isDigit(c1)) || ('.' == c1)) {
                    num1.append(c1);
                    index1++;
                    if (index1 < s1.length()) {
                        c1 = s1.charAt(index1);
                    } else {
                        c1 = '\000';
                    }
                }
                while ((Character.isDigit(c2)) || ('.' == c2)) {
                    num2.append(c2);
                    index2++;
                    if (index2 < s2.length()) {
                        c2 = s2.charAt(index2);
                    } else {
                        c2 = '\000';
                    }
                }
                try {
                    double val1 = Double.parseDouble(num1.toString());
                    double val2 = Double.parseDouble(num2.toString());
                    System.out.println("Comparing " + val1 + " to " + val2);
                    if (val1 > val2)
                        return 1;
                    if (val2 > val1) {
                        return -1;
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("num1: " + num1);
                    System.err.println("num2: " + num2);
                    nfe.printStackTrace();
                }
            }
            if (c1 > c2)
                return 1;
            if (c2 > c1) {
                return -1;
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        String a = "Suspension culture - Methyl jasmonate, 100 µM for 6 hours replicate 2";
        String b = "Suspension culture - Methyl jasmonate, 100 µM for 6 hours";
        NumberStringComparator<String> comp = new NumberStringComparator();
        System.out.println("1 - " + comp.compare(a, b));
        a = "Suspension culture - Methyl jasmonate, 100 µM for 6 hours replicate 2";
        b = "Suspension culture - Methyl jasmonate, 100 µM for 12 hours replicate 2";
        System.out.println("-1 - " + comp.compare(a, b));
        a = "Suspension culture - Methyl jasmonate, 100 µM for 6.5 hours replicate 2";
        b = "Suspension culture - Methyl jasmonate, 100 µM for 6.40 hours replicate 2";
        System.out.println("1 - " + comp.compare(a, b));
        a = "2 - hello";
        b = "10 - hello";
        System.out.println("-1 - " + comp.compare(a, b));
    }
}
