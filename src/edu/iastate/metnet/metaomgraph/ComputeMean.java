/**
 * 
 */
package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;

/**
 * @author sumanth
 *
 */
public class ComputeMean {
	private ArrayList<Integer> integerList;

	/**
	 * @param integerList arraylist from which the mean is calculated.
	 *
	 */
	public ComputeMean(ArrayList<Integer> integerList) {
		this.integerList = new ArrayList<Integer>();
		this.integerList = integerList;
	}
	
	/**
	 * @return Double mean value of the integers in arraylist.
	 *
	 */
	public Double GetMean() {
		if(null == integerList || integerList.isEmpty()) {
			return 0.0;
		}
		
		// calculate the average using stream class.
		Double mean = integerList.stream()
				.mapToInt(num -> num)
				.average()
				.orElse(0.0);
		
		return mean;
	}
	
	/**
	 * @param integerList update the ArrayList values with the new values.
	 *
	 */	
	public void UpdateArrayList(ArrayList<Integer> integerList) {
		this.integerList = integerList;
	}
}
