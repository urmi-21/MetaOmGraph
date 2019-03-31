package edu.iastate.metnet.metaomgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Class to store results of a differential expression analysis
 * 
 * @author urmi
 *
 */

public class DifferentialExpResults {

	// method used for the analysis
	// 0 MW U test; 1 t test; 2 Weltch t test; 3 Paired t test; 4 Wilcoxon signed
	// rank test
	private int method;
	private String grp1;
	private String grp2;
	private int grp1Size;
	private int grp2Size;
	private String featureListName;
	private String dataTransform;
	private List<String> rowNames;
	private List<Double> meanGrp1;
	private List<Double> meanGrp2;
	private List<Double> logFC;
	private List<Double> fStat;
	private List<Double> fPval;
	private List<Double> pval;
	private String id;

	public DifferentialExpResults(String id, int method, String grp1, String grp2, int grp1Size, int grp2Size,
			String featureListName, String dataTransform, List<String> rowNames, List<Double> meanGrp1,
			List<Double> meanGrp2, List<Double> fStat, List<Double> fPval, List<Double> pval) {
		this.id = id;
		this.method = method;
		this.grp1 = grp1;
		this.grp2 = grp2;
		this.grp1Size = grp1Size;
		this.grp2Size = grp2Size;
		this.featureListName = featureListName;
		this.dataTransform = dataTransform;
		this.rowNames = rowNames;
		this.meanGrp1 = meanGrp1;
		this.meanGrp2 = meanGrp2;
		this.logFC = calculatelogFC();
		this.fStat = fStat;
		this.fPval = fPval;
		this.pval = pval;

	}

	// calculate log FC
	private List<Double> calculatelogFC() {
		List<Double> res = new ArrayList<>();
		for (int i = 0; i < meanGrp1.size(); i++) {
			res.add(meanGrp1.get(i) - meanGrp2.get(i));
		}
		return res;
	}

	// methods to retrieve data
	public String getID() {
		return this.id;
	}

	public String getmethodName() {
		// 0 MW U test; 1 t test; 2 Weltch t test; 3 Paired t test; 4 Wilcoxon signed
		if (method == 0) {
			return "M-W U test";
		}
		if (method == 1) {
			return "Student's t test";
		}
		if (method == 2) {
			return "Weltch's t test";
		}
		if (method == 3) {
			return "Paired t test";
		}
		if (method == 4) {
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
		return featureListName;
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

	// return this object in XML format
	public Element getAsXMLNode() {
		
		Element res = new Element(id);
		
		//add attributes
		res.setAttribute("method", String.valueOf(method));
		res.setAttribute("Group1", grp1);
		res.setAttribute("Group2", grp2);
		res.setAttribute("Group1Size", String.valueOf(grp1Size));
		res.setAttribute("Group2Size", String.valueOf(grp2Size));
		res.setAttribute("FeatureList", featureListName);
		res.setAttribute("DataTransform", dataTransform);
		
		
		// add rownames
		Element rowName = new Element("rownames");
		for (int i = 0; i < rowNames.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(rowNames.get(i));
			rowName.addContent(thisVal);
		}

		// add meangrp1
		Element grp1 = new Element("grp1");
		for (int i = 0; i < meanGrp1.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(meanGrp1.get(i)));
			grp1.addContent(thisVal);
		}

		// add meangrp2
		Element grp2 = new Element("grp2");
		for (int i = 0; i < meanGrp2.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(meanGrp2.get(i)));
			grp2.addContent(thisVal);
		}

		// add logfc
		Element logfc = new Element("logfc");
		for (int i = 0; i < logFC.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(logFC.get(i)));
			logfc.addContent(thisVal);
		}

		// add fstat
		Element fstat = new Element("fstat");
		if (fstat != null) {
			for (int i = 0; i < fStat.size(); i++) {
				Element thisVal = new Element("value");
				thisVal.addContent(String.valueOf(fStat.get(i)));
				fstat.addContent(thisVal);
			}
		}

		// add fPval
		Element fpval = new Element("fpval");
		if (fPval != null) {
			for (int i = 0; i < fPval.size(); i++) {
				Element thisVal = new Element("value");
				thisVal.addContent(String.valueOf(fPval.get(i)));
				fpval.addContent(thisVal);
			}
		}

		// add Pval
		Element pVal = new Element("pval");
		for (int i = 0; i < pval.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(pval.get(i)));
			pVal.addContent(thisVal);
		}

		res.addContent(rowName);
		res.addContent(grp1);
		res.addContent(grp2);
		res.addContent(logfc);
		res.addContent(fstat);
		res.addContent(fpval);
		res.addContent(pVal);

		// print
		/*
		 * XMLOutputter outter = new XMLOutputter();
		 * outter.setFormat(Format.getPrettyFormat()); String resDoc =
		 * outter.outputString(res); JOptionPane.showMessageDialog(null, resDoc);
		 */
		return res;
	}
}
