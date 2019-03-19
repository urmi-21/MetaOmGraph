package edu.iastate.metnet.metaomgraph;

import java.util.Comparator;

import javax.swing.JOptionPane;

/**
 * Class for comparision of numbers and string in the tables
 * 
 * @author urmi
 *
 */

public class AlphanumericComparator implements Comparator {
	public AlphanumericComparator() {
	}
	
	public int compare(Object o1, Object o2) {
		//convert to string and lower case for case insensitive match
		String s1 = o1.toString().toLowerCase();
		String s2 = o2.toString().toLowerCase();
		return compareNatural(s1, s2);
	}

	//this approach doesn't always work; results in java.lang.IllegalArgumentException: Comparison method violates its general contract! exception
	public int compareOLD(Object o1, Object o2) {
		String s1 = o1.toString();
		String s2 = o2.toString();
		final Double num1 = getDouble(s1);
		final Double num2 = getDouble(s2);
		try {

			if (num1 != null && num2 != null) {
				//System.out.println("in AC NUM:" + num1 + ".." + num2);
				return Double.compare(num1, num1);
				//return num1.compareTo(num2);
			}
			
			//System.out.println("in AC:" + s1 + ".." + s2);
			return s1.compareTo(s2);
		} catch (IllegalArgumentException e) {
			//JOptionPane.showMessageDialog(null, "ERR:" + s1 + ".." + s2);
			return 0;
		}

	}

	private Double getDouble(String number) {
		try {
			return Double.parseDouble(number);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	//function for comparing alphanumeric strings
	//code reference https://stackoverflow.com/questions/7270447/java-string-number-comparator
	//could be improved later
	public  int compareNatural(String a, String b) {
	    int la = a.length();
	    int lb = b.length();
	    int ka = 0;
	    int kb = 0;
	    while (true) {
	        if (ka == la)
	            return kb == lb ? 0 : -1;
	        if (kb == lb)
	            return 1;
	        if (a.charAt(ka) >= '0' && a.charAt(ka) <= '9' && b.charAt(kb) >= '0' && b.charAt(kb) <= '9') {
	            int na = 0;
	            int nb = 0;
	            while (ka < la && a.charAt(ka) == '0')
	                ka++;
	            while (ka + na < la && a.charAt(ka + na) >= '0' && a.charAt(ka + na) <= '9')
	                na++;
	            while (kb < lb && b.charAt(kb) == '0')
	                kb++;
	            while (kb + nb < lb && b.charAt(kb + nb) >= '0' && b.charAt(kb + nb) <= '9')
	                nb++;
	            if (na > nb)
	                return 1;
	            if (nb > na)
	                return -1;
	            if (ka == la)
	                return kb == lb ? 0 : -1;
	            if (kb == lb)
	                return 1;

	        }
	        if (a.charAt(ka) != b.charAt(kb))
	            return a.charAt(ka) - b.charAt(kb);
	        ka++;
	        kb++;
	    }
	}
}