package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class MetadataTreeModel {

	private String root;
	private List<String> children;
	
	public MetadataTreeModel(String root, List<String> children) {
		super();
		this.root = root;
		this.children = children;
	}
	
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public List<String> getChildren() {
		return children;
	}
	public void setChildren(List<String> children) {
		this.children = children;
	}
	
	
}
