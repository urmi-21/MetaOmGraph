package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

import edu.iastate.metnet.metaomgraph.CorrelationMeta;

public class CorrelationNodeModel {

	private String corrName;
	private String corrtype;
	private String corrmodel;
	private String corrvar;
	private String bins;
	private String order;
	private List<CorrelationMetadataModel> corrList;
	
	
	public String getCorrName() {
		return corrName;
	}

	public void setCorrName(String corrName) {
		this.corrName = corrName;
	}

	public String getCorrtype() {
		return corrtype;
	}

	public void setCorrtype(String corrtype) {
		this.corrtype = corrtype;
	}

	public String getCorrmodel() {
		return corrmodel;
	}

	public void setCorrmodel(String corrmodel) {
		this.corrmodel = corrmodel;
	}

	public String getCorrvar() {
		return corrvar;
	}

	public void setCorrvar(String corrvar) {
		this.corrvar = corrvar;
	}

	public String getBins() {
		return bins;
	}

	public void setBins(String bins) {
		this.bins = bins;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<CorrelationMetadataModel> getCorrList() {
		return corrList;
	}

	public void setCorrList(List<CorrelationMetadataModel> corrList) {
		this.corrList = corrList;
	}
	
	
	
}
