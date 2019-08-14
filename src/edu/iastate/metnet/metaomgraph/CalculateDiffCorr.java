package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.math3.analysis.function.Atanh;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.biomage.Array.Array;

import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;

public class CalculateDiffCorr {

	private String geneList;
	private String featureName;
	private int featureIndex;
	// group indices
	Collection<Integer> grp1Ind;
	Collection<Integer> grp2Ind;
	private String g1name;
	private String g2name;
	private MetaOmProject myProject;
	private int method; // 0 paramentric fisher test 1 permutation with FT 2 perm without transform

	private List<Double> corrGrp1;
	private List<Double> corrGrp2;
	private List<Double> zVals1;
	private List<Double> zVals2;
	private List<Double> diffZvals;
	private List<Double> zScores;
	private List<Double> pValues;

	private boolean[] excluded;

	public CalculateDiffCorr(String genelist, String string, int featureInd, List<String> grp1, List<String> grp2,
			String name1, String name2, MetaOmProject myProject, int m) {

		this.geneList = genelist;
		this.myProject = myProject;
		featureName = string;
		featureIndex = featureInd;
		// create collection of indices
		grp1Ind = getIndices(grp1);
		grp2Ind = getIndices(grp2);
		g1name = name1;
		g2name = name2;

		method = m;
		excluded = MetaOmAnalyzer.getExclude();
	}

	/**
	 * get indices of samples by sample ID
	 * 
	 * @param listDC
	 * @return
	 */
	private Collection<Integer> getIndices(List<String> listDC) {
		Collection<Integer> res = new ArrayList<>();
		String[] dataColumnheaders = myProject.getDataColumnHeaders();
		for (int i = 0; i < dataColumnheaders.length; i++) {
			if (listDC.contains(dataColumnheaders[i])) {
				res.add(i);
			}
		}
		return res;
	}

	/**
	 * Function to calculate correlations of a feature wrt to other features in two
	 * groups (e.g. grp1 has samples A,B,C grp2 has samples C,D,E). The feature is
	 * accessed via the featureIndex Returns two Lists with correlation value of the
	 * feature in the two groups.
	 * 
	 * @param g1Ind
	 *            Indices of samples in group1
	 * @param g2Ind
	 *            Indices of samples in group2
	 * 
	 * @throws IOException
	 */
	public List<List<Double>> computeTwoGroupCorrelations(Collection<Integer> g1Ind, Collection<Integer> g2Ind)
			throws IOException {
		// compute corrGrp1 and corrGrp2
		List<Double> resGrp1 = new ArrayList<>();
		List<Double> resGrp2 = new ArrayList<>();
		// get the target data
		final int[] entries = myProject.getGeneListRowNumbers(geneList);
		// apply transformations if specified
		double[] targetData = myProject.getAllData(featureIndex, false);
		// split targetData into two groups target1 and target2
		double target1[] = new double[grp1Ind.size()];
		double target2[] = new double[grp2Ind.size()];

		int i1, i2;
		i1 = i2 = 0;
		for (int k = 0; k < targetData.length; k++) {
			if (excluded != null && excluded[k]) {
				continue;
			}
			if (g1Ind.contains(k)) {
				target1[i1++] = targetData[k];
			} else if (g2Ind.contains(k)) {
				target2[i2++] = targetData[k];
			}
		}

		// calculate two lists of correlation wrt to target1 and target2
		CorrelationCalc calcy1 = new CorrelationCalc(target1, excluded);
		CorrelationCalc calcy2 = new CorrelationCalc(target2, excluded);

		for (int i = 0; i < entries.length; i++) {
			double[] thisdata;
			thisdata = myProject.getAllData(entries[i], false);

			// split targetData into two groups target1 and target2
			double data1[] = new double[grp1Ind.size()];
			double data2[] = new double[grp2Ind.size()];
			i1 = i2 = 0;
			for (int k = 0; k < thisdata.length; k++) {
				if (excluded != null && excluded[k]) {
					continue;
				}
				if (g1Ind.contains(k)) {
					data1[i1++] = thisdata[k];
				} else if (g2Ind.contains(k)) {
					data2[i2++] = thisdata[k];
				}
			}
			resGrp1.add(calcy1.pearsonCorrelation(data1, myProject.mayContainBlankValues(), myProject.getBlankValue()));
			resGrp2.add(calcy2.pearsonCorrelation(data2, myProject.mayContainBlankValues(), myProject.getBlankValue()));

		}

		List<List<Double>> result = new ArrayList<>();
		result.add(resGrp1);
		result.add(resGrp2);

		return result;

	}

