package edu.iastate.metnet.metaomgraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomGeneratorFactory;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * Compute mutual information between two data vectors using gaussian density
 * 
 * @author mrbai
 *
 */
public class MutualInformation {

	private double[] vecX;
	private double[] vecY;
	private double h;
	private int sizeM;
	// used for bspline calc
	private static double[] knotVector;
	private static double[][] basis1Degree;
	private static int numBins;
	private static int order;
	private static double toler = 0.0000001;

	public MutualInformation(double[] x, double[] y, double h) {
		this.vecX = x;
		this.vecY = y;
		this.h = h;
		this.sizeM = x.length;
		if (y.length != sizeM) {
			JOptionPane.showMessageDialog(null, "The two data series should be same length");
			return;
		}
	}

	public double returnMI() {
		return returnMI(this.vecX, this.vecY, this.h);
	}

	public double returnMI(double[] vecX, double[] vecY, double h) {
		double res = 0;
		// System.out.println("X:" + Arrays.toString(vecX));
		// System.out.println("Y:" + Arrays.toString(vecY));
		// formula reference: Fast calculation of pairwise mutual information for gene
		// regulatory network reconstruction.,
		// http://pengqiu.gatech.edu/pdf/FastPairMI_revision.pdf
		double constH = -1 / (2 * h * h);
		double outerSum = 0;
		// first loop
		for (int i = 0; i < sizeM; i++) {
			// second loop
			double xi = vecX[i];
			double yi = vecY[i];
			double num1 = 0;
			double den1 = 0;
			double den2 = 0;
			for (int j = 0; j < sizeM; j++) {
				double xj = vecX[j];
				double yj = vecY[j];
				double a = (xi - xj) * (xi - xj);
				double b = (yi - yj) * (yi - yj);
				num1 += Math.exp(constH * (a + b));
				den1 += Math.exp(constH * a);
				den2 += Math.exp(constH * b);
			}
			double inLogTerm = (sizeM * num1) / (den1 * den2);
			outerSum += Math.log(inLogTerm);

		}

		// divide by size
		res = outerSum / sizeM;
		return res;
	}

