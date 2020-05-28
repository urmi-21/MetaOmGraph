package edu.iastate.metnet.metaomgraph;

import java.util.List;

public class ComputeMean {

	public int getMean(List<Integer> l) {

		int mean = 0;

		if(!l.isEmpty()) {
			for(int x : l) {
				mean += x;
			}
			mean /= l.size();
		}
		return mean;
	}
	
	
	public long getMeanLong(List<Long> l) {

		long mean = 0;

		if(!l.isEmpty()) {
			for(long x : l) {
				mean += x;
			}
			mean /= l.size();
		}
		return mean;
	}
	
	
	public double getMeanDouble(List<Double> d) {

		double mean = 0;

		if(!d.isEmpty()) {
			for(double x : d) {
				mean += x;
			}
			mean /= d.size();
		}
		return mean;
	}

}
