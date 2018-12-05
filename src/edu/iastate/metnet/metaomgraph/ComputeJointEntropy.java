package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComputeJointEntropy {
	private List<double[][]> wtMatlist;
	private double[][] target;
	private int threads;

	public ComputeJointEntropy(List<double[][]> wtMatlist, double[][] target, int t) {
		this.wtMatlist = wtMatlist;
		this.target = target;
		threads = t;
	}

	public List<Double> getJointEntropy() throws InterruptedException, ExecutionException {
		List<Double> res = new ArrayList<>();

		int size = wtMatlist.size() / threads;

		// split list into t parts and execute in parallel
		List<JointEntropy> taskList = new ArrayList<>();
		for (int start = 0; start < wtMatlist.size(); start += size) {
			int end = Math.min(start + size, wtMatlist.size());
			List<double[][]> sublist = wtMatlist.subList(start, end);
			taskList.add(new JointEntropy(sublist, target));
		}

		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(size);
		List<Future<double[]>> allRes = pool.invokeAll(taskList);
		for (Future<double[]> re : allRes) {
			double[] temp = re.get();
			for (int i = 0; i < temp.length; i++) {
				res.add(temp[i]);
			}

		}
		// important to shutdown
		pool.shutdown();
		return res;
	}
}

// class to calculate joint entopy given wt matrixlist and target wtmatrix
class JointEntropy implements Callable<double[]> {
	List<double[][]> wtMatlist;
	double[][] target;
	private double toler = 1E-10;

	public JointEntropy(List<double[][]> wtMatlist, double[][] target) {
		this.wtMatlist = wtMatlist;
		this.target = target;
	}

	@Override
	public double[] call() throws Exception {
		// TODO Auto-generated method stub
		double[] res = new double[wtMatlist.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = getJointEntropy(wtMatlist.get(i), target);
		}

		return res;
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

}