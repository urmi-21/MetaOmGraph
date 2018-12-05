package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JOptionPane;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import javafx.util.Pair;

public class ComputeRunsSimilarity {

	/**
	 * similarity type: 1 cosine distance 2 pearson correlation etc.
	 */

	private int _P = 6;
	private int type;
	private HashMap<Integer, double[]> databyCols;

	public ComputeRunsSimilarity(int t, HashMap<Integer, double[]> databyCols) {
		this.type = t;
		this.databyCols = databyCols;

	}

	public HashMap<String, Double> doComputation() throws InterruptedException, ExecutionException {
		return doComputation(this.type);
	}

	public HashMap<String, Double> doComputation(int type) throws InterruptedException, ExecutionException {
		// do type of computation in parallel
		// create task list based on type of similarity
		Integer[] keys = databyCols.keySet().toArray(new Integer[0]);
		List<Future<Double>> allRes = null;
		// create new pool
		ExecutorService pool = null;
		List<String> resNames = new ArrayList<>();
		if (type == 1) {
			List<ComputeCosSim> taskList = new ArrayList<>();
			// create all the tasks
			for (int i = 0; i < keys.length - 1; i++) {
				for (int j = i + 1; j < keys.length; j++) {
					double[] vecA = databyCols.get(keys[i]);
					double[] vecB = databyCols.get(keys[j]);
					taskList.add(new ComputeCosSim(vecA, vecB));
					String nameA = MetaOmGraph.getActiveProject().getDataColumnHeader(keys[i]);
					String nameB = MetaOmGraph.getActiveProject().getDataColumnHeader(keys[j]);
					resNames.add(nameA + ":" + nameB);
				}
			}

			// execute task
			pool = Executors.newFixedThreadPool(_P);
			allRes = pool.invokeAll(taskList);

		} else if (type == 2) {
			List<ComputeCorr> taskList = new ArrayList<>();
			// create all the tasks
			for (int i = 0; i < keys.length - 1; i++) {
				for (int j = i + 1; j < keys.length; j++) {
					double[] vecA = databyCols.get(keys[i]);
					double[] vecB = databyCols.get(keys[j]);
					taskList.add(new ComputeCorr(vecA, vecB));
					String nameA = MetaOmGraph.getActiveProject().getDataColumnHeader(keys[i]);
					String nameB = MetaOmGraph.getActiveProject().getDataColumnHeader(keys[j]);
					resNames.add(nameA + ":" + nameB);
				}
			}

			// execute task
			pool = Executors.newFixedThreadPool(_P);
			allRes = pool.invokeAll(taskList);
		}

		// process results
		HashMap<String, Double> resultsCombined = new HashMap<>();
		List<Double> results = new ArrayList<>();
		// JOptionPane.showMessageDialog(null, "names:" + resNames.toString());
		int i = 0;
		for (Future<Double> re : allRes) {
			double temp = re.get();
			results.add(temp);
			// JOptionPane.showMessageDialog(null, "temp:"+resNames.get(i)+":" + temp);
			resultsCombined.put(resNames.get(i), temp);
			i++;
		}

		return resultsCombined;
	}

	/**
	 * Function to convert results with names to a pair
	 * 
	 * @param results
	 * @param resNames
	 * @return
	 */
	public Pair<String[], Double[]> getMatrixData(List<Double> results, List<String> resNames) {

		return null;
	}
}

/**
 * Class to compute cosine similarity
 * 
 * @author mrbai
 *
 */
class ComputeCosSim implements Callable<Double> {
	double[] target;
	double[] data;

	public ComputeCosSim(double[] a, double[] b) {
		this.target = a;
		this.data = b;
	}

	@Override
	public Double call() throws Exception {
		// TODO Auto-generated method stub
		double res = 0;
		// handle if dimention is less than 1
		if (target.length <= 1) {
			return 0.0;
		}
		// compute cosine similarity
		double dProd = 0;
		double normA = 0;
		double normB = 0;
		for (int i = 0; i < target.length; i++) {
			dProd += target[i] * data[i];
			normA += Math.pow(target[i], 2);
			normB += Math.pow(data[i], 2);
		}
		res = (dProd / (Math.sqrt(normA) * Math.sqrt(normB)));

		if (Double.isNaN(res)) {
			return 0.0;
		}
		return res;
	}

}

/**
 * Class to compute pearson correlation bw two double arrays
 * 
 * @author mrbai
 *
 */
class ComputePearsonCorr implements Callable<Double> {
	double[] target;
	double[] data;

	public ComputePearsonCorr(double[] a, double[] b) {
		this.target = a;
		this.data = b;
	}

	@Override
	public Double call() throws Exception {
		// TODO Auto-generated method stub
		double res = 0;
		// handle if dimention is less than 1
		if (target.length <= 1) {
			return 0.0;
		}
		PearsonsCorrelation pc = new PearsonsCorrelation();
		res = pc.correlation(target, data);
		// JOptionPane.showMessageDialog(null, "thiscorr:"+res);
		if (Double.isNaN(res)) {
			return 0.0;
		}
		return res;
	}

}