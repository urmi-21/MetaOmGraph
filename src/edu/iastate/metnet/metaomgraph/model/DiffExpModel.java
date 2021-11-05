package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

import edu.iastate.metnet.metaomgraph.DifferentialExpResults;

public class DiffExpModel {
	
	private String name;
	private List<DifferentialExpResults> deResults;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DifferentialExpResults> getDeResults() {
		return deResults;
	}
	public void setDeResults(List<DifferentialExpResults> deResults) {
		this.deResults = deResults;
	}
	
	

}
