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

import org.apache.commons.math3.analysis.function.Atanh;
import org.apache.commons.math3.analysis.function.Tanh;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

public class CorrelationGrouped {

	private List<double[]> dataGrouped;
	private List<double[]> sourceGrouped;
	private int _P;

	public CorrelationGrouped(List<double[]> sourceGrouped, List<double[]> dataGrouped, int p) {
		this.dataGrouped = dataGrouped;
		this.sourceGrouped = sourceGrouped;
		this._P = p;
	}

	public CorrelationMeta doComputation(boolean randModel) throws InterruptedException, ExecutionException {
		CorrelationMeta toReturn = new CorrelationMeta();
		double[] finalRes = null;
		int size = dataGrouped.size() / _P;

		List<ComputeCorr> taskList = new ArrayList<>();
		for (int i = 0; i < dataGrouped.size(); i++) {
			taskList.add(new ComputeCorr(dataGrouped.get(i), sourceGrouped.get(i)));
		}

		// create new pool
		ExecutorService pool = Executors.newFixedThreadPool(_P);

		// JOptionPane.showMessageDialog(null, "calling");
		List<Future<Double>> allRes = pool.invokeAll(taskList);

		finalRes = new double[dataGrouped.size()];
		int index = 0;
		for (Future<Double> re : allRes) {
			double temp = re.get();
			// JOptionPane.showMessageDialog(null, "temp"+temp);
			finalRes[index++] = temp;
		}

		// JOptionPane.showMessageDialog(null, "r for groups"+
		// Arrays.toString(finalRes));
		// return the p value
		// important to shutdown
		pool.shutdown();

		int[] gSize = new int[sourceGrouped.size()];
		for (int i = 0; i < gSize.length; i++) {
			gSize[i] = sourceGrouped.get(i).length;
		}

		double rval = 0;
		if (randModel) {
			toReturn = getrvalueRandModel(finalRes, gSize);
		} else {
			toReturn = getrvalueFEModel(finalRes, gSize);
		}
		return toReturn;
	}

	/**
	 * meta analysis of correlations using fixed effect model
	 * 
	 * @param corrvals
	 * @param groupSizes
	 * @return
	 */
	private static CorrelationMeta getrvalueFEModel(double[] corrvals, int[] groupSizes) {
		double[] z_r = new double[corrvals.length];
		int sumW = 0;
		double sumWZ = 0;
		double pooledzr = 0;
		double pooledr = 0;
		double stdErr = 0;
		double zStatistic = 0;
		double pvalzr = 0;
		double pooledrCIL = 0; // for CI lower bound
		double pooledrCIH = 0; // for CI higher bound
		double alpha = 0.05;
		double qStatistic = 0;
		double pvalQ = 0;

		Atanh atanh = new Atanh();
		Tanh tanh = new Tanh();
		for (int i = 0; i < z_r.length; i++) {
			z_r[i] = atanh.value(corrvals[i]);
		}

		for (int i = 0; i < groupSizes.length; i++) {
			sumW += groupSizes[i] - 3;
			sumWZ += z_r[i] * (groupSizes[i] - 3);
		}

		pooledzr = sumWZ / sumW;
		pooledr = tanh.value(pooledzr);
		stdErr = Math.sqrt(1.0 / sumW);
		// System.out.println("SE:" + stdErr);
		zStatistic = pooledzr / stdErr;
		System.out.println("zStatistic:" + zStatistic);
		NormalDistribution stdNorm = new NormalDistribution();

		if (zStatistic >= 0) {
			pvalzr = 2 * (1 - stdNorm.cumulativeProbability(zStatistic));
		} else {
			pvalzr = 2 * (stdNorm.cumulativeProbability(zStatistic));
		}

		// System.out.println("pval:" + pvalzr);

		// System.out.println("c val 2 tail:" +
		// stdNorm.inverseCumulativeProbability(alpha / 2.0));
		pooledrCIL = pooledzr + (stdNorm.inverseCumulativeProbability(alpha / 2.0) * stdErr);
		pooledrCIH = pooledzr - (stdNorm.inverseCumulativeProbability(alpha / 2.0) * stdErr);
		if (pooledrCIL > pooledrCIH) {
			double temp = pooledrCIH;
			pooledrCIH = pooledrCIL;
			pooledrCIL = temp;
		}
		System.out.println("95% CI:" + tanh.value(pooledrCIL) + "-" + tanh.value(pooledrCIH));

		for (int i = 0; i < groupSizes.length; i++) {
			qStatistic += (groupSizes[i] - 3) * (z_r[i] - pooledzr) * (z_r[i] - pooledzr);
		}

		ChiSquaredDistribution chisqdist = new ChiSquaredDistribution(groupSizes.length - 1);
		pvalQ = 1 - chisqdist.cumulativeProbability(qStatistic);
		System.out.println("Q stat:" + qStatistic + " pval q:" + pvalQ);
		if (Double.isNaN(pooledr)) {
			pooledr = 0;
		}
		CorrelationMeta toReturn = new CorrelationMeta(pooledr, pvalzr, zStatistic, qStatistic, pooledzr, stdErr);

		return toReturn;

	}

	/**
	 * meta analysis of correlations using random effect model
	 * 
	 * @param corrvals
	 * @param groupSizes
	 * @return
	 */

