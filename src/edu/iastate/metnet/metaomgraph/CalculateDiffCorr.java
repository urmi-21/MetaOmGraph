package edu.iastate.metnet.metaomgraph;

import java.util.List;

public class CalculateDiffCorr {
	
	private String featureName;
	private int featureIndex;
	private List<String> grp1;
	private List<String> grp2;
	private String g1name;
	private String g2name;
	private MetaOmProject myProject;
	private int method;

	
	public CalculateDiffCorr(String string,int featureInd, List<String> grp1, List<String> grp2, String name1, String name2, MetaOmProject myProject, int m) {
		
		featureName=string;
		featureIndex=featureInd;
		this.grp1=grp1;
		this.grp2=grp2;
		g1name=name1;
		g2name=name2;
		this.myProject=myProject;
		method=m;
		
		
	}
	
	
	
}
