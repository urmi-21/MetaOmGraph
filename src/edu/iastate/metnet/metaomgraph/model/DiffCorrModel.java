package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

import edu.iastate.metnet.metaomgraph.DifferentialCorrResults;

public class DiffCorrModel {

	private String name;
	private List<DifferentialCorrResults> dcResults;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DifferentialCorrResults> getDcResults() {
		return dcResults;
	}
	public void setDcResults(List<DifferentialCorrResults> dcResults) {
		this.dcResults = dcResults;
	}
	
	
}
