package edu.iastate.metnet.metaomgraph;

import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.ml.distance.ManhattanDistance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class CorrelationCalc {
	private double[] a;
	private double[] rank;
	//private double aveA;
	//private boolean hasBlanks;
	private boolean[] excludes;
	//private int excludeCount;

	public CorrelationCalc(double[] baseData) {
		System.out.println("creating a calc with " + baseData.length + " values");
		a = baseData;
		calcBaseValues();
	}

	public CorrelationCalc(double[] baseData, boolean[] excludes) {
		a = baseData;
		this.excludes = excludes;
		calcBaseValues();
	}

	public CorrelationCalc(Number[] baseData) {
		a = new double[baseData.length];
		for (int i = 0; i < a.length; i++) {
			a[i] = baseData[i].doubleValue();
		}
		excludes = null;
		calcBaseValues();
	}

	public CorrelationCalc(Object[] baseData) {
		if (baseData.length == 0)
			return;
		if (!(baseData[0] instanceof Number)) {
			System.out.println("Error: not Numbers");
		} else {
			a = new double[baseData.length];
			for (int i = 0; i < a.length; i++) {
				a[i] = ((Number) baseData[i]).doubleValue();
			}
			excludes = null;
			calcBaseValues();
		}
	}

	private void calcBaseValues() {
		rank = calcRank(a);
		/*if (excludes != null) {
			for (boolean excludeMe : excludes) {
				if (excludeMe) {
					excludeCount += 1;
				}
			}
		} else {
			excludeCount = 0;
		}*/
	}

	private double[] calcRank(double[] data) {
		SortableData[] myData = new SortableData[data.length];
		for (int i = 0; i < myData.length; i++) {
			myData[i] = new SortableData(data[i], i);
		}

		Arrays.sort(myData);
		int x = 0;
		double currRank = 1.0D;
		double thisRank = 0.0D;
		int y = 0;
		while (x < myData.length) {
			if (Double.isNaN(myData[x].getValue())) {
				myData[x].setValue(Double.NaN);
				x++;
			} else {
				y = x;
				thisRank = currRank;
				while ((y + 1 < myData.length) && (myData[y].getValue() == myData[(y + 1)].getValue())) {
					y++;
					currRank += 1.0D;
					thisRank += currRank;
				}
				if (y > x)
					thisRank /= (y - x + 1);
				while (x <= y) {
					myData[x].setValue(thisRank);
					x++;
				}
				currRank += 1.0D;
			}
		}
		for (int i = 0; i < myData.length; i++)
			myData[i].setSortByData(false);
		Arrays.sort(myData);
		double[] result = new double[myData.length];
		for (int i = 0; i < result.length; i++)
			result[i] = myData[i].getValue();
		return result;
	}

	

	public double pearsonCorrelation(double[] b, boolean hasSkips, Double blankValue) {

		
		return pearsonCorrelationStandard(b);
	}

	/**
	 * @author urmi
	 * @param b
	 * @return
	 */
	public double pearsonCorrelationStandard(double[] b) {
		return pearsonCorrelationStandard(a, b);
	}
	public double pearsonCorrelationStandard(double[] a,double[] b) {

		if (b.length != a.length) {
			JOptionPane.showMessageDialog(null, "Unequal lengths", "Error", JOptionPane.ERROR_MESSAGE);
			return 0;
		}

		double sx = 0.0;
		double sy = 0.0;
		double sxx = 0.0;
		double syy = 0.0;
		double sxy = 0.0;

		int n = b.length;

		for (int i = 0; i < n; ++i) {
			double x = a[i];
			double y = b[i];
			// return 0 in nan found
			if (Double.isNaN(x) || Double.isNaN(y)) {
				return 0;
			}
			sx += x;
			sy += y;
			sxx += x * x;
			syy += y * y;
			sxy += x * y;
		}

		// compute cov
		double cov = sxy / n - sx * sy / n / n;
		// SE of x
		double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
		// SE of y
		double sigmay = Math.sqrt(syy / n - sy * sy / n / n);

		// correlation is just a normalized covariation
		double corr=cov / (sigmax * sigmay);
		if(Double.isNaN(corr) || Double.isInfinite(corr)) {
			corr=0;
		}
		return corr;

	}

	

	

	

	// using apache
	public double newSpearmanCorrelation(double[] b) {
		/*SpearmansCorrelation spc = new SpearmansCorrelation();
		double thisVal = 0.0D;
		try {
			thisVal = 0.0D + spc.correlation(a, b);
		} catch (NotANumberException nane) {
			thisVal = 0.0D;
		}
		if (Double.isNaN(thisVal)) {
			thisVal = 0.0D;
		}
		return thisVal;*/
		
		return mySpearmanCorrelation(b);

	}
	
	/**
	 * function to compute spearman correlation
	 * @param b
	 * @return
	 */
	public double mySpearmanCorrelation(double[] b) {
		//double[] rankA=calcRank(a);
		double[] rankB=calcRank(b);
		return pearsonCorrelationStandard(this.rank,rankB);
	}
	

	

	

	
	

	public double euclideanDistance(Object[] b, boolean hasSkips, Double blankValue) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double[] newb = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			if (!(b[i] instanceof Number))
				return 0.0D;

			newb[i] = ((Number) b[i]).doubleValue();
		}
		return euclideanDistance(newb, hasSkips, blankValue);
	}

	public double euclideanDistance(double[] b, boolean hasSkips, Double blankValue) {
		/*
		 * if ((hasSkips) || (hasBlanks)) { return euclideanDistanceWithSkips(b,
		 * blankValue); }
		 */
		return euclideanDistanceStandard(b);
	}

	public double euclideanDistanceStandard(double[] b) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double sqDis = 0.0D;
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]) || Double.isNaN(b[i])) {
				return 0.0D;
			}
			sqDis += (a[i] - b[i]) * (a[i] - b[i]);
		}
		return Math.sqrt(sqDis);
	}

	

	public double manhattanDistance(Object[] b, boolean hasSkips, Double blankValue) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double[] newb = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			if (!(b[i] instanceof Number))
				return 0.0D;

			newb[i] = ((Number) b[i]).doubleValue();
		}
		return manhattanDistance(newb, hasSkips, blankValue);
	}

	public double manhattanDistance(double[] b, boolean hasSkips, Double blankValue) {
		/*
		 * if ((hasSkips) || (hasBlanks)) { return manhattanDistanceWithSkips(b,
		 * blankValue); }
		 */
		return manhattanDistanceStandard(b);
	}

	public double manhattanDistanceStandard(double[] b) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double diffSum = 0.0D;
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]) || Double.isNaN(b[i])) {
				return 0.0D;
			}
			diffSum += Math.abs(a[i] - b[i]);
		}
		return diffSum;
	}

	

	public double weightedEuclideanDistance(Object[] b, boolean hasSkips, Double blankValue) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double[] newb = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			if (!(b[i] instanceof Number))
				return 0.0D;

			newb[i] = ((Number) b[i]).doubleValue();
		}
		return weightedEuclideanDistance(newb, hasSkips, blankValue);
	}

	public double weightedEuclideanDistance(double[] b, boolean hasSkips, Double blankValue) {
		/*
		 * if ((hasSkips) || (hasBlanks)) { return weightedEuclideanDistanceWithSkips(b,
		 * blankValue); }
		 */
		return weightedEuclideanDistanceStandard(b);
	}

	public double weightedEuclideanDistanceStandard(double[] b) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double sqDis = 0.0D;
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]) || Double.isNaN(b[i])) {
				return 0.0D;
			}
			if ((a[i] != 0.0D) || (b[i] != 0.0D)) {
				sqDis = sqDis + (a[i] - b[i]) * (a[i] - b[i]) / (a[i] * a[i] + b[i] * b[i]);
			}
		}
		return Math.sqrt(sqDis);
	}

	

	public double weightedManhattanDistance(double[] b, boolean hasSkips, Double blankValue) {
		/*
		 * if ((hasSkips) || (hasBlanks)) { return weightedManhattanDistanceWithSkips(b,
		 * blankValue); }
		 */
		return weightedManhattanDistanceStandard(b);
	}

	public double weightedManhattanDistanceStandard(double[] b) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double diffSum = 0.0D;
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]) || Double.isNaN(b[i])) {
				return 0.0D;
			}
			if ((a[i] != 0.0D) || (b[i] != 0.0D)) {
				diffSum = diffSum + Math.abs(a[i] - b[i]) / Math.sqrt(a[i] * a[i] + b[i] * b[i]);
			}
		}
		return diffSum;
	}

	

	public double canberraDistance(Object[] b, boolean hasSkips, Double blankValue) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double[] newb = new double[b.length];
		for (int i = 0; i < b.length; i++) {
			if (!(b[i] instanceof Number))
				return 0.0D;

			newb[i] = ((Number) b[i]).doubleValue();
		}
		return canberraDistance(newb, hasSkips, blankValue);
	}

	public double canberraDistance(double[] b, boolean hasSkips, Double blankValue) {
		/*
		 * if ((hasSkips) || (hasBlanks)) { return canberraDistanceWithSkips(b,
		 * blankValue); }
		 */
		return canberraDistanceStandard(b);
	}

	public double canberraDistanceStandard(double[] b) {
		if ((b == null) || (a == null))
			return 0.0D;
		if (b.length != a.length) {
			System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
			return 0.0D;
		}
		double distance = 0.0D;
		double denom = 0.0D;
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]) || Double.isNaN(b[i])) {
				return 0.0D;
			}
			denom = Math.abs(a[i] + b[i]);

			if (denom != 0.0D)
				distance += Math.abs(a[i] - b[i]) / denom;
		}
		return distance;
	}

	
}
