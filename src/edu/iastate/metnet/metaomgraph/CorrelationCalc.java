package edu.iastate.metnet.metaomgraph;

import java.io.PrintStream;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.commons.math3.ml.distance.ManhattanDistance;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;


public class CorrelationCalc {
    private double[] a;
    private double[] rank;
    private double aveA;
    private boolean hasBlanks;
    private boolean[] excludes;
    private int excludeCount;

    public CorrelationCalc(double[] baseData) {
        System.out.println("creating a calc with " + baseData.length + " values");
        a = baseData;
        excludes = null;
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
        aveA = 0.0D;
        hasBlanks = false;
        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i])) {
                aveA += a[i];
            } else {
                hasBlanks = true;
            }
        }
        aveA /= a.length;
        rank = calcRank(a);
        if (excludes != null) {
            for (boolean excludeMe : excludes) {
                if (excludeMe) {
                    excludeCount += 1;
                }
            }
        } else {
            excludeCount = 0;
        }
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
                if (y > x) thisRank /= (y - x + 1);
                while (x <= y) {
                    myData[x].setValue(thisRank);
                    x++;
                }
                currRank += 1.0D;
            }
        }
        for (int i = 0; i < myData.length; i++) myData[i].setSortByData(false);
        Arrays.sort(myData);
        double[] result = new double[myData.length];
        for (int i = 0; i < result.length; i++) result[i] = myData[i].getValue();
        return result;
    }

    private double[] calcRankWithSkips(double[] data, boolean[] skip, int n) {
        SortableData[] myData = new SortableData[n];
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (!skip[i]) {
                myData[index] = new SortableData(data[i], index);
                index++;
            }
        }

        Arrays.sort(myData);
        int x = 0;
        int y = 0;
        double currRank = 1.0D;
        double thisRank = 0.0D;
        while (x < myData.length) {
            y = x;
            thisRank = currRank;
            while ((y + 1 < myData.length) && (myData[y].getValue() == myData[(y + 1)].getValue())) {
                y++;
                currRank += 1.0D;
                thisRank += currRank;
            }
            if (y > x) thisRank /= (y - x + 1);
            while (x <= y) {
                myData[x].setValue(thisRank);
                x++;
            }
            currRank += 1.0D;
        }
        for (int i = 0; i < myData.length; i++) myData[i].setSortByData(false);
        Arrays.sort(myData);

        double[] result = new double[myData.length];
        for (int i = 0; i < result.length; i++) result[i] = myData[i].getValue();

        return result;
    }


    public double pearsonCorrelation(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return pearsonCorrelation(newb, hasSkips, blankValue);
    }

    public double pearsonCorrelation(double[] b, boolean hasSkips, Double blankValue) {
    	
        if ((hasSkips) || (hasBlanks)) {
        	JOptionPane.showMessageDialog(null, "*a:"+Arrays.toString(a));
            JOptionPane.showMessageDialog(null, "*b:"+Arrays.toString(b));
            JOptionPane.showMessageDialog(null, "blank val:"+blankValue);
            return pearsonCorrelationWithSkips(b, blankValue);
        }
        
        JOptionPane.showMessageDialog(null, "a:"+Arrays.toString(a));
        JOptionPane.showMessageDialog(null, "b:"+Arrays.toString(b));
        return new PearsonsCorrelation().correlation(a, b);
        
        //return pearsonCorrelationStandard(b);
    }


    public double oldPearsonCorrelation(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double aveB = 0.0D;
        for (int i = 0; i < b.length; i++) {
            aveB += b[i];
        }
        aveB /= b.length;

        double[] A = new double[a.length];
        double[] B = new double[b.length];
        for (int i = 0; i < A.length; i++) {
            A[i] = (a[i] - aveA);
            b[i] -= aveB;
        }
        double top = 0.0D;
        double bottom = 0.0D;
        double bottom1 = 0.0D;
        double bottom2 = 0.0D;
        for (int i = 0; i < A.length; i++) {
            top += A[i] * B[i];
            bottom1 += A[i] * A[i];
            bottom2 += B[i] * B[i];
        }
        bottom = Math.sqrt(bottom1 * bottom2);
        return top / bottom;
    }

    public double pearsonCorrelationStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        int n = a.length;
        double aveB = 0.0D;
        double top = 0.0D;
        double bottom = 0.0D;
        double bottom1 = 0.0D;
        double bottom2 = 0.0D;
        for (int i = 0; i < n; i++) {
            aveB += b[i];
            top += a[i] * b[i];
            bottom1 += a[i] * a[i];
            bottom2 += b[i] * b[i];
        }
        aveB /= n;
        top -= aveA * aveB * n;
        bottom1 -= n * aveA * aveA;
        if (bottom1 == 0.0D) {
            bottom1 = 1.0D;
        }
        bottom2 -= n * aveB * aveB;
        if (bottom2 == 0.0D) {
            bottom2 = 1.0D;
        }
        bottom = Math.sqrt(bottom1 * bottom2);

        if (bottom == 0.0D) {
            System.err.print("A:");
            for (double thisA : a) {
                System.err.print(" " + thisA);
            }
            System.err.println();
            System.err.print("B:");
            for (double thisB : b) {
                System.err.print(" " + thisB);
            }
            System.err.println();
            System.err.println("Bottom 1: " + bottom1);
            System.err.println("Bottom 2: " + bottom2);
            return 1.0D;
        }
        return top / bottom;
    }

    public double pearsonCorrelationWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            //System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        int n = a.length;
        double newAveA = 0.0D;
        double aveB = 0.0D;
        double top = 0.0D;
        double bottom = 0.0D;
        double bottom1 = 0.0D;
        double bottom2 = 0.0D;
        int skips = 0;
        double nextA = 0.0D;
        double nextB = 0.0D;
        for (int i = 0; i < n; i++) {
            nextA = a[i];
            nextB = b[i];

            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB))) {
                newAveA += nextA;
                aveB += nextB;
                top += nextA * nextB;
                bottom1 += nextA * nextA;
                bottom2 += nextB * nextB;
            } else {
                skips++;
            }
        }
        n -= skips;
        if (n == 0) return 0.0D;

        aveB /= n;
        newAveA /= n;
        top -= newAveA * aveB * n;
        bottom1 -= n * newAveA * newAveA;
        bottom2 -= n * aveB * aveB;
        bottom = Math.sqrt(bottom1 * bottom2);
        double result = top / bottom;
        if (Double.isNaN(result)) return 0.0D;

        return result;
    }

    public double spearmanCorrelationWithSkips(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        boolean[] skipMe = new boolean[a.length];
        int n = 0;
        for (int i = 0; i < a.length; i++) {
            if ((!Double.isNaN(a[i])) && (!Double.isNaN(b[i]))) {
                skipMe[i] = false;
                n++;
            } else {
                skipMe[i] = true;
            }
        }
        double sumDSquared = 0.0D;
        double[] aRank = calcRankWithSkips(a, skipMe, n);
        double[] bRank = calcRankWithSkips(b, skipMe, n);
        for (int i = 0; i < n; i++) {
            sumDSquared += (aRank[i] - bRank[i]) * (aRank[i] - bRank[i]);
        }
        double result = 1.0D - 6.0D * sumDSquared / (n * (n * n - 1));
        if (Double.isNaN(result)) return 0.0D;

        return result;
    }

    public double spearmanCorrelationWithReplacements(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }

        double[] newA = new double[a.length - excludeCount];
        double[] newB = new double[b.length - excludeCount];
        int index = 0;
        for (int i = 0; i < a.length; i++) {
            if (excludes == null || !excludes[i]) {
                if (!Double.isNaN(a[i])) newA[index] = a[i];
                else newA[index] = blankValue.doubleValue();

                if (!Double.isNaN(b[i])) newB[index] = b[i];
                else newB[index] = blankValue.doubleValue();

                index++;
            }
        }
        double sumDSquared = 0.0D;
        double[] aRank = calcRank(newA);
        double[] bRank = calcRank(newB);
        int n = aRank.length;
        for (int i = 0; i < n; i++) {
            sumDSquared += (aRank[i] - bRank[i]) * (aRank[i] - bRank[i]);
        }
        double result = 1.0D - 6.0D * sumDSquared / (n * (n * n - 1));
        if (Double.isNaN(result)) return 0.0D;

        return result;
    }


    public double spearmanCorrelationStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double sumdsquared = 0.0D;
        double[] bRank = calcRank(b);
        for (int x = 0; x < rank.length; x++) {
            sumdsquared += (rank[x] - bRank[x]) * (rank[x] - bRank[x]);
        }
        int n = rank.length;
        double result = 1.0D - 6.0D * sumdsquared / (n * (n * n - 1));
        return result;
    }

    //not used for now, needs fixing
    public double newSpearmanCorrelation(double[] b, boolean hasSkips, Double blankValue) {
    	SpearmansCorrelation spc = new SpearmansCorrelation();
    	JOptionPane.showMessageDialog(null, "a:"+Arrays.toString(a));
    	JOptionPane.showMessageDialog(null, "b:"+Arrays.toString(b));
    	double thisVal = 0.0D + spc.correlation(a, b);
    	if (Double.isNaN(thisVal)) {
    		thisVal = 0.0D;
		}
    	return thisVal;
    	
        /*if ((hasSkips) || (hasBlanks)) {
            return newspearmanCorrelationWithSkips(b, blankValue);
        }
        return newspearmanCorrelationStandard(b);*/
    }

    private double newspearmanCorrelationStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] bRank = calcRank(b);
        int n = rank.length;
        double aveB = 0.0D;
        double top = 0.0D;
        double bottom = 0.0D;
        double bottom1 = 0.0D;
        double bottom2 = 0.0D;
        for (int i = 0; i < n; i++) {
            aveB += bRank[i];
            top += rank[i] * bRank[i];
            bottom1 += rank[i] * rank[i];
            bottom2 += bRank[i] * bRank[i];
        }
        aveB /= n;
        top -= aveB * aveB * n;
        bottom1 -= n * aveB * aveB;
        bottom2 -= n * aveB * aveB;
        bottom = Math.sqrt(bottom1 * bottom2);

        return top / bottom;
    }

    private double newspearmanCorrelationWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        int n = a.length;
        double[] bRank = calcRank(b);
        double newAveA = 0.0D;
        double aveB = 0.0D;
        double top = 0.0D;
        double bottom = 0.0D;
        double bottom1 = 0.0D;
        double bottom2 = 0.0D;
        int skips = 0;
        double nextA = 0.0D;
        double nextB = 0.0D;
        for (int i = 0; i < n; i++) {
            nextA = rank[i];
            nextB = bRank[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) nextA = blankValue.doubleValue();
                if ((Double.isNaN(nextB)) && (blankValue != null)) nextB = blankValue.doubleValue();
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB))) {
                newAveA += nextA;
                aveB += nextB;
                top += nextA * nextB;
                bottom1 += nextA * nextA;
                bottom2 += nextB * nextB;
            } else {
                skips++;
            }
        }
        n -= skips;
        if (n == 0) return 0.0D;

        aveB /= n;
        newAveA /= n;
        top -= newAveA * aveB * n;
        bottom1 -= n * newAveA * newAveA;
        bottom2 -= n * aveB * aveB;
        bottom = Math.sqrt(bottom1 * bottom2);
        double result = top / bottom;
        if (Double.isNaN(result)) {
            return 0.0D;
        }
        return result;
    }

    public double spearmanCorrelation(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            if (blankValue == null) {
                return spearmanCorrelationWithSkips(b);
            }
            return spearmanCorrelationWithReplacements(b, blankValue);
        }

        return spearmanCorrelationStandard(b);
    }


    public double spearmanCorrelation(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return spearmanCorrelation(newb, hasSkips, blankValue);
    }


    public double euclideanDistance(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return euclideanDistance(newb, hasSkips, blankValue);
    }

    public double euclideanDistance(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            return euclideanDistanceWithSkips(b, blankValue);
        }
        return euclideanDistanceStandard(b);
    }

    public double euclideanDistanceStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double sqDis = 0.0D;
        for (int i = 0; i < a.length; i++) {
            sqDis += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(sqDis);
    }

    public double euclideanDistanceWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double sqDis = 0.0D;
        for (int i = 0; i < a.length; i++) {
            double nextA = a[i];
            double nextB = b[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB))) {
                sqDis += (nextA - nextB) * (nextA - nextB);
            }
        }
        return Math.sqrt(sqDis);
    }

    public double manhattanDistance(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return manhattanDistance(newb, hasSkips, blankValue);
    }

    public double manhattanDistance(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            return manhattanDistanceWithSkips(b, blankValue);
        }
        return manhattanDistanceStandard(b);
    }

    public double manhattanDistanceStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double diffSum = 0.0D;
        for (int i = 0; i < a.length; i++) {
            diffSum += Math.abs(a[i] - b[i]);
        }
        return diffSum;
    }

    public double manhattanDistanceWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double diffSum = 0.0D;
        double nextA = 0.0D;
        double nextB = 0.0D;
        for (int i = 0; i < a.length; i++) {
            nextA = a[i];
            nextB = b[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB))) {
                diffSum += Math.abs(nextA - nextB);
            }
        }
        return diffSum;
    }

    public double weightedEuclideanDistance(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return weightedEuclideanDistance(newb, hasSkips, blankValue);
    }

    public double weightedEuclideanDistance(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            return weightedEuclideanDistanceWithSkips(b, blankValue);
        }
        return weightedEuclideanDistanceStandard(b);
    }

    public double weightedEuclideanDistanceStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double sqDis = 0.0D;
        for (int i = 0; i < a.length; i++) {
            if ((a[i] != 0.0D) || (b[i] != 0.0D)) {
                sqDis = sqDis + (a[i] - b[i]) * (a[i] - b[i]) / (a[i] * a[i] + b[i] * b[i]);
            }
        }
        return Math.sqrt(sqDis);
    }

    public double weightedEuclideanDistanceWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double sqDis = 0.0D;
        double nextA = 0.0D;
        double nextB = 0.0D;
        for (int i = 0; i < a.length; i++) {
            nextA = a[i];
            nextB = b[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB)) && ((nextA != 0.0D) || (nextB != 0.0D))) {
                sqDis = sqDis + (nextA - nextB) * (nextA - nextB) / (nextA * nextA + nextB * nextB);
            }
        }

        return Math.sqrt(sqDis);
    }

    public double weightedManhattanDistance(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return weightedManhattanDistance(newb, hasSkips, blankValue);
    }

    public double weightedManhattanDistance(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            return weightedManhattanDistanceWithSkips(b, blankValue);
        }
        return weightedManhattanDistanceStandard(b);
    }

    public double weightedManhattanDistanceStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double diffSum = 0.0D;
        for (int i = 0; i < a.length; i++) {
            if ((a[i] != 0.0D) || (b[i] != 0.0D)) {
                diffSum = diffSum + Math.abs(a[i] - b[i]) / Math.sqrt(a[i] * a[i] + b[i] * b[i]);
            }
        }
        return diffSum;
    }

    public double weightedManhattanDistanceWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double diffSum = 0.0D;
        double nextA = 0.0D;
        double nextB = 0.0D;
        for (int i = 0; i < a.length; i++) {
            nextA = a[i];
            nextB = b[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB)) && ((nextA != 0.0D) || (nextB != 0.0D))) {
                diffSum = diffSum + Math.abs(nextA - nextB) / Math.sqrt(nextA * nextA + nextB * nextB);
            }
        }

        return diffSum;
    }


    public double canberraDistance(Object[] b, boolean hasSkips, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double[] newb = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            if (!(b[i] instanceof Number)) return 0.0D;

            newb[i] = ((Number) b[i]).doubleValue();
        }
        return canberraDistance(newb, hasSkips, blankValue);
    }

    public double canberraDistance(double[] b, boolean hasSkips, Double blankValue) {
        if ((hasSkips) || (hasBlanks)) {
            return canberraDistanceWithSkips(b, blankValue);
        }
        return canberraDistanceStandard(b);
    }

    public double canberraDistanceStandard(double[] b) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double distance = 0.0D;
        double denom = 0.0D;
        for (int i = 0; i < a.length; i++) {
            denom = Math.abs(a[i] + b[i]);
            if (denom != 0.0D) distance += Math.abs(a[i] - b[i]) / denom;
        }
        return distance;
    }

    public double canberraDistanceWithSkips(double[] b, Double blankValue) {
        if ((b == null) || (a == null)) return 0.0D;
        if (b.length != a.length) {
            System.out.println("Original: " + a.length + " elements.  New: " + b.length + " elements.");
            return 0.0D;
        }
        double distance = 0.0D;
        double nextA = 0.0D;
        double nextB = 0.0D;
        double denom = 0.0D;
        for (int i = 0; i < a.length; i++) {
            nextA = a[i];
            nextB = b[i];
            if (excludes != null && excludes[i]) {
                nextA = Double.NaN;
                nextB = Double.NaN;
            } else {
                if ((Double.isNaN(nextA)) && (blankValue != null)) {
                    nextA = blankValue.doubleValue();
                }
                if ((Double.isNaN(nextB)) && (blankValue != null)) {
                    nextB = blankValue.doubleValue();
                }
            }
            if ((!Double.isNaN(nextA)) && (!Double.isNaN(nextB))) {
                denom = Math.abs(nextA + nextB);
                if (denom != 0.0D) distance += Math.abs(nextA - nextB) / denom;
            }
        }
        return distance;
    }
}
