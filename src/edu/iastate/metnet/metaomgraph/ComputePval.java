package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

/**
 * Class to compute a p-value for correlation measure data is a row against
 * which we want p-value. shufflist is the list of permuted target row c value
 * is the original correlation coeff as computed between data and target rows.
 * 
 * 
 * @author urmi
 *
 */
public class ComputePval {

	private double[] data;
	private List<double[]> shuffList;
	private double cvalue;
	private int threads;

	public ComputePval(double[] data, List<double[]> shuffList, int t) {
		this.data = data;
		this.shuffList = shuffList;
		// this.cvalue=cval;
		this.threads = t;
	}

	/**
	 * split the permuted list into multiple and get a correlation coeff for each
	 * executing in parallel.
	 * 
	 * @author urmi
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public double[] doComputation() throws InterruptedException, ExecutionException {
		double[] finalRes = null;
		int size = shuffList.size() / threads;

		List<ComputeP> taskList = new ArrayList<>();
		for (int start = 0; start < shuffList.size(); start += size) {
			int end = Math.min(start + size, shuffList.size());
			List<double[]> sublist = shuffList.subList(start, end);
			taskList.add(new ComputeP(data, sublist));
		}

		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(size);

		List<Future<double[]>> allRes = pool.invokeAll(taskList);

		finalRes = new double[shuffList.size()];
		int index = 0;
		for (Future<double[]> re : allRes) {
			double[] temp = re.get();
			for (int i = 0; i < temp.length; i++) {
				finalRes[index++] = temp[i];
			}

		}
		// return the p value
		// important to shutdown
		pool.shutdown();
		double[] toReturn = new double[2];
		toReturn[0] = finalRes[0]; // correlation value
		toReturn[1] = getPvalue(finalRes); // p value
		return toReturn;

	}

	public double[] doComputationSpearman() throws InterruptedException, ExecutionException {
		double[] finalRes = null;
		int size = shuffList.size() / threads;

		List<ComputeSP> taskList = new ArrayList<>();
		for (int start = 0; start < shuffList.size(); start += size) {
			int end = Math.min(start + size, shuffList.size());
			List<double[]> sublist = shuffList.subList(start, end);
			taskList.add(new ComputeSP(data, sublist));
		}

		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(size);

		List<Future<double[]>> allRes = pool.invokeAll(taskList);

		finalRes = new double[shuffList.size()];
		int index = 0;
		for (Future<double[]> re : allRes) {
			double[] temp = re.get();
			for (int i = 0; i < temp.length; i++) {
				finalRes[index++] = temp[i];
			}

		}
		// return the p value
		// important to shutdown
		pool.shutdown();
		double[] toReturn = new double[2];
		toReturn[0] = finalRes[0]; // correlation value
		toReturn[1] = getPvalue(finalRes); // p value
		return toReturn;

	}

	public double getPvalue(double[] permC) {
		double pval = 0;
		int count = 0;
		// first value is correlation against original data
		double obsC = permC[0];
		 //JOptionPane.showMessageDialog(null, "obs C:"+obsC);
		 //JOptionPane.showMessageDialog(null, "all C:"+Arrays.toString(permC));
		for (int t = 0; t < permC.length; t++) {
			double tempVal = permC[t];
			if (tempVal < 0)
				tempVal = tempVal * -1;
			if (obsC < 0)
				obsC = obsC * -1;
			if (tempVal >= obsC) {
				count++;
			}
		}
		pval = (double) count / permC.length;

		return pval;
	}
}

/**
 * Class to compute pearson correlation
 * 
 * @author urmi
 *
 */
class ComputeP implements Callable<double[]> {
	double[] target;
	List<double[]> data;

	public ComputeP(double[] a, List<double[]> b) {
		this.target = a;
		this.data = b;
	}

	@Override
	public double[] call() throws Exception {
		// TODO Auto-generated method stub
		double[] res = new double[data.size()];
		PearsonsCorrelation pc = new PearsonsCorrelation();

		for (int d = 0; d < data.size(); d++) {
			double thisVal = 0.0D + pc.correlation(target, data.get(d));
			if (Double.isNaN(thisVal)) {
				res[d] = 0.0D;
			} else {
				res[d] = thisVal;
			}
		}

		return res;
	}

}

/**
 * Class to compute spearman correlation
 * 
 * @author urmi
 *
 */
class ComputeSP implements Callable<double[]> {
	double[] target;
	List<double[]> data;

	public ComputeSP(double[] a, List<double[]> b) {
		this.target = a;
		this.data = b;
	}

	@Override
	public double[] call() throws Exception {
		// TODO Auto-generated method stub
		double[] res = new double[data.size()];
		SpearmansCorrelation spc = new SpearmansCorrelation();

		for (int d = 0; d < data.size(); d++) {
			double thisVal = 0.0D;
			try {
				thisVal = 0.0D + spc.correlation(target, data.get(d));
			} catch (NotANumberException nane) {
				thisVal = 0.0D;
			}

			if (Double.isNaN(thisVal)) {
				res[d] = 0.0D;
			} else {
				// res[d] = spc.correlation(target, data.get(d));
				res[d] = thisVal;
			}
		}

		return res;
	}

}