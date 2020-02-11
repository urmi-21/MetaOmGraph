package edu.iastate.metnet.metaomgraph;

//estimate density using B spline
public class ComputeDensityFromSpline {
	private double[] knotVector;
	private double[][] basis1Degree;
	private final int numBins;
	private final int order;
	private double[] data;
	private double toler = 1E-10;
	private boolean allZeros;

	public ComputeDensityFromSpline(double[] x, int M, int k, double[] kvec) {
		numBins = M;
		order = k;
		knotVector = kvec;
		data = normalizeData(x);
		//System.out.println("thisdata:" + Arrays.toString(data));
	}

	private double[][] computeBasis(double[] z) {
		double[][] res = null;
		// create a matrix Mxu
		res = new double[numBins][z.length];
		// computing from B1 till BM so index for knot vector will start from 1
		// B1,k is treated as B0,k so match t[0] and [0+1]

		for (int i = 0; i < z.length; i++) {
			for (int j = 0; j < numBins; j++) {

				if (((knotVector[j] <= z[i]) && (z[i] < knotVector[j + 1]))
						|| (Math.abs(z[i] - knotVector[j + 1]) < toler && (j + 1 == numBins))) {
					res[j][i] = 1.0;
				} else {
					res[j][i] = 0.0;
				}

			}
		}
		return res;
	}

	public double[][] getBasis1() {
		return this.basis1Degree;
	}

	public double[] normalizeData(double[] x) {
		double[] z = new double[x.length];
		// normalize data to range 0-M-k+1
		double xmin = 100000000;
		double xmax = -100000000;
		double upperRange = numBins - order + 1;
		for (int i = 0; i < x.length; i++) {
			if (x[i] < xmin) {
				xmin = x[i];
			}
			if (x[i] > xmax) {
				xmax = x[i];
			}
		}
		double width = xmax - xmin;
		if (width == 0) {
			return z;
		}

		for (int i = 0; i < x.length; i++) {
			z[i] = (x[i] - xmin) * (upperRange / width);
		}

		if (width == 0) {
			// JOptionPane.showMessageDialog(null, "wid 0 "+Arrays.toString(z));
			this.allZeros = true;
		}

		return z;
	}

	

	/**
	 * Compute wt matrix
	 * 
	 * @return
	 */
	public double[][] computeWtMatrix() {
		double[][] wtMat = new double[numBins][data.length];
		for (int i = 0; i < data.length; i++) {
			// System.out.println(Arrays.deepToString(computeWC2forz(i)));
			double[][] temp = computeWCforz(i);
			for (int j = 0; j < numBins; j++) {
				// copy last column of temp into position in wtmat
				wtMat[j][i] = temp[j][order - 1];
			}
		}

		return wtMat;
	}

	private double[][] computeWCforz(int index) {
		double[][] res = new double[numBins][order];
		double thisz = data[index];
		// init first column to be same as basis for this z
		for (int i = 0; i < numBins; i++) {
			res[i][0] = basis1Degree[i][index];
		}

		for (int k = 1; k < order; k++) {
			for (int i = 0; i < numBins; i++) {
				// assume index of knotvector starts from 1, order and bins start from 1
				int newi = i + 1;
				int newk = k + 1;
				double d1 = knotVector[(newi + newk - 1) - 1] - knotVector[newi - 1];
				double d2 = knotVector[newi + newk - 1] - knotVector[(newi - 1) + 1];

				if (d1 == 0 && d2 == 0) {
					res[i][k] = 0.0;
				} else if (d1 == 0) {
					res[i][k] = (res[i + 1][k - 1] * (knotVector[newi + newk - 1] - thisz)) / d2;
				} else if (d2 == 0) {
					res[i][k] = (res[i][k - 1] * (thisz - knotVector[newi - 1])) / d1;
				} else {
					res[i][k] = ((res[i + 1][k - 1] * (knotVector[newi + newk - 1] - thisz)) / d2)
							+ ((res[i][k - 1] * (thisz - knotVector[newi - 1])) / d1);
				}

				if (res[i][k] < 0) {
					res[i][k] = 0;
				}

			}

		}

		return res;
	}