	private static CorrelationMeta getrvalueRandModel(double[] corrvals, int[] groupSizes) {
		int k = groupSizes.length;
		double[] z_r = new double[corrvals.length];
		int sumW = 0;
		double sumWZ = 0;
		double pooledzr = 0;
		double pooledr = 0;
		double stdErr = 0;
		double zStatistic = 0;
		double pvalzr = 0;
		double pooledrCIL = 0; // for CI lower bound
		double pooledrCIH = 0; // for CI higher bound
		double alpha = 0.05;
		double qStatistic = 0;
		double pvalQ = 0;
		// Extra vars for this model
		double tau = 0;
		double denomC = 0;
		double sumSqW = 0;
		double pooledzr_rand = 0;
		double sumWstarZr = 0;
		double sumWstar = 0;

		Atanh atanh = new Atanh();
		Tanh tanh = new Tanh();
		for (int i = 0; i < z_r.length; i++) {
			z_r[i] = atanh.value(corrvals[i]);
		}

		for (int i = 0; i < groupSizes.length; i++) {
			sumW += (groupSizes[i] - 3);
			sumWZ += z_r[i] * (groupSizes[i] - 3);
			sumSqW += (groupSizes[i] - 3) * (groupSizes[i] - 3);
		}

		pooledzr = sumWZ / sumW;
		pooledr = tanh.value(pooledzr);
		stdErr = Math.sqrt(1.0 / sumW);
		zStatistic = pooledzr / stdErr;
		NormalDistribution stdNorm = new NormalDistribution();
		for (int i = 0; i < groupSizes.length; i++) {
			qStatistic += (groupSizes[i] - 3) * (z_r[i] - pooledzr) * (z_r[i] - pooledzr);
		}
		ChiSquaredDistribution chisqdist = new ChiSquaredDistribution(groupSizes.length - 1);
		pvalQ = 1 - chisqdist.cumulativeProbability(qStatistic);
		System.out.println("Q stat:" + qStatistic + " pval q:" + pvalQ);

		denomC = sumW - (sumSqW / sumW);
		tau = (qStatistic - (k - 1)) / denomC;
		if (tau < 0) {
			tau = 0;
		}
		System.out.println("tau:" + tau);
		for (int i = 0; i < groupSizes.length; i++) {
			double thisW = 1.0 / ((1.0 / (groupSizes[i] - 3)) + tau);
			// System.out.println("thisW:"+thisW);
			sumWstar += thisW;
			sumWstarZr += z_r[i] * thisW;
		}

		// System.out.println("SumWstr:" + sumWstar);
		pooledzr_rand = sumWstarZr / sumWstar;
		pooledr = tanh.value(pooledzr_rand);
		stdErr = Math.sqrt(1.0 / sumWstar);
		// System.out.println("SE:" + stdErr);
		zStatistic = pooledzr_rand / stdErr;
		System.out.println("zStatistic:" + zStatistic);

		if (zStatistic >= 0) {
			pvalzr = 2 * (1 - stdNorm.cumulativeProbability(zStatistic));
		} else {
			pvalzr = 2 * (stdNorm.cumulativeProbability(zStatistic));
		}

		// System.out.println("pval:" + pvalzr);
		if (Double.isNaN(pooledr)) {
			pooledr = 0;
		}
		CorrelationMeta toReturn = new CorrelationMeta(pooledr, pvalQ, zStatistic, qStatistic, pooledzr_rand, stdErr);

		return toReturn;

	}

	public static void main(String[] args) {
		/*
		 * Atanh atanh = new Atanh(); System.out.println("atanh(0.5):" +
		 * atanh.value(0.5)); System.out.println("atanh(0.1):" + atanh.value(0.1));
		 * System.out.println("atanh(0.2):" + atanh.value(0.2));
		 * System.out.println("atanh(-0.5):" + atanh.value(-0.5));
		 */
		double[] corrvals = new double[] { 0.56, 0.43, 0.53, 0.51, 0.66, 0.46, 0.33, 0.38 };
		int[] groupSizes = new int[] { 133, 149, 131, 120, 111, 152, 60, 122 };
		corrvals = new double[] { 0.51, 0.48, 0.30, 0.21, 0.60, 0.46, 0.22, 0.25 };
		groupSizes = new int[] { 131, 129, 155, 121, 111, 119, 112, 145 };
		// corrvals = new double[] { 0.7026,0.494856,-0.655774568 };
		// groupSizes = new int[] { 4,7,7 };

		System.out.println("pooldr:" + getrvalueFEModel(corrvals, groupSizes).getrVal() + " pv:"
				+ getrvalueFEModel(corrvals, groupSizes).getpVal());
		System.out.println("..........");
		System.out.println("RAND pooldr:" + getrvalueRandModel(corrvals, groupSizes).getrVal() + " pv:"
				+ getrvalueRandModel(corrvals, groupSizes).getpVal());

	}

}

/**
 * Class to compute pearson correlation bw two double arrays
 * 
 * @author urmi
 *
 */
class ComputeCorr implements Callable<Double> {
	double[] target;
	double[] data;
	int method; // 1 pearson 2 spearman

	public ComputeCorr(double[] a, double[] b) {
		this(a, b, 1);
	}

	public ComputeCorr(double[] a, double[] b, int method) {
		this.target = a;
		this.data = b;
		this.method = method;
	}

	@Override
	public Double call() throws Exception {
		// TODO Auto-generated method stub
		double res = 0;
		// handle if dimention is less than 1
		if (target.length <= 1) {
			return 0.0;
		}
		if (method == 1) {
			PearsonsCorrelation pc = new PearsonsCorrelation();
			res = pc.correlation(target, data);
		} else if (method == 2) {
			SpearmansCorrelation sc = new SpearmansCorrelation();
			res = sc.correlation(target, data);
		}

		// JOptionPane.showMessageDialog(null, "thiscorr:"+res);
		if (Double.isNaN(res)) {
			return 0.0;
		}
		return res;
	}

}