package edu.iastate.metnet.metaomgraph.model;

public class QueryModel {

	private String matchAll;
	private String field;
	private String term;
	
	public String getMatchAll() {
		return matchAll;
	}
	public void setMatchAll(String matchAll) {
		this.matchAll = matchAll;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
	
}
