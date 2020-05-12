package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;

public class ComputeMean {
	private ArrayList<Integer> integerList = new ArrayList<Integer>();
	
	public static float calcMean(ArrayList<Integer> integerList){
		float sum = 0;
		if (!integerList.isEmpty()) {
			for (Integer i : integerList){
				sum = sum + i;
			}
		}
		return sum/integerList.size();
	}
	

}