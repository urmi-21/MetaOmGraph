package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class QuerySetModel {

	private String name;
	private String matchAll;
	private List<QueryModel> queries;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMatchAll() {
		return matchAll;
	}
	public void setMatchAll(String matchAll) {
		this.matchAll = matchAll;
	}
	public List<QueryModel> getQueries() {
		return queries;
	}
	public void setQueries(List<QueryModel> queries) {
		this.queries = queries;
	}
	
	
}
