package edu.iastate.metnet.metaomgraph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

	public CalculateDiffCorr(String genelist, String string, int featureInd, List<String> grp1, List<String> grp2,
			String name1, String name2, MetaOmProject myProject, int m) {

		this.geneList = genelist;
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
		int i1 = 0, i2 = 0;
		for (int k = 0; k < targetData.length; k++) {
			if (excluded != null && excluded[k]) {
				continue;
			}
			if (g1Ind.contains(k)) {
				target1[i1++] = targetData[k];
			} else if (g2Ind.contains(k)) {
				target1[i2++] = targetData[k];
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
	
	
	public List<String> getFeatureNames(){
		String[] featNamesArr=myProject.getDefaultRowNames(myProject.getGeneListRowNumbers(geneList));
		return Arrays.asList(featNamesArr);
	}
	public int getGrp1Size() {
		return grp1Ind.size();
	}
	
	public int getGrp2Size() {
		return grp2Ind.size();
	}
	
	public List<Double> getCorrGrp1(){
		return corrGrp1;
	}
	
	public List<Double> getCorrGrp2(){
		return corrGrp2;
	}

}
