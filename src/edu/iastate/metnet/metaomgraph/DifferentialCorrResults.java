package edu.iastate.metnet.metaomgraph;

import java.util.List;

public class DifferentialCorrResults {

	private String geneList;
	private String featureName;
	private int featureID;
	private List<String> grp1;
	private List<String> grp2;
	private String grp1Name;
	private String grp2Name;
	private int method;

	public DifferentialCorrResults(String geneList, String featureName, int featureID, List<String> grp1,
			List<String> grp2, String grp1Name, String grp2Name, int method) {
		
		this.geneList=geneList;
		this.featureName=featureName;
		this.featureID=featureID;
		this.grp1=grp1;
		this.grp2=grp2;
		this.grp1Name=grp1Name;
		this.grp2Name=grp2Name;
		this.method=method;

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
	
	public List<String> getGrp1Samples(){
		return grp1;
	}
	
	public List<String> getGrp2Samples(){
		return grp2;
	}
}
