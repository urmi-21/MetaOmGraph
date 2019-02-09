package edu.iastate.metnet.metaomgraph;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JOptionPane;

import edu.iastate.metnet.metaomgraph.chart.HistogramChart;

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
		double[] res = new double[pvals.length];
		int m = pvals.length;
		for (int i = 0; i < res.length; i++) {
			res[i] = pvals[i] * m;
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
		int m = pvals.length;
		double[] res = new double[m];
		/*
		 * int [] origInd=new int[m]; for(int i=0;i<origInd.length;i++) { origInd[i]=i;
		 * }
		 */

		// sort pvals and origInd together
		Integer[] idx = new Integer[m];
		for (int i = 0; i < idx.length; i++)
			idx[i] = i;
		Arrays.sort(idx, new Comparator<Integer>() {
			public int compare(Integer i1, Integer i2) {
				return Double.compare(pvals[i1], pvals[i2]);
			}
		});

		// idx now contains indices in sorted order
		double[] pvSorted = new double[m];
		for (int i = 0; i < idx.length; i++) {
			pvSorted[i] = pvals[idx[i]];
		}
		System.out.println("SA: " + Arrays.toString(pvSorted));

		// start correction
		for (int i = m - 1; i >= 0; i--) {
			if (i == m - 1) {
				res[i] = pvSorted[i];
			} else {
				double thisPV = pvSorted[i];
				int rank = i + 1;

				double v1 = res[i + 1];
				double v2 = thisPV * (m / (double) rank);
				res[i] = Math.min(v1, v2);
			}
		}
		
		//order result correctly in orignal order
		double[] resSorted = new double[m];
		for (int i = 0; i < idx.length; i++) {
			resSorted[idx[i]] = res[i];
		}

		return resSorted;
	}

	public static void main(String[] args) {
		AdjustPval ob = new AdjustPval();
		double[] pv = { 1, 0, 0.1, 0.3, 0.005,0.1,0.03,0.12,0.34 };

		System.out.println("Orig: " + Arrays.toString((pv)));
		System.out.println("res: " + Arrays.toString(ob.getBHAdj(pv)));
		System.out.println("res: " + Arrays.toString(ob.getBonferroniAdj(pv)));
	}

}
