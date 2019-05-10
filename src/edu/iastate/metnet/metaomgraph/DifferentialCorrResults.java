package edu.iastate.metnet.metaomgraph;

import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.iastate.metnet.metaomgraph.utils.Utils;

public class DifferentialCorrResults {

	private String geneList;
	private String featureName;
	private int featureID;
	private List<String> grp1;
	private List<String> grp2;
	private String grp1Name;
	private String grp2Name;
	private int method;
	private List<String> featureNames;
	private List<Double> corrGrp1;
	private List<Double> corrGrp2;
	private String id;
	String dataTransform;

	public DifferentialCorrResults(String geneList, String featureName, int featureID, List<String> grp1,
			List<String> grp2, String grp1Name, String grp2Name, int method, List<String> featureNames,
			List<Double> corrGrp1, List<Double> corrGrp2, String dataTransform, String id) {

		this.geneList = geneList;
		this.featureName = featureName;
		this.featureID = featureID;
		this.grp1 = grp1;
		this.grp2 = grp2;
		this.grp1Name = grp1Name;
		this.grp2Name = grp2Name;
		this.method = method;
		this.featureNames = featureNames;
		this.corrGrp1 = corrGrp1;
		this.corrGrp2 = corrGrp2;
		this.dataTransform = dataTransform;
		this.id =  Utils.removeSpecialChars(id);

	}
	
	public String getID() {
		return this.id;
	}

	public String getGeneListName() {
		return geneList;
	}

	public String getfetureName() {
		return featureName;
	}

	public String getGrp1Name() {
		return grp1Name;
	}

	public String getGrp2Name() {
		return grp2Name;
	}

	public int getfeatureID() {
		return featureID;
	}

	public String getMethod() {
		return "Fisher Transform";
	}

	public List<String> getGrp1Samples() {
		return grp1;
	}

	public List<String> getGrp2Samples() {
		return grp2;
	}

	public List<Double> getCorrGrp1() {
		return corrGrp1;
	}

	public List<Double> getCorrGrp2() {
		return corrGrp2;
	}

	public List<String> getFeatureNames() {
		return featureNames;
	}

	public int getGrp1Size() {
		return grp1.size();
	}

	public int getGrp2Size() {
		return grp2.size();
	}

	// return this object in XML format
	public Element getAsXMLNode() {

		Element res = new Element(id);

		// add attributes
		res.setAttribute("FeatureName", featureName);
		res.setAttribute("FeatureIndex", String.valueOf(featureID));
		res.setAttribute("method", String.valueOf(method));
		res.setAttribute("Group1", grp1Name);
		res.setAttribute("Group2", grp2Name);
		res.setAttribute("FeatureList", geneList);
		res.setAttribute("DataTransform", dataTransform);

		// add featureNames
		Element rowName = new Element("rownames");
		for (int i = 0; i < featureNames.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(featureNames.get(i));
			rowName.addContent(thisVal);
		}

		// add grp1 samples
		Element grp1Samples = new Element("grp1Samples");
		for (int i = 0; i < grp1.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(grp1.get(i)));
			grp1Samples.addContent(thisVal);
		}

		// add grp2 samples
		Element grp2Samples = new Element("grp2Samples");
		for (int i = 0; i < grp2.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(grp2.get(i)));
			grp2Samples.addContent(thisVal);
		}

		// add corrgrp1
		Element grp1 = new Element("grp1Corr");
		for (int i = 0; i < corrGrp1.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(corrGrp1.get(i)));
			grp1.addContent(thisVal);
		}

		// add corrgrp2
		Element grp2 = new Element("grp2Corr");
		for (int i = 0; i < corrGrp2.size(); i++) {
			Element thisVal = new Element("value");
			thisVal.addContent(String.valueOf(corrGrp2.get(i)));
			grp2.addContent(thisVal);
		}

		res.addContent(rowName);
		res.addContent(grp1Samples);
		res.addContent(grp2Samples);
		res.addContent(grp1);
		res.addContent(grp2);

		// print
		/*
		 * XMLOutputter outter = new XMLOutputter();
		 * outter.setFormat(Format.getPrettyFormat()); String resDoc =
		 * outter.outputString(res); JOptionPane.showMessageDialog(null, resDoc);
		 */
		return res;
	}

}
