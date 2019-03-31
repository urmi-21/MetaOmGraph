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
	private String geneListName;
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
			String geneListName, String dataTransform, List<String> rowNames, List<Double> meanGrp1,
			List<Double> meanGrp2, List<Double> fStat, List<Double> fPval, List<Double> pval) {
		this.id = id;
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

	// return this object in XML format
	public Element getAsXMLNode() {
		Element res = new Element(id);
		//add rownames
		Element rowName=new Element("rownames");
		for(int i=0;i<rowNames.size();i++) {
			Element thisVal=new Element("value");
			thisVal.addContent(rowNames.get(i));
			rowName.addContent(thisVal);
		}

		res.addContent(rowName);
		
		//print
		XMLOutputter outter = new XMLOutputter();
		outter.setFormat(Format.getPrettyFormat());
		String resDoc = outter.outputString(res);
		JOptionPane.showMessageDialog(null, resDoc);
		
				
		return res;
	}
}
