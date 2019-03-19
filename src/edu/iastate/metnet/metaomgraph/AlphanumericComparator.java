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
}