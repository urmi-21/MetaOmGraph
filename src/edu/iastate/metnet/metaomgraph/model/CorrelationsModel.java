package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class CorrelationsModel {

	private String root;
	private List<CorrelationNodeModel> correlations;

	
	public CorrelationsModel(String root, List<CorrelationNodeModel> correlations) {
		super();
		this.root = root;
		this.correlations = correlations;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public List<CorrelationNodeModel> getCorrelations() {
		return correlations;
	}

	public void setCorrelations(List<CorrelationNodeModel> correlations) {
		this.correlations = correlations;
	}
	
	
	
}
