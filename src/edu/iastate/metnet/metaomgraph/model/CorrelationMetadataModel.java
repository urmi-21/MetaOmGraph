package edu.iastate.metnet.metaomgraph.model;

public class CorrelationMetadataModel {

	private String name;
	private String value;
	private String pvalue;
	
	public CorrelationMetadataModel(String name, String value, String pvalue) {
		super();
		this.name = name;
		this.value = value;
		this.pvalue = pvalue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPvalue() {
		return pvalue;
	}

	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}
	
}
