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

import edu.iastate.metnet.metaomgraph.Metadata.MetadataQuery;
import edu.iastate.metnet.metaomgraph.ui.TreeSearchQueryConstructionPanel;

public class calculateLogFC {

	private String selectedList;
	private String grpID;
	private MetaOmProject myProject;
	private Map<String, Collection<Integer>> splitIndex;
	private boolean[] excluded;
	
	private List<String> featureNames;
	private List<Double> mean1;
	private List<Double> mean2;

	public calculateLogFC(String selectedList, String grpID, MetaOmProject myProject) {
		this.selectedList = selectedList;
		this.grpID = grpID;
		this.myProject = myProject;
		excluded = MetaOmAnalyzer.getExclude();

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
					//add result and complement list
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

		return true;

	}

	public void doCalc() throws IOException {

		int[] selected = myProject.getGeneListRowNumbers(this.selectedList);
		double[] fcVals = new double[selected.length];
		featureNames=new ArrayList<>();
		mean1=new ArrayList<>();
		mean2=new ArrayList<>();
		
		for (int r = 0; r < selected.length; r++) {
			double[] thisData = myProject.getAllData(selected[r], true);
			double m1 = 0, m2 = 0, fc = 0;
			Collection<Integer> g1Ind = (Collection<Integer>) splitIndex.values().toArray()[0];
			Collection<Integer> g2Ind = (Collection<Integer>) splitIndex.values().toArray()[1];
			double log2b10 = Math.log(2.0D);

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

			//JOptionPane.showMessageDialog(null, "s1:" + m1 + " s2:" + m2);
			m1 = m1 / g1Ind.size();
			m2 = m2 / g2Ind.size();
			fc = m1 - m2;
			//JOptionPane.showMessageDialog(null, "mean1:" + m1 + " s:" + g1Ind.size());
			//JOptionPane.showMessageDialog(null, "mean2:" + m2 + " s:" + g2Ind.size());
			fcVals[r] = fc;
			
			featureNames.add(myProject.getDefaultRowNames(r));
			mean1.add(m1);
			mean2.add(m2);

		}

		JOptionPane.showMessageDialog(null, Arrays.toString(fcVals));

	}
	
	public List<String> getFeatureNames(){
		return this.featureNames;
	}
	
	public List<Double> getMean1(){
		return this.mean1;
	}
	
	public List<Double> getMean2(){
		return this.mean2;
	}

}