	/**
	 * compute weight matrix
	 * 
	 * @return
	 */
	public double[][] getWeightMatrix() {
		double res[][] = null;
		// this.basis1Degree = computeBasis1(data);
		// this.basis1Degree = computeBasis1_new(data);
		this.basis1Degree = computeBasis(data);
		// res = computeWC(data);
		res = computeWtMatrix();
		return res;
	}

	/**
	 * check if probability matrix sum to 1
	 * 
	 * @param mat
	 * @return
	 */
	public double checkWtMatrix(double[][] mat) {
		int numCols = mat[0].length;
		double[] rowSums = new double[mat.length];
		for (int i = 0; i < rowSums.length; i++) {
			for (int j = 0; j < numCols; j++) {
				rowSums[i] += mat[i][j];
			}
		}
		// get entropy
		double sum = 0;
		for (int i = 0; i < rowSums.length; i++) {
			rowSums[i] = rowSums[i] / numCols;
			double thisPi = rowSums[i];
			sum += thisPi;
		}
		return sum;
		/*
		 * if(Math.round(sum)==1) { return true; }else { return false; }
		 */
	}

	/**
	 * check if joint probability matrix sum to 1
	 * 
	 * @param matX
	 * @param matY
	 * @return
	 */
	public double checkJointWtMatrix(double[][] matX, double[][] matY) {
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

			sum += thisPi;
		}
		return sum;

	}

	/**
	 * given weight matrix calculate entropy
	 * 
	 * @param mat
	 * @return
	 */
	public double getEntropy(double[][] mat) {
		double res = 0;
		int numCols = mat[0].length;
		double[] rowSums = new double[mat.length];
		for (int i = 0; i < rowSums.length; i++) {
			for (int j = 0; j < numCols; j++) {
				rowSums[i] += mat[i][j];
			}
		}
		// get entropy
		for (int i = 0; i < rowSums.length; i++) {
			rowSums[i] = rowSums[i] / numCols;
			double thisPi = rowSums[i];

			/*
			 * if (thisPi == 0) { thisPi += 0.00001; }
			 */

			if (thisPi > toler) {
				res += thisPi * (Math.log(thisPi) / Math.log(2));
			}

		}
		res = -1 * res;

		return res;
	}

	// use default matrix of the object
	public double getEntropy() {

		return getEntropy(getWeightMatrix());
	}

	/**
	 * given two weight matrices calculate the joint entropy
	 * 
	 * @param matX
	 * @param matY
	 * @return
	 */
	public double getJointEntropy(double[][] matX, double[][] matY) {
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
		for (int i = 0; i < rowSums.length; i++) {
			rowSums[i] = rowSums[i] / numCols;
			double thisPi = rowSums[i];

			/*
			 * if (thisPi == 0) { thisPi += 0.00001; }
			 */
			if (thisPi > toler) {
				res += thisPi * (Math.log(thisPi) / Math.log(2));
			}

		}
		res = -1 * res;
		// System.out.println("JH rowsums:" + Arrays.toString(rowSums) + "ncols:" +
		// numCols + "sum:" + sum);

		return res;
	}

	// compute joint entropy of this obj with another
	// computes wt matrix again
	public double getJointEntropy(ComputeDensityFromSpline ob) {
		// JOptionPane.showMessageDialog(null,
		// "sumwt1:"+checkWtMatrix(getWeightMatrix()));
		// JOptionPane.showMessageDialog(null,
		// "sumwt2:"+checkWtMatrix(ob.getWeightMatrix()));
		if (this.allZeros) {
			return ob.getEntropy();
		}
		if (ob.allZeros) {
			return getEntropy();
		}
		return getJointEntropy(getWeightMatrix(), ob.getWeightMatrix());
	}

}