	public static double[] generateRandGaussArray(int n) {
		double[] res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = Math.random();
			RandomGenerator rg = RandomGeneratorFactory.createRandomGenerator(new Random());
			res[i] = rg.nextGaussian();
		}
		return res;
	}

	public static double[] generateRandUnifArray(int n) {
		double[] res = new double[n];
		for (int i = 0; i < n; i++) {
			res[i] = Math.random();
		}
		return res;
	}

	public static void writetoFile(String filename, double[] x) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < x.length; i++) {
			// Maybe:
			outputWriter.write(x[i] + "");
			// Or:
			// outputWriter.write(Integer.toString(x[i]);
			outputWriter.newLine();
		}
		outputWriter.flush();
		outputWriter.close();
	}

	private static double getTi(int i, int k, int M) {
		if (i < k)
			return 0;
		if (k <= i && i <= M - 1)
			return i = k + 1;
		return M - 1 - k + 2;
	}

	private static double getBi(double z, int i, int k, int M) {
		if (k == 1) {
			if (getTi(i, k, M) <= z && z < getTi(i + 1, k, M)) {
				return 1;
			} else {
				return 0;
			}
		} else {
			return (getBi(z, i, k - 1, M) * ((z - getTi(i, k, M)) / (getTi(i + k - 1, k, M) - getTi(i, k, M))))
					+ (getBi(z, i + 1, k - 1, M)
							* ((getTi(i + k, k, M) - z) / (getTi(i + k, k, M) - getTi(i + 1, k, M))));
		}
	}

	private static double[] getTvector(int k, int binsM) {
		if (k >= binsM) {
			JOptionPane.showMessageDialog(null, "Please check the values. k should be less than M...");
		}
		double[] tVec = new double[binsM + k];

		for (int i = 0; i < tVec.length; i++) {
			if (i < k) {
				tVec[i] = 0;
			} else if (k <= i && i <= binsM) {
				tVec[i] = i - k + 1;
			} else {
				tVec[i] = binsM - 1 - k + 2;
			}
		}
		double[] tVecTemp = new double[binsM + k];
		for (int i = 1; i < tVec.length; i++) {
			tVecTemp[i-1]=tVec[i];
		}
		tVecTemp[tVecTemp.length-1]=tVecTemp[tVec.length-1];
		//return tVecTemp;
		return tVec;
	}

	// i will start from 0 till M+k-1
	private static double getBasisi1(int i, double z, double[] t) {
		double res = 0;
		if (t[i] <= z && z < t[i + 1]) {
			res = 1;
		}
		return res;
	}

	private static double[][] computeBasis1(double[] z) {
		double[][] res = null;
		// create a matrix Mxu
		res = new double[numBins][z.length];
		// computing from B1 till BM so index for knot vector will start from 1
		// B1,k is treated as B0,k so match t[0] and [0+1]
		for (int j = 0; j < numBins; j++) {
			for (int i = 0; i < z.length; i++) {
				if (knotVector[j] <= z[i] && z[i] < knotVector[j + 1]) {
					res[j][i] = 1;
				} else {
					if (z[i] == numBins - order + 1 && z[i] <= knotVector[j + 1]) {
						// special case when z[i] is equal to maximum
						res[j][i] = 1;
					} else
						res[j][i] = 0;
				}
			}
		}
		return res;
	}

	private static double[][] computeBasis1_new(double[] z) {
		double[][] res = null;
		// create a matrix Mxu
		res = new double[numBins][z.length];
		// computing from B1 till BM so index for knot vector will start from 1
		// B1,k is treated as B0,k so match t[0] and [0+1]
		for (int j = 0; j < numBins; j++) {
			for (int i = 0; i < z.length; i++) {
				if (Math.abs(z[i] - (numBins - order + 1)) <= toler) {
					// special case when z[i] is equal to maximum
					res[numBins - 1][i] = 1;
				} else if (knotVector[j] <= z[i] && z[i] < knotVector[j + 1]) {
					res[j][i] = 1;
				} else {

					res[j][i] = 0;
				}
			}
		}
		return res;
	}

	private static double[][] computeWC(double[] z) {
		double[][] res = null;
		// create a matrix Mxu
		res = new double[numBins][z.length];

		// do for 1 z
		for (int zInd = 0; zInd < z.length; zInd++) {
			double thisz = z[zInd];
			// compute B1,k ... BM,k using dynamic pro approach
			double[][] thisRes = new double[order][numBins];
			// the top row of thisRes matrix will have B1,k ... BM,k. each lower row will
			// have B1,k-1 ... BM,k-1 etc till B1,1 ... BM,1
			// set last row of thisRes to B1,1 ... BM,1 for the given z
			for (int i = 0; i < numBins; i++) {
				thisRes[order - 1][i] = basis1Degree[i][zInd];
			}
			// start building thisRes from second last row
			for (int j = order - 2; j >= 0; j--) {
				for (int i = 0; i < numBins; i++) {

					// ith col means i+1th bin
					/**
					 * t_m=tm t_m+k-1=tmpkm1 t_m+k=tmpk t_m+1=tmp1
					 */
					double tm = knotVector[i];
					double tmp1 = knotVector[i + 1];
					double tmpkm1 = knotVector[i + order - 1];
					double tmpk = knotVector[i + order];
					double k1 = (thisz - tm) / (tmpkm1 - tm);
					double k2 = (tmpk - thisz) / (tmpk - tmp1);
					if (Double.isNaN(k2) || Double.isInfinite(k2)) {
						k2 = 0;
					}
					if (Double.isNaN(k1) || Double.isInfinite(k1)) {
						k1 = 0;
					}
					// System.out.println("k1:"+k1+" k2:"+k2);
					if (i + 1 < numBins) {
						thisRes[j][i] = (k1 * thisRes[j + 1][i]) + (k2 * thisRes[j + 1][i + 1]);
					} else {
						// for m>M Bm,k is zero
						thisRes[j][i] = k1 * thisRes[j + 1][i] + 0;
					}
				}
			}
			// System.out.println("thisRes " + Arrays.deepToString(thisRes));

			// add to main result matrix
			for (int m = 0; m < numBins; m++) {
				res[m][zInd] = thisRes[0][m];
			}
		}
		// System.out.println("FinalRes " + Arrays.deepToString(res));
		return res;
	}

	public static double getEntropy(double[][] mat) {
		double res = 0;
		int numCols = mat[0].length;
		double[] rowSums = new double[mat.length];
		for (int i = 0; i < rowSums.length; i++) {
			for (int j = 0; j < numCols; j++) {
				rowSums[i] += mat[i][j];
			}
		}
		// System.out.println(Arrays.toString(rowSums));

		// get entropy
		// System.out.println("H rowsums:" + Arrays.toString(rowSums));
		double sum = 0;
		for (int i = 0; i < rowSums.length; i++) {
			rowSums[i] = rowSums[i] / numCols;
			double thisPi = rowSums[i];
			if (thisPi == 0) {
				thisPi += 0.00001;
			}
			res += thisPi * (Math.log(thisPi) / Math.log(2));
			sum += thisPi;
		}
		res = -1 * res;
		// System.out.println("H rowPi:" + Arrays.toString(rowSums) + "ncols:" + numCols
		// + "sum:" + sum);
		return res;
	}

	private static double getJointEntropy(double[][] matX, double[][] matY) {
		double res = 0;
		int mX = matX.length; // num bins in X
		int mY = matX.length; // num bins in X
		int numCols = matX[0].length;
		double[] rowSums = new double[mX * mY];
		int rsInd = 0;
		for (int j = 0; j < mX; j++) {
			for (int i = 0; i < mY; i++) {
				for (int k = 0; k < numCols; k++) {
					rowSums[rsInd] += matX[j][k] * matY[i][k];
				}
				rsInd++;
			}

		}
		double sum = 0;
		for (int i = 0; i < rowSums.length; i++) {
			rowSums[i] = rowSums[i] / numCols;
			double thisPi = rowSums[i];
			if (thisPi == 0) {
				thisPi += 0.00001;
			}
			res += thisPi * (Math.log(thisPi) / Math.log(2));
			sum += thisPi;
		}
		res = -1 * res;
		// System.out.println("JH rowsums:" + Arrays.toString(rowSums) + "ncols:" +
		// numCols + "sum:" + sum);

		return res;
	}

	public static void main(String[] args) throws IOException {
		double[] v1 = { 2, 6, 8, 11, 20 };
		double[] v2 = { 2, 0, 8, 0, 0 };
		MutualInformation ob = new MutualInformation(v1, v2, 0.01);
		MutualInformation ob2 = new MutualInformation(v1, v1, 0.01);
		//System.out.println("MI:" + ob.returnMI() + ":" + ob2.returnMI());

		MutualInformation obj = new MutualInformation(v1, v2, 0.01);
		double[] x = generateRandGaussArray(10000);
		//System.out.println("MIx:" + ob.returnMI(x, x, 0.1));
		double[] x1 = generateRandGaussArray(100);
		double[] x2 = generateRandUnifArray(100);
		double[] x1sq = new double[x1.length];
		int k = 0;
		for (double d : x1) {
			x1sq[k++] = d * d;
		}
		// x2=x1sq;
		// writetoFile("C:/Users/mrbai/Documents/x1.txt", x1);
		// writetoFile("C:/Users/mrbai/Documents/x2.txt", x2);
		//System.out.println("MI:" + ob.returnMI(x1, x2, 0.0001));

		// MI using bspline
		int M = 3;
		k = 2;
		//v1 = new double[] { 0, 0.2, 0.4, 0.6, 0.8, 1 };
		//v2 = new double[] { 0.8, 1, 0.6, 0.4, 0.0, 0.2 };
		
		v1 = new double[] {  0.4, 0.6, 0.8, 1,0,0.2 };
		v2 = new double[] {  0.6, 0.4, 0.0, 0.2,0.8,1 };
		
		
		//v1 = new double[] { 0.2, 0.4,0.8 };
		//v2 = new double[] { 0.4,0.2,0.8 };
		//v2 = new double[] { 0, 0, 0, 0.0, 0.0, 0 };
		// v1 = new double[] { 0, 0, 0.9, 0, 0 };
		double[] z = new double[v1.length];
		double min = 0;
		double max = 1;
		// trasnform x to z
		for (int i = 0; i < z.length; i++) {
			z[i] = 2 * v1[i];
		}
		// start computing b spline
		M =4;
		k =3;
		numBins = M;
		order = k;
		knotVector=getTvector(k,M);
		System.out.println("KV:"+Arrays.toString(knotVector)); //knotvec is same as in c code
		ComputeDensityFromSpline o1= new ComputeDensityFromSpline(v1, M, k, knotVector);
		double hX=o1.getEntropy();
		System.out.println("hX:"+hX);
		
		ComputeDensityFromSpline o2= new ComputeDensityFromSpline(v2, M, k, knotVector);
		double hY=o2.getEntropy();
		System.out.println("hY:"+hY);
		
		double jH=o1.getJointEntropy(o2);
		System.out.println("jH:"+jH);
		
		System.out.println("MI:"+(hX+hY-jH));
		
		
		double jH2=o1.getJointEntropy(o1);
		System.out.println("jH2:"+jH2);
		
		System.out.println("MI2:"+(hX+hX-jH2));
		
		//
		System.out.println("Basis1X:"+Arrays.deepToString(o1.getBasis1()));
		System.out.println("Basis1Y:"+Arrays.deepToString(o2.getBasis1()));
		System.out.println("WCX:"+Arrays.deepToString(o1.getWeightMatrix()));
		System.out.println("WCY:"+Arrays.deepToString(o2.getWeightMatrix()));
		
	
		
		/*double[] v3 = new double[] { 1, 0.8, 0.4, 0.6, 0.0, 0.2 };
		// compute MI bw v1 and v3
		System.out.println("********************************");
		// trasnform x to z
		for (int i = 0; i < z.length; i++) {
			z[i] = 2 * v3[i];
		}
		knotVector = getTvector(k, M);
		basis1Degree = computeBasis1(z);

		// t=new double[] {0,1,2,3};
		System.out.println("tvec:" + Arrays.toString(knotVector));
		System.out.println(Arrays.deepToString(basis1Degree));
		 wtMatY = computeWC(z);

		System.out.println("wtY:" + Arrays.deepToString(wtMatY));

		System.out.println("Entropy Y:" + getEntropy(wtMatY));

		jH = getJointEntropy(wtMatX, wtMatY);
		System.out.println("JH:" + jH);

		mI = getEntropy(wtMatX) + getEntropy(wtMatY) - jH;
		System.out.println("MI:" + mI);

		o = new ComputeDensityFromSpline(z, M, k, knotVector);
		System.out.println("ret wtMat" + Arrays.deepToString(o.getWeightMatrix()));
		*/
	}
}
