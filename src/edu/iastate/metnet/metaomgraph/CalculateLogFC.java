package edu.iastate.metnet.metaomgraph;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;
import org.biomage.Array.Array;

import com.sun.xml.internal.ws.api.Cancelable;

import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.DifferentialExpFrame;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;

public class CalculateLogFC {

	private String selectedList;
	private String grpID;
	private MetaOmProject myProject;
	private Map<String, Collection<Integer>> splitIndex;
	private boolean[] excluded;
	private int testMethod;

	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;
	private List<Double> testPvals;
	private List<Double> ftestPvals;
	private List<Double> ftestRatiovals;

	private boolean calcStatus = true;
	SwingWorker analyzeWorker;

	// group indices
	Collection<Integer> grp1Ind;
	Collection<Integer> grp2Ind;
	String grp1Name;
	String grp2Name;

	public CalculateLogFC(String selectedList, String grpID, MetaOmProject myProject, boolean tflag) {
		this.selectedList = selectedList;
		this.grpID = grpID;
		this.myProject = myProject;
		excluded = MetaOmAnalyzer.getExclude();
		this.testMethod = 0;

	}

	/**
	 * Class to compute logFC
	 * 
	 * @param selectedList
	 *            Selected list of features to use
	 * @param grpI
	 *            List of sample names (data columns) in first group
	 * @param grpII
	 *            List of sample names (data columns) in second group
	 * @param myProject
	 *            active project
	 * @param method
	 *            method to use for calculation 0: M-W U test 1: t test (equal
	 *            variance) 2: Welch t test (unequal variance) 3: Paired t-test 4:
	 *            wilcoxon
	 * 
	 * 
	 */
	public CalculateLogFC(String selectedList, List<String> grpI, List<String> grpII, String name1, String name2,
			MetaOmProject myProject, int method) {
		this.selectedList = selectedList;
		this.myProject = myProject;
		excluded = MetaOmAnalyzer.getExclude();
		this.testMethod = method;
		if (testMethod < 0 || testMethod > 6) {
			JOptionPane.showMessageDialog(null, "Invalid method selected", "Invalid method", JOptionPane.ERROR_MESSAGE);
			calcStatus = false;
		}

		// create collection of indices
		grp1Ind = getIndices(grpI);
		grp2Ind = getIndices(grpII);
		this.grp1Name = name1;
		this.grp2Name = name2;
		// JOptionPane.showMessageDialog(null, "g1:" + grpI.toString() + " g1ind:" +
		// grp1Ind.toString());
		// JOptionPane.showMessageDialog(null, "g2:" + grpII.toString() + " g2ind:" +
		// grp2Ind.toString());

	}

	private Collection<Integer> getIndices(List<String> listDC) {
		Collection<Integer> res = new ArrayList<>();
		String[] dataColumnheaders = myProject.getDataColumnHeaders();
		// JOptionPane.showMessageDialog(null,
		// "DH:"+Arrays.toString(dataColumnheaders));
		for (int i = 0; i < dataColumnheaders.length; i++) {
			if (listDC.contains(dataColumnheaders[i])) {
				res.add(i);
			}
		}
		return res;
	}

