package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class ComputeEntropy {

	private List<double[][]> wtMatlist;
	private int threads;

	public ComputeEntropy(List<double[][]> wtMat, int t) {
		wtMatlist = wtMat;
		threads = t;
	}

	public List<Double> getEntropy() throws InterruptedException, ExecutionException {
		List<Double> res = null;

		int size = wtMatlist.size() / threads;

		// split list into t parts and execute in parallel
		List<Entropy> taskList = new ArrayList<>();
		for (int start = 0; start < wtMatlist.size(); start += size) {
			int end = Math.min(start + size, wtMatlist.size());
			List<double[][]> sublist = wtMatlist.subList(start, end);
			taskList.add(new Entropy(sublist));
		}
		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(size);
		List<Future<double[]>> allRes = pool.invokeAll(taskList);

		res = new ArrayList<>();

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

// class to calculate entopy given wt matrix
class Entropy implements Callable<double[]> {
	List<double[][]> wtMatlist;
	private double toler = 1E-10;
	public Entropy(List<double[][]> wtMatlist) {
		this.wtMatlist = wtMatlist;
	}

	@Override
	public double[] call() throws Exception {
		// TODO Auto-generated method stub
		double[] res = new double[wtMatlist.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = getEntropy(wtMatlist.get(i));
		}

		return res;
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

}