	public void methodParametric() throws IOException {
		// compute corrGrp1 and corrGrp2
		corrGrp1 = new ArrayList<>();
		corrGrp2 = new ArrayList<>();
		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;

		// get the target data
		final int[] entries = myProject.getGeneListRowNumbers(geneList);
		// apply transformations if specified
		double[] targetData = myProject.getAllData(featureIndex, false);
		// split targetData into two groups target1 and target2
		double target1[] = new double[grp1Ind.size()];
		double target2[] = new double[grp2Ind.size()];

		int i1, i2;
		i1 = i2 = 0;
		for (int k = 0; k < targetData.length; k++) {
			if (excluded != null && excluded[k]) {
				continue;
			}
			if (g1Ind.contains(k)) {
				target1[i1++] = targetData[k];
			} else if (g2Ind.contains(k)) {
				target2[i2++] = targetData[k];
			}
		}

		// calculate two lists of correlation wrt to target1 and target2
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, entries.length, true);
		SwingWorker analyzeWorker = new SwingWorker() {

			boolean errored = false;

			public Object construct() {
				try {
					CorrelationCalc calcy1 = new CorrelationCalc(target1, excluded);
					CorrelationCalc calcy2 = new CorrelationCalc(target2, excluded);

					int i = 0;
					do {
						progress.setProgress(i);
						double[] thisdata;
						thisdata = myProject.getAllData(entries[i], false);

						// split targetData into two groups target1 and target2
						double data1[] = new double[grp1Ind.size()];
						double data2[] = new double[grp2Ind.size()];
						int i1 = 0, i2 = 0;
						for (int k = 0; k < thisdata.length; k++) {
							if (excluded != null && excluded[k]) {
								continue;
							}
							if (g1Ind.contains(k)) {
								data1[i1++] = thisdata[k];
							} else if (g2Ind.contains(k)) {
								data2[i2++] = thisdata[k];
							}
						}

						corrGrp1.add(calcy1.pearsonCorrelation(data1, myProject.mayContainBlankValues(),
								myProject.getBlankValue()));
						corrGrp2.add(calcy2.pearsonCorrelation(data2, myProject.mayContainBlankValues(),
								myProject.getBlankValue()));

						i++;
						if (i >= entries.length)
							break;
					} while (!progress.isCanceled());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			public void finished() {
				if (progress.isCanceled()) {
					// JOptionPane.showMessageDialog(null, "click cancelled");
					errored = true;
					progress.dispose();
				}
				if ((!progress.isCanceled()) && (!errored)) {

				}
				progress.dispose();
			}
		};

		analyzeWorker.start();
		progress.setVisible(true);
	}

	/**
	 * Calculate differential correlation values and p value using permutation
	 * method
	 * 
	 * @throws IOException
	 */
	public void methodPermutation(boolean fTransform) throws IOException {

		// compute corrGrp1 and corrGrp2
		corrGrp1 = new ArrayList<>();
		corrGrp2 = new ArrayList<>();
		// exchange grp1Ind and grp2Ind to shuffle data
		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;

		// get the target data
		final int[] entries = myProject.getGeneListRowNumbers(geneList);
		// apply transformations if specified
		double[] targetData = myProject.getAllData(featureIndex, false);
		// split targetData into two groups target1 and target2
		double target1[] = new double[grp1Ind.size()];
		double target2[] = new double[grp2Ind.size()];
		int i1, i2;
		i1 = i2 = 0;
		// handle excluded samples
		for (int k = 0; k < targetData.length; k++) {
			if (excluded != null && excluded[k]) {
				continue;
			}
			if (g1Ind.contains(k)) {
				target1[i1++] = targetData[k];
			} else if (g2Ind.contains(k)) {
				target2[i2++] = targetData[k];
			}
		}
		// target1 contains values of the target genes over samples in first group
		// target2 contains values of the target genes over samples in second group

		// calculate two lists of correlation wrt to target1 and target2
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, entries.length, true);
		SwingWorker analyzeWorker = new SwingWorker() {

			boolean errored = false;

			public Object construct() {
				try {
					CorrelationCalc calcy1 = new CorrelationCalc(target1, excluded);
					CorrelationCalc calcy2 = new CorrelationCalc(target2, excluded);

					int i = 0;
					do {
						progress.setProgress(i);
						double[] thisdata;
						thisdata = myProject.getAllData(entries[i], false);

						// split data into two groups target1 and target2
						double data1[] = new double[grp1Ind.size()];
						double data2[] = new double[grp2Ind.size()];
						int i1 = 0, i2 = 0;
						for (int k = 0; k < thisdata.length; k++) {
							if (excluded != null && excluded[k]) {
								continue;
							}
							if (g1Ind.contains(k)) {
								data1[i1++] = thisdata[k];
							} else if (g2Ind.contains(k)) {
								data2[i2++] = thisdata[k];
							}
						}

						corrGrp1.add(calcy1.pearsonCorrelation(data1, myProject.mayContainBlankValues(),
								myProject.getBlankValue()));
						corrGrp2.add(calcy2.pearsonCorrelation(data2, myProject.mayContainBlankValues(),
								myProject.getBlankValue()));

						i++;
						if (i >= entries.length)
							break;
					} while (!progress.isCanceled());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			public void finished() {
				if (progress.isCanceled()) {
					// JOptionPane.showMessageDialog(null, "click cancelled");
					errored = true;
					progress.dispose();
				}
				if ((!progress.isCanceled()) && (!errored)) {

				}
				progress.dispose();
			}
		};

		analyzeWorker.start();
		progress.setVisible(true);

	}

	/**
	 * Function takes two integer collection and randomly shuffle the two lists
	 * 
	 * @param list
	 *            input list of indices
	 * @param size1
	 *            size of output list1
	 * @param size2
	 *            size of output list2
	 * @return
	 */
	public List<Collection<Integer>> randomlySplitIndices(Collection<Integer> list, int size1, int size2) {
		List<Integer> indexList = new ArrayList<>(list);

		// shuffle the input list
		Collections.shuffle(indexList);
		List<Integer> sublist1 = indexList.subList(0, size1);
		List<Integer> sublist2 = indexList.subList(size1, indexList.size());

		Collection<Integer> grp1Copy = new ArrayList<>(sublist1);
		Collection<Integer> grp2Copy = new ArrayList<>(sublist2);

		List<Collection<Integer>> result = new ArrayList<>();
		result.add(grp1Copy);
		result.add(grp2Copy);

		return result;
	}

	public void doCalc() throws IOException {

		// get current method
		if (this.method == 0) {
			// methodParametric();
			List<List<Double>> res = computeTwoGroupCorrelations(grp1Ind, grp2Ind);
			this.corrGrp1 = res.get(0);
			this.corrGrp2 = res.get(1);

			// after computation of corr vals
			zVals1 = getConveredttoZ(this.corrGrp1);
			zVals2 = getConveredttoZ(this.corrGrp2);
			diffZvals = getDiff(zVals1, zVals2);
			zScores = computeZscores(diffZvals, getGrp1Size(), getGrp2Size());
			pValues = computePVals(zScores);

		} else if (this.method == 1) {

			List<List<Double>> res = computeTwoGroupCorrelations(grp1Ind, grp2Ind);
			this.corrGrp1 = res.get(0);
			this.corrGrp2 = res.get(1);

			// combine indices to shuffle
			Collection<Integer> combinedInd = new ArrayList<>();
			for (Integer i : grp1Ind) {
				combinedInd.add(i);
			}
			for (Integer i : grp2Ind) {
				combinedInd.add(i);
			}

			// shuffle groups and compute correlations
			List<List<List<Double>>> permutationResults = getPermutatedCorrelations(combinedInd);
			if (permutationResults == null) {
				return;
			}
			List<List<Double>> corrRes1 = permutationResults.get(0);
			List<List<Double>> corrRes2 = permutationResults.get(1);

			// compute p value obtained with permutation method
			zVals1 = corrGrp1;
			zVals2 = corrGrp2;
			diffZvals = getDiff(zVals1, zVals2);
			// zscores are irrelevant in this method
			zScores = new ArrayList<>();
			// pValues = new ArrayList<>();
			pValues = computePermutationPvals(diffZvals, corrRes1, corrRes2);
			// initialize zscores with zeroes
			for (int i = 0; i < zVals1.size(); i++) {
				zScores.add(0.0);
			}

		} else {
			JOptionPane.showMessageDialog(null, "Error");
		}

	}

	/**
	 * Function to generate permuted data and calculate the correlation values
	 * 
	 * @param combinedInd
	 * @return
	 * @throws IOException
	 */
	private List<List<List<Double>>> getPermutatedCorrelations(Collection<Integer> combinedInd) throws IOException {
		List<List<Double>> corrRes1 = new ArrayList<>();
		List<List<Double>> corrRes2 = new ArrayList<>();
		int permutations = MetaOmGraph.getNumPermutations();

		final List<Boolean> status = new ArrayList<>();
		// status.add(0, true);
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating permutations...", "", 0L, permutations, true);

		SwingWorker analyzeWorker = new SwingWorker() {
			public boolean errored = false;

			public Object construct() {
				try {

					for (int i = 0; i < permutations && !progress.isCanceled(); i++) {
						progress.setProgress(i);
						List<Collection<Integer>> shuffleResult = randomlySplitIndices(combinedInd, getGrp1Size(),
								getGrp2Size());
						// JOptionPane.showMessageDialog(null, "ind:"+i+"
						// l1"+shuffleResult.get(0).toString()+" l2"+shuffleResult.get(1).toString());
						// get the groupwise correlation for this permutation
						List<List<Double>> thisResult = computeTwoGroupCorrelations(shuffleResult.get(0),
								shuffleResult.get(1));
						// add the results two two lists
						corrRes1.add(thisResult.get(0));
						corrRes2.add(thisResult.get(1));
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			public void finished() {
				if (progress.isCanceled()) {
					JOptionPane.showMessageDialog(null, "click cancelled");
					errored = true;
					JOptionPane.showMessageDialog(null, status.toString());
					status.add(false);
					JOptionPane.showMessageDialog(null, status.toString());
					progress.dispose();

				}

				if ((!progress.isCanceled()) && (!errored)) {
					status.add(true);
				}
				progress.dispose();
			}

		};

		analyzeWorker.start();
		progress.setVisible(true);

		
			
			JOptionPane.showMessageDialog(null, status.toString());
			// cancelled or error
			if (!status.get(0)) {
				return null;
			}
			List<List<List<Double>>> result = new ArrayList<>();
			result.add(corrRes1);
			result.add(corrRes2);
			JOptionPane.showMessageDialog(null, "returning RES");
			return result;
		
	}

	/**
	 * Function to compute p values from the permutations
	 * 
	 * @param diff
	 *            the observed difference in correlation values
	 * @param permutedResGrp1
	 *            correlation under group 1 obtained with permuted datasets
	 * @param permutedResGrp2
	 *            correlation under group 2 obtained with permuted datasets
	 * @return
	 */
	public List<Double> computePermutationPvals(List<Double> diff, List<List<Double>> permutedResGrp1,
			List<List<Double>> permutedResGrp2) {

		List<Double> result = new ArrayList<>();

		// do for each row
		for (int i = 0; i < diff.size(); i++) {
			// initialize extreme results
			int numExtreme = 0;
			double thisDiff = diff.get(i);

			// for each permutation
			for (int j = 0; j < permutedResGrp1.size(); j++) {
				// get result from jth permutation for ith row
				double thisr1 = permutedResGrp1.get(j).get(i);
				double thisr2 = permutedResGrp2.get(j).get(i);
				double obsDiff = thisr1 - thisr2;

				if (Math.abs(obsDiff) >= Math.abs(thisDiff)) {
					numExtreme++;
				}
			}

			double thisPval = (double) numExtreme / permutedResGrp1.size();

			result.add(thisPval);
		}

		return result;

	}

	public List<String> getFeatureNames() {
		String[] featNamesArr = myProject.getDefaultRowNames(myProject.getGeneListRowNumbers(geneList));
		return Arrays.asList(featNamesArr);
	}

	public int getGrp1Size() {
		return grp1Ind.size();
	}

	public int getGrp2Size() {
		return grp2Ind.size();
	}

	public List<Double> getCorrGrp1() {
		return corrGrp1;
	}

	public List<Double> getCorrGrp2() {
		return corrGrp2;
	}

	/**
	 * convert r values to z applying Fisher's transform
	 * 
	 * @param rVals
	 * @return
	 */
	public static List<Double> getConveredttoZ(List<Double> rVals) {
		List<Double> res = new ArrayList<>();
		Atanh atan = new Atanh();
		for (double d : rVals) {
			res.add(atan.value(d));
			// JOptionPane.showMessageDialog(null, "val:"+d+" atan:"+atan.value(d));
		}
		return res;
	}

	/**
	 * returns difference between two correlation value arrays
	 * 
	 * @param rVals1
	 * @param rVals2
	 * @return
	 */
	public static List<Double> getDiff(List<Double> rVals1, List<Double> rVals2) {
		List<Double> res = new ArrayList<>();
		for (int i = 0; i < rVals1.size(); i++) {
			res.add(rVals1.get(i) - rVals2.get(i));
		}
		return res;
	}

	/**
	 * Get z score array from two correlation arrays
	 * 
	 * @param rVals1
	 * @param rVals2
	 * @return
	 */

	public static List<Double> computeZscores(List<Double> diff, int n1, int n2) {

		List<Double> res = new ArrayList<>();
		for (int i = 0; i < diff.size(); i++) {
			double thisZ = diff.get(i);
			double denom = Math.sqrt((1 / ((double) n1 - 3)) + (1 / ((double) n2 - 3)));
			// JOptionPane.showMessageDialog(null, "denom:" + denom);
			thisZ = thisZ / denom;
			res.add(thisZ);
		}
		return res;
	}

	/**
	 * get the pvalues using normal distribution
	 * 
	 * @param zScores
	 *            list of z scores
	 * @return
	 */
	public static List<Double> computePVals(List<Double> zScores) {
		List<Double> res = new ArrayList<>();
		NormalDistribution nob = new NormalDistribution();

		for (int i = 0; i < zScores.size(); i++) {
			double thisZ = zScores.get(i);
			if (thisZ > 0) {
				thisZ = thisZ * -1;
			}
			res.add(nob.cumulativeProbability(thisZ) * 2);
		}
		return res;
	}

	/**
	 * get z vals
	 * 
	 * @param index
	 * @return
	 */
	public List<Double> getzVals(int index) {
		if (index == 1) {
			return zVals1;
		} else if (index == 2) {
			return zVals2;
		}
		return null;
	}

	public List<Double> getDiffZVals() {
		return diffZvals;
	}

	public List<Double> getzScores() {
		return zScores;
	}

	public List<Double> getpValues() {
		return pValues;
	}

}