	// return true if grpID splits into two groups
	public boolean createGroup() {
		if (grpID.equals("By Query")) {
			// display query panel
			final TreeSearchQueryConstructionPanel tsp = new TreeSearchQueryConstructionPanel(myProject, false);
			final MetadataQuery[] queries;
			queries = tsp.showSearchDialog();
			if (tsp.getQueryCount() <= 0) {
				System.out.println("Search dialog cancelled");
				// User didn't enter any queries
				return false;
			}
			// final int[] result = new int[myProject.getDataColumnCount()];
			Collection<Integer> result = new ArrayList<>();
			List<Collection<Integer>> resList = new ArrayList<>();
			final boolean nohits;
			new AnimatedSwingWorker("Searching...", true) {
				@Override
				public Object construct() {
					ArrayList<Integer> toAdd = new ArrayList<Integer>(result.size());
					for (int i = 0; i < myProject.getDataColumnCount(); i++) {
						toAdd.add(i);
					}
					Integer[] hits = myProject.getMetadataHybrid().search(queries, tsp.matchAll());
					// remove excluded cols from list
					// urmi
					if (excluded != null) {
						List<Integer> temp = new ArrayList<>();
						for (Integer i : hits) {
							if (!excluded[i]) {
								temp.add(i);
							}
						}
						hits = new Integer[temp.size()];
						hits = temp.toArray(hits);
					}

					int index;
					for (index = 0; index < hits.length; index++) {
						result.add(hits[index]);
						toAdd.remove(hits[index]);
					}
					// add result and complement list
					resList.add(result);
					resList.add(myProject.getComplentDataColumns(result, true));
					return null;
				}
			}.start();

			// create a split index with "hits" as one category and all others as second
			// category
			if (resList.get(0).size() < 1) {
				JOptionPane.showMessageDialog(null, "No hits found", "No hits", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			if (resList.size() != 2) {
				JOptionPane.showMessageDialog(null, "More than two categories", "More than two categories",
						JOptionPane.INFORMATION_MESSAGE);
				return false;
			}

			splitIndex = new TreeMap<>();
			splitIndex.put("Hits", resList.get(0));
			splitIndex.put("Other", resList.get(1));
		} else {
			// split data set by values of col_val
			List<String> selectedVals = new ArrayList<>();
			selectedVals.add(grpID);
			this.splitIndex = myProject.getMetadataHybrid().cluster(selectedVals);

			if (splitIndex.size() != 2) {
				JOptionPane.showMessageDialog(null, "More than two categories", "More than two categories",
						JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}

		grp1Ind = (Collection<Integer>) splitIndex.values().toArray()[0];
		grp2Ind = (Collection<Integer>) splitIndex.values().toArray()[1];
		return true;

	}

	public void doCalc() {
		if (calcStatus == false) {
			return;
		}
		// indices of selected feature list
		int[] selected = myProject.getGeneListRowNumbers(this.selectedList);
		// array to store fc values
		double[] fcVals = new double[selected.length];
		featureNames = new ArrayList<>();
		mean1 = new ArrayList<>();
		mean2 = new ArrayList<>();

		testPvals = new ArrayList<>();
		ftestPvals = new ArrayList<>();
		ftestRatiovals = new ArrayList<>();
		// utestPvals = new ArrayList<>();

		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;

		double log2b10 = Math.log(2.0D);
		// apache objects too conduct statistical tests
		TTest tob = new TTest();
		Variance vob = new Variance();
		MannWhitneyUTest uob = new MannWhitneyUTest();
		WilcoxonSignedRankTest wsrtob = new WilcoxonSignedRankTest();

		// tob.

		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, selected.length, true);
		analyzeWorker = new SwingWorker() {
			boolean errored = false;

			public Object construct() {
				for (int r = 0; r < selected.length; r++) {
					progress.setProgress(r);
					double[] thisDataRaw = null; // untransformed data
					double[] thisData = null;
					try {
						// get untransformed data to calculate logFC
						thisDataRaw = myProject.getAllData(selected[r], true);
						// JOptionPane.showMessageDialog(null, "this Data:"+Arrays.toString(thisData));

						double m1 = 0, m2 = 0, fc = 0;

						for (int k = 0; k < thisDataRaw.length; k++) {
							if (excluded != null && excluded[k]) {
								continue;
							}
							if (g1Ind.contains(k)) {
								// for GM
								m1 += (Math.log(thisDataRaw[k] + 1) / log2b10);
								// for AM
								// m1 += thisDataRaw[k] ;

							} else if (g2Ind.contains(k)) {
								// for GM
								m2 += (Math.log(thisDataRaw[k] + 1) / log2b10);
								// for AM
								// m2 += thisDataRaw[k] ;
							}
						}

						// JOptionPane.showMessageDialog(null, "s1:" + m1 + " s2:" + m2);
						m1 = m1 / g1Ind.size();
						m2 = m2 / g2Ind.size();

						// logchange with AM
						// double logm1=(Math.log(m1+1) / log2b10);
						// double logm2=(Math.log(m2+1) / log2b10);
						// m1=logm1;
						// m2=logm2;
						// JOptionPane.showMessageDialog(null, "log of:"+m1+" is"+logm1);
						// JOptionPane.showMessageDialog(null, "log of:"+m2+" is"+logm2);
						fc = m1 - m2;

						fcVals[r] = fc;
						featureNames.add(myProject.getDefaultRowNames(selected[r]));
						// add means for the rth feature
						mean1.add(m1);
						mean2.add(m2);

						// perform selected tests
						// step 1 create two arrays containing data from two groups for rth feature

						// get transformed data, if any transformation applied
						thisData = myProject.getAllData(selected[r]);
						// JOptionPane.showMessageDialog(null, "this Data2:"+Arrays.toString(thisData));

						// s1 and s2 stores data for two groups
						double[] s1 = new double[g1Ind.size()];
						double[] s2 = new double[g2Ind.size()];
						int s1ind = 0, s2ind = 0;
						for (int k = 0; k < thisData.length; k++) {
							if (excluded != null && excluded[k]) {
								continue;
							}
							if (g1Ind.contains(k)) {
								s1[s1ind++] = thisData[k];

							} else if (g2Ind.contains(k)) {
								s2[s2ind++] = thisData[k];
							}
						}

						// store p vals of all tests in ttestPvals
						// additionly store pvals of f test in ftestPvals
						if (testMethod == 0) {
							// perform MannWhitney U test
							testPvals.add(uob.mannWhitneyUTest(s1, s2));
						} else if (testMethod == 1 || testMethod == 2) {
							// perform t test with F test
							// do f test
							double vs1 = vob.evaluate(s1);
							double vs2 = vob.evaluate(s2);
							FDistribution fob = null;
							double fRatio = 0;
							if (vs1 > vs2) {
								fRatio = vs1 / vs2;
								fob = new FDistribution(g1Ind.size() - 1, g2Ind.size() - 1);
							} else {
								fRatio = vs2 / vs1;
								fob = new FDistribution(g2Ind.size() - 1, g1Ind.size() - 1);
							}

							ftestPvals.add(1 - fob.cumulativeProbability(fRatio));
							ftestRatiovals.add(fRatio);

							if (testMethod == 1) {
								// do t test
								testPvals.add(tob.homoscedasticTTest(s1, s2));
							} else if (testMethod == 2) {
								// do welch test
								testPvals.add(tob.tTest(s1, s2));
							}

						} else if (testMethod == 4) {
							// perform paired t
							testPvals.add(tob.pairedTTest(s1, s2));
						} else if (testMethod == 5) {
							// perform wilcoxonSignedRankTest
							// NOTE: exact p vals only work for n <=30
							// set exact pv is n <= 10 otherwise its slow
							if (s1.length <= 10) {
								testPvals.add(wsrtob.wilcoxonSignedRankTest(s1, s2, true));
							} else {
								testPvals.add(wsrtob.wilcoxonSignedRankTest(s1, s2, false));
							}
						}

						else if (testMethod == 3) {
							// perform permutation test

							// combine the data and randomly sample
							List<Double> combinedData = new ArrayList<>();
							for (int k = 0; k < thisDataRaw.length; k++) {
								if (excluded != null && excluded[k]) {
									continue;
								}
								if (g1Ind.contains(k) || g2Ind.contains(k)) {
									combinedData.add(thisDataRaw[k]);
								}
							}

							// observed geometric means are m1 and m2

							double thisPval = computePermutationPval(s1, s2, MetaOmGraph.getNumPermutations());
							testPvals.add(thisPval);
							// return null;
						}

						else if (testMethod == 6) {
							// perform permutation test on paired data

							double thisPval = computePermutationPvalPaired(s1, s2, MetaOmGraph.getNumPermutations());
							testPvals.add(thisPval);
							// return null;
						}

						else {
							JOptionPane.showMessageDialog(null, "Unknown Test Error", "Error",
									JOptionPane.ERROR_MESSAGE);
							return null;

						}

					} catch (IOException ioe) {
						JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
								"IOException", 0);
						ioe.printStackTrace();
						progress.dispose();
						errored = true;
						calcStatus = false;
						return null;
					} catch (ArrayIndexOutOfBoundsException oob) {
						progress.dispose();
						errored = true;
						calcStatus = false;
						return null;
					}
				}
				return null;
			}

			public void finished() {
				if (progress.isCanceled()) {
					// JOptionPane.showMessageDialog(null, "click cancelled");
					calcStatus = false;
					errored = true;
					progress.dispose();

				}
				if ((!progress.isCanceled()) && (!errored)) {
					calcStatus = true;
				}
				progress.dispose();
			}
		};

		analyzeWorker.start();
		progress.setVisible(true);

	}

	/**
	 * get diff in means of two lists
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public double getDiffInMeans(List<Double> list1, List<Double> list2) {
		OptionalDouble avg1 = list1.stream().mapToDouble(a -> a).average();
		OptionalDouble avg2 = list2.stream().mapToDouble(a -> a).average();
		if (!avg1.isPresent() || !avg2.isPresent()) {
			return 0.0;
		}
		return avg1.getAsDouble() - avg2.getAsDouble();

	}

	public double getDiffInMeans(double[] s1, double[] s2) {
		double mean1 = 0;
		double mean2 = 0;
		for (int i = 0; i < s1.length; i++) {
			mean1 += s1[i];
		}

		for (int i = 0; i < s2.length; i++) {
			mean2 += s2[i];
		}

		mean1 = mean1 / s1.length;
		mean2 = mean2 / s2.length;
		return mean1 - mean2;
	}

	/**
	 * Function to perform a permutation test on paired data
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 * @throws IOException
	 */
	public double computePermutationPvalPaired(double[] s1, double[] s2, int numPermutations) {

		double thisDiff = getDiffInMeans(s1, s2);
		// System.out.println("Obs mean:" + thisDiff);

		double[] diffArray = new double[s1.length];
		List<Double> permutedMeanDiffs = new ArrayList<>();

		for (int i = 0; i < s1.length; i++) {
			diffArray[i] = s1[i] - s2[i];
		}
		ArrayList<Integer> indices = new ArrayList<>();
		for (int i = 0; i < diffArray.length; i++) {
			indices.add(i);
		}
		// do for number of permutations
		for (int k = 0; k < numPermutations; k++) {

			int[] toFlip = new int[diffArray.length];

			// randomly choose values to exchange
			for (int i = 0; i < toFlip.length; i++) {
				toFlip[i] = new Random().nextInt(2);
				if (toFlip[i] == 0) {
					toFlip[i] = -1;
				}
			}

			// calculate statistic from permuted data
			double thisSum = 0;
			for (int i = 0; i < diffArray.length; i++) {
				thisSum += (toFlip[i] * diffArray[i]);
			}

			permutedMeanDiffs.add(thisSum / s1.length);
		}

		// System.out.println("Perm mean:"+permutedMeanDiffs.toString());
		// compute pvalue
		double numExtremes = 0;
		for (int i = 0; i < permutedMeanDiffs.size(); i++) {
			if (Math.abs(permutedMeanDiffs.get(i)) >= Math.abs(thisDiff)) {
				numExtremes += 1;
			}
		}

		// add 1 for observed statistic
		return (numExtremes + 1.0) / (permutedMeanDiffs.size() + 1.0);
	}

	/**
	 * Function to compute p value of geometric means equality test using
	 * permutations
	 * 
	 * @param mean1
	 * @param mean2
	 * @param size1
	 * @param size2
	 * @param combinedData
	 * @return
	 * @throws IOException
	 */
	public double computePermutationPval(double[] s1, double[] s2, int numPermutations) {

		// combine the data and randomly sample
		List<Double> combinedData = new ArrayList<>();
		for (int k = 0; k < s1.length; k++) {
			combinedData.add(s1[k]);
		}
		for (int k = 0; k < s2.length; k++) {
			combinedData.add(s2[k]);
		}

		double thisDiff = getDiffInMeans(s1, s2);
		System.out.println("ObsMean:" + thisDiff);

		List<Double> permutedDiffs = computeTwoGroupMeanDifferences(s1.length, s2.length, combinedData,
				numPermutations);

		// compute pvalue
		double numExtremes = 0;
		for (int i = 0; i < permutedDiffs.size(); i++) {
			if (Math.abs(permutedDiffs.get(i)) >= Math.abs(thisDiff)) {
				numExtremes += 1;
			}
		}

		// add 1 for observed statistic
		return (numExtremes + 1.0) / (permutedDiffs.size() + 1.0);
	}

	/**
	 * Function to compute difference in geometric means of genes over two groups
	 * after shuffling data
	 * 
	 * @param g1Ind
	 * @param g2Ind
	 * @return
	 * @throws IOException
	 */
	public List<Double> computeTwoGroupMeanDifferences(int size1, int size2, List<Double> combinedData,
			int numPermutations) {
		// compute corrGrp1 and corrGrp2
		List<Double> diff = new ArrayList<>();

		for (int i = 0; i < numPermutations; i++) {
			Collections.shuffle(combinedData);
			List<Double> grp1Data = combinedData.subList(0, size1);
			List<Double> grp2Data = combinedData.subList(size1, combinedData.size());
			// compute the two geometric means
			diff.add(getDiffInMeans(grp1Data, grp2Data));
		}

		return diff;

	}

	public double getGeometricMean(List<Double> data) {

		double sum = 0;
		double log2b10 = Math.log(2.0D);
		for (double d : data) {
			sum += (Math.log(d + 1) / log2b10);
		}

		return sum / data.size();
	}

	/**
	 * return status if calculation was completed
	 * 
	 * @return
	 */
	public boolean getcalcStatus() {

		return this.calcStatus;
	}

	public List<String> getFeatureNames() {
		return this.featureNames;
	}

	public List<Double> getMean1() {
		return this.mean1;
	}

	public List<Double> getMean2() {
		return this.mean2;
	}

	public List<Double> testPV() {
		return this.testPvals;
	}

	public List<Double> ftestPV() {
		return this.ftestPvals;
	}

	public List<Double> ftestRatios() {
		return this.ftestRatiovals;
	}

	public String getMethodName() {
		String[] methods = new String[] { "M-W U test", "Student's t-test", "Welch's t-test", "Paired t-test",
				"Wilcoxon Signed Rank Test" };
		return methods[testMethod];
	}

	public String getGrp1Name() {
		return grp1Name;
	}

	public String getGrp2Name() {
		return grp2Name;
	}

	public static void main(String[] args) {
		CalculateLogFC ob = new CalculateLogFC(null, null, null, false);
		double[] s1 = { 5, 6, 7 };
		double[] s2 = { 1, 2, 3 };
		System.out.println(ob.getDiffInMeans(s1, s2));

		// test permutation test for paired data
		double[] d1 = { 80.50, 84.90, 81.50, 82.60, 79.90, 88.70, 94.90, 76.30, 81.00, 80.50, 85.00, 89.20, 81.30,
				76.50, 70.00, 80.40, 83.30, 83.00, 87.70, 84.20, 86.40, 76.50, 80.20, 87.80, 83.30, 79.70, 84.50, 80.80,
				87.40 };
		double[] d2 = { 82.20, 85.60, 81.40, 81.90, 76.40, 103.6, 98.40, 93.40, 73.40, 82.10, 96.70, 95.30, 82.40,
				72.50, 90.90, 71.30, 85.40, 81.60, 89.10, 83.90, 82.70, 75.70, 82.60, 100.4, 85.20, 83.60, 84.60, 96.20,
				86.70 };

		System.out.println(ob.computePermutationPvalPaired(d1, d2, 1000));
		System.out.println(ob.computePermutationPval(d1, d2, 100000));

	}
}
