package edu.iastate.metnet.metaomgraph.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;


public class FlatFileConverter {
    private TreeMap<String, String> converty;

    public String convert(String convertMe) {
        if ((convertMe == null) || (converty == null))
            return null;
        return converty.get(convertMe.toLowerCase());
    }


    public FlatFileConverter(InputStream in, String delimiter) {
        try {
            BufferedReader infile = new BufferedReader(
                    new InputStreamReader(in));
            converty = new TreeMap();
            String temp = infile.readLine();

            while (infile.ready())
                if ((temp == null) || (temp.equals(""))) {
                    temp = infile.readLine();
                } else {
                    String[] splitTemp = temp.split(delimiter, 2);
                    String str1 = splitTemp[0];
                    String str2;
                    if (splitTemp.length > 1) {
                        str2 = splitTemp[1];


                    } else {

                        str2 = "\"NO MATCH\",";
                    }
                    temp = infile.readLine();
                    str1.replaceAll("\\*", "");
                    str2.replaceAll("\\*", "");
                    str1 = str1.toLowerCase();

                    if (converty.get(str1) != null) {
                        str2 = converty.get(str1) + "; " + str2;
                    }
                    converty.put(str1, str2);
                }
            infile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
