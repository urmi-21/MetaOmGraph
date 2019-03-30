package edu.iastate.metnet.metaomgraph;

import java.util.List;

/**
 * Class to store results of a differential expression analysis
 * 
 * @author urmi
 *
 */

public class DifferentialExpResults {

	// method used for the analysis
	// 1 MW U test; 2 t test; 3 Weltch t test; 4 Paired t test; 5 Wilcoxon signed
	// rank test
	private int method;
	private String grp1;
	private String grp2;
	private int grp1Size;
	private int grp2Size;
	private String geneListName;
	private String dataTransform;
	private List<String> rowNames;
	private List<Double> meanGrp1;
	private List<Double> meanGrp2;
	private List<Double> logFC;
	private List<Double> fStat;
	private List<Double> fPval;
	private List<Double> pval;

	public DifferentialExpResults(int method, String grp1, String grp2, int grp1Size, int grp2Size, String geneListName,
			String dataTransform, List<String> rowNames, List<Double> meanGrp1, List<Double> meanGrp2,
			List<Double> logFC, List<Double> fStat, List<Double> fPval, List<Double> pval) {

		this.method = method;
		this.grp1 = grp1;
		this.grp2 = grp2;
		this.grp1Size = grp1Size;
		this.grp2Size = grp2Size;
		this.geneListName = geneListName;
		this.dataTransform = dataTransform;
		this.rowNames = rowNames;
		this.meanGrp1 = meanGrp1;
		this.meanGrp2 = meanGrp2;
		this.logFC = logFC;
		this.fStat = fStat;
		this.fPval = fPval;
		this.pval = pval;

	}

	// methods to retrieve data
	public String getmethodName() {
		// 1 MW U test; 2 t test; 3 Weltch t test; 4 Paired t test; 5 Wilcoxon signed
		if (method == 1) {
			return "M-W U test";
		}
		if (method == 2) {
			return "Student's t test";
		}
		if (method == 3) {
			return "Weltch's t test";
		}
		if (method == 4) {
			return "Paired t test";
		}
		if (method == 5) {
			return "Wilcoxon signed-rank test";
		}

		return "unknown";
	}

	public String getGrp1Name() {
		return grp1;
	}

	public String getGrp2Name() {
		return grp2;
	}

	public String getGeneListName() {
		return geneListName;
	}

	public String geDataTransform() {
		return dataTransform;
	}

	public int getGrp1Size() {
		return grp1Size;
	}

	public int getGrp2Size() {
		return grp2Size;
	}

	public List<String> getRowNames() {
		return rowNames;
	}

	public List<Double> getMeanGrp1() {
		return meanGrp1;
	}

	public List<Double> getMeanGrp2() {
		return meanGrp2;
	}

	public List<Double> getlogFC() {
		return logFC;
	}

	public List<Double> getfStat() {
		return fStat;
	}

	public List<Double> getFPVal() {
		return fPval;
	}

	public List<Double> getPVal() {
		return pval;
	}

}
