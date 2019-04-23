package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

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

	public void doCalc() {
		// compute corrGrp1 and corrGrp2

		Collection<Integer> g1Ind = grp1Ind;
		Collection<Integer> g2Ind = grp2Ind;

		final BlockingProgressDialog progress = new BlockingProgressDialog(MetaOmGraph.getMainWindow(),
				"Calculating...", "", 0L, 1, true);
		SwingWorker analyzeWorker = new SwingWorker() {
			boolean errored = false;

			public Object construct() {
				int r = 0;
				progress.setProgress(r);
				double[] thisData = null;
				try {
					// get untransformed data to calculate logFC
					thisData = myProject.getAllData(featureIndex, true);
					
					// JOptionPane.showMessageDialog(null, "this Data:"+Arrays.toString(thisData));

					

					/*for (int k = 0; k < thisData.length; k++) {
						if (excluded != null && excluded[k]) {
							continue;
						}
						if (g1Ind.contains(k)) {
							m1 += (Math.log(thisData[k] + 1) / log2b10);

						} else if (g2Ind.contains(k)) {
							m2 += (Math.log(thisData[k] + 1) / log2b10);
						}
					}*/

				

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
