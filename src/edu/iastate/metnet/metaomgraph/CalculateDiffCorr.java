package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;



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
	private int method;

	private List<Double> corrGrp1;
	private List<Double> corrGrp2;

	private boolean[] excluded;

	public CalculateDiffCorr(String genelist, String string, int featureInd, List<String> grp1, List<String> grp2, String name1,
			String name2, MetaOmProject myProject, int m) {

		this.geneList=genelist;
		featureName = string;
		featureIndex = featureInd;
		// create collection of indices
		grp1Ind = getIndices(grp1);
		grp2Ind = getIndices(grp2);
		g1name = name1;
		g2name = name2;
		this.myProject = myProject;
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

	public void doCalc() throws IOException {
		// compute corrGrp1 and corrGrp2

		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;
		
		//get the target data
		final int[] entries = myProject.getGeneListRowNumbers(geneList);
		//apply transformations if specified
		double[] targetData=myProject.getAllData(featureIndex, false);
		//split targetData into two groups target1 and target2
		double target1[] = new double[grp1Ind.size()];
		double target2[] = new double[grp2Ind.size()];
		int i1=0,i2=0;
		for (int k = 0; k < targetData.length; k++) {
			if (excluded != null && excluded[k]) {
				continue;
			}
			if (g1Ind.contains(k)) {
				target1[i1++]=targetData[k];
			} else if (g2Ind.contains(k)) {
				target1[i2++]=targetData[k];
			}
		}
		
		
		// calculate two lists of correlation wrt to target1 and target2
		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, entries.length, true);
		SwingWorker analyzeWorker = new SwingWorker() {
			
			boolean errored = false;
			public Object construct() {
				int r = 0;
				progress.setProgress(r);
				double[] thisData = null;
				try {
					
					
					

				

				} catch (IOException ioe) {
					JOptionPane.showMessageDialog(MetaOmGraph.getMainWindow(), "Error reading project data",
							"IOException", 0);
					ioe.printStackTrace();
					progress.dispose();
					errored = true;
					return null;
				} catch (ArrayIndexOutOfBoundsException oob) {
					progress.dispose();
					errored = true;
					return null;
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
		/*
		 * EventQueue.invokeLater(new Runnable() { public void run() { try {
		 * analyzeWorker.start(); progress.setVisible(true); } catch (Exception e) {
		 * analyzeWorker.interrupt(); progress.dispose(); e.printStackTrace(); } } });
		 */

	}

	

}
