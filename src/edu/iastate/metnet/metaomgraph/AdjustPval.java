package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Class contains p value adjustment methods
 * 
 * @author mrbai
 *
 */
public class AdjustPval {

	/**
	 * apply Bonferroni method for p value correction
	 * 
	 * @param pvals
	 * @return
	 */
	public double[] getBonferroniAdj(double[] pvals) {
		if (pvals == null || pvals.length < 2) {
			return pvals;
		}
		double[] res = new double[pvals.length];
		int m = pvals.length;
		for (int i = 0; i < res.length; i++) {
			res[i] = pvals[i] * m;
			if (res[i] > 1) {
				res[i] = 1;
			}
		}

		return res;
	}

	/**
	 * apply Benjamini Hochberg method for p value correction
	 * 
	 * @param pvals
	 * @return
	 */
	public double[] getBHAdj(double[] pvals) {
		if (pvals == null || pvals.length < 2) {
			return pvals;
		}
		int m = pvals.length;
		double[] res = new double[m];

		// idx now contains indices in sorted order
		int[] idx = getSortedIndex(pvals);
		// get sorted pvalues
		double[] pvSorted = new double[m];
		for (int i = 0; i < idx.length; i++) {
			pvSorted[i] = pvals[idx[i]];
		}

		// start correction
		for (int i = m - 1; i >= 0; i--) {
			if (i == m - 1) {
				res[i] = pvSorted[i];
			} else {
				double thisPV = pvSorted[i];
				int rank = i + 1;
				double v1 = res[i + 1];
				double v2 = thisPV * (m / (double) rank);
				if (v1 < v2) {
					res[i] = v1;
				} else {
					res[i] = v2;
				}

			}
		}

		// order result correctly in orignal order
		double[] resSorted = new double[m];
		for (int i = 0; i < idx.length; i++) {
			resSorted[idx[i]] = res[i];
		}
		return resSorted;
	}

	public double[] getHolmsAdj(double[] pvals) {

		// if less than 2 elements
		if (pvals == null || pvals.length < 2) {
			return pvals;
		}

		int m = pvals.length;

		int[] sortedInd = getSortedIndex(pvals);
		double[] sortedIndDouble = intToDouble(sortedInd);
		double[] cummaxInput = new double[m];

		for (int i = 0; i < m; ++i) {
			cummaxInput[i] = (m - i) * pvals[sortedInd[i]];
		}
		int[] ro = getSortedIndex(sortedIndDouble);
		double[] cummaxOutput = cummax(cummaxInput);
		double[] pmin = pminx(cummaxOutput, 1.0);
		double[] result = new double[m];
		for (int i = 0; i < m; ++i) {
			result[i] = pmin[ro[i]];
		}

		return result;

	}

	private double[] intToDouble(int[] array) {
		double[] result = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}

	private double[] pminx(double[] array, double x) {
		if (array.length < 1) {
			return null;
		}
		double[] result = new double[array.length];
		for (int i = 0; i < array.length; ++i) {
			if (array[i] < x) {
				result[i] = array[i];
			} else {
				result[i] = x;
			}
		}
		return result;
	}

	private double[] cummax(double[] array) {
		if (array.length < 1) {
			return null;
		}

		double[] output = new double[array.length];
		double cumulativeMax = array[0];
		for (int i = 0; i < array.length; ++i) {
			if (array[i] > cumulativeMax)
				cumulativeMax = array[i];
			output[i] = cumulativeMax;
		}
		return output;
	}

	/**
	 * get the indices sorted by value
	 * 
	 * @param pvals
	 * @return
	 */
	int[] getSortedIndex(double[] pvals) {
		int m = pvals.length;
		// get sorted order of pvals
		Integer[] idx = new Integer[m];
		for (int i = 0; i < idx.length; i++)
			idx[i] = i;
		Arrays.sort(idx, new Comparator<Integer>() {
			@Override
			public int compare(Integer i1, Integer i2) {
				return Double.compare(pvals[i1], pvals[i2]);
			}
		});

		int[] res = new int[idx.length];
		for (int i = 0; i < idx.length; i++) {
			res[i] = idx[i];
		}
		return res;
	}

	/**
	 * Function to return all the methods implemented
	 * 
	 * @return
	 */
	public static String[] getMethodNames() {
		return new String[] { "Benjamini-Hochberg", "Holm", "Bonferroni" };

	}

	/**
	 * Compute adjusted p values and return as a list
	 * 
	 * @param pv
	 * @param methodName
	 * @return
	 */
	public static List<Double> computeAdjPV(List<Double> pv) {
		return computeAdjPV(pv, "Benjamini-Hochberg");
	}

	public static List<Double> computeAdjPV(List<Double> pv, String methodName) {

		if (pv == null) {
			return null;
		}
		List<Double> res = null;
		double[] adjPV = null;
		// find adjusted pvalues;default method BH
		AdjustPval ob = new AdjustPval();

		if (methodName == null || methodName.length() < 1 || methodName == "Benjamini-Hochberg") {
			adjPV = ob.getBHAdj(pv.stream().mapToDouble(d -> d).toArray());
		} else if (methodName == "Bonferroni") {
			adjPV = ob.getBonferroniAdj(pv.stream().mapToDouble(d -> d).toArray());
		} else if (methodName == "Holm") {
			adjPV = ob.getHolmsAdj(pv.stream().mapToDouble(d -> d).toArray());
		}

		if (adjPV != null) {
			res = new ArrayList<>();
			for (double d : adjPV) {
				res.add(d);
			}
		}
		return res;
	}

	/**
	 * apply Benjamini and Yekutieli method for p value correction
	 * 
	 * @param pvals
	 * @return
	 */
	/*
	 * public double[] getBYAdj(double[] pvals) {
	 * 
	 * return null; }
	 */

	public static void main(String[] args) {
		// test
		AdjustPval ob = new AdjustPval();
		double[] pv = { 1, 0, 0.1, 0.3, 0.005, 0.025 };
		System.out.println("Orig: " + Arrays.toString((pv)));
		System.out.println("res BH: " + Arrays.toString(ob.getBHAdj(pv)));
		System.out.println("res bonf: " + Arrays.toString(ob.getBonferroniAdj(pv)));
		System.out.println("res holms: " + Arrays.toString(ob.getHolmsAdj(pv)));

	}

}
