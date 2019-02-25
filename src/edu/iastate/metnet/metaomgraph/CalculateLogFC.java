package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;

import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.BlockingProgressDialog;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;

public class CalculateLogFC {

	private String selectedList;
	private String grpID;
	private MetaOmProject myProject;
	private Map<String, Collection<Integer>> splitIndex;
	private boolean[] excluded;
	private boolean dotTest;
	private int testMethod;

	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;
	private List<Double> ttestPvals;
	private List<Double> ftestPvals;
	private List<Double> ftestRatiovals;
	private List<Double> utestPvals;
	
	//group indices
	Collection<Integer> grp1Ind ;
	Collection<Integer> grp2Ind ;

	public CalculateLogFC(String selectedList, String grpID, MetaOmProject myProject, boolean tflag) {
		this.selectedList = selectedList;
		this.grpID = grpID;
		this.myProject = myProject;
		excluded = MetaOmAnalyzer.getExclude();
		dotTest = tflag;
		

	}
	
	/**
	 * Class to compute logFC
	 * @param selectedList Selected list of features to use
	 * @param grpI	List of sample names (data columns) in first group
	 * @param grpII	List of sample names (data columns) in second group
	 * @param myProject	active project
	 * @param method	method to use for calculation
	 * 1: M-W U test
	 * 2: t test (equal variance)
	 * 3: Welch t test (unequal variance)
	 * 4: Paired t-test
	 * 
	 * 
	 */
	public CalculateLogFC(String selectedList, List<String> grpI, List<String> grpII , MetaOmProject myProject, int method) {
		this.selectedList = selectedList;
		this.myProject = myProject;
		excluded = MetaOmAnalyzer.getExclude();
		this.testMethod=method;
		if(testMethod<1 || testMethod>4) {
			JOptionPane.showMessageDialog(null, "Invalid method selected", "Invalid method", JOptionPane.ERROR_MESSAGE);
		}
		
		//create collection of indices
		grp1Ind=getIndices(grpI);
		grp2Ind=getIndices(grpII);
		

	}
	
	private Collection<Integer> getIndices(List<String> listDC){
		 Collection<Integer> res=  new ArrayList<>();
		 String [] dataColumnheaders=myProject.getDataColumnHeaders();
		 for(int i=0;i<dataColumnheaders.length;i++) {
			 if(listDC.contains(dataColumnheaders[i])) {
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

		grp1Ind=(Collection<Integer>) splitIndex.values().toArray()[0];
		grp2Ind=(Collection<Integer>) splitIndex.values().toArray()[1];
		return true;

	}

	public void doCalc() {
		
		int[] selected = myProject.getGeneListRowNumbers(this.selectedList);
		double[] fcVals = new double[selected.length];
		featureNames = new ArrayList<>();
		mean1 = new ArrayList<>();
		mean2 = new ArrayList<>();
		if (dotTest) {
			ttestPvals = new ArrayList<>();
			ftestPvals = new ArrayList<>();
			ftestRatiovals = new ArrayList<>();
			utestPvals = new ArrayList<>();
		}
		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;
		
		double log2b10 = Math.log(2.0D);
		TTest tob = new TTest();
		Variance vob = new Variance();
		
		MannWhitneyUTest uob = new MannWhitneyUTest();

		// tob.

		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, selected.length, true);
		SwingWorker analyzeWorker = new SwingWorker() {
			boolean errored = false;

			public Object construct() {

				for (int r = 0; r < selected.length; r++) {
					progress.setProgress(r);
					double[] thisData = null;
					try {
						thisData = myProject.getAllData(selected[r], true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					double m1 = 0, m2 = 0, fc = 0;

					for (int k = 0; k < thisData.length; k++) {
						if (excluded != null && excluded[k]) {
							continue;
						}
						if (g1Ind.contains(k)) {
							m1 += (Math.log(thisData[k] + 1) / log2b10);

						} else {
							m2 += (Math.log(thisData[k] + 1) / log2b10);
						}
					}

					// JOptionPane.showMessageDialog(null, "s1:" + m1 + " s2:" + m2);
					m1 = m1 / g1Ind.size();
					m2 = m2 / g2Ind.size();
					fc = m1 - m2;
					// JOptionPane.showMessageDialog(null, "mean1:" + m1 + " s:" + g1Ind.size());
					// JOptionPane.showMessageDialog(null, "mean2:" + m2 + " s:" + g2Ind.size());
					fcVals[r] = fc;

					featureNames.add(myProject.getDefaultRowNames(selected[r]));
					mean1.add(m1);
					mean2.add(m2);

					if (dotTest) {
						try {
							thisData = myProject.getAllData(selected[r]);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						double[] s1 = new double[g1Ind.size()];
						double[] s2 = new double[g2Ind.size()];
						int s1ind = 0, s2ind = 0;
						for (int k = 0; k < thisData.length; k++) {
							if (excluded != null && excluded[k]) {
								continue;
							}
							if (g1Ind.contains(k)) {
								s1[s1ind++] = thisData[k];

							} else {
								s2[s2ind++] = thisData[k];
							}
						}
						// do ftest
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
						ttestPvals.add(tob.tTest(s1, s2));
						//mannwhitney test
						utestPvals.add(uob.mannWhitneyUTest(s1, s2));
						
					}

				}
				return null;
			}

			public void finished() {
				if ((!progress.isCanceled()) && (!errored)) {
					
				}
				progress.dispose();
			}
		};
		analyzeWorker.start();
		progress.setVisible(true);

		//return exitStatus;
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

	public List<Double> ttestPV() {
		return this.ttestPvals;
	}

	public List<Double> ftestPV() {
		return this.ftestPvals;
	}

	public List<Double> ftestRatios() {
		return this.ftestRatiovals;
	}
	
	public List<Double> utestPV() {
		return this.utestPvals;
	}

}
