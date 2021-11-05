package edu.iastate.metnet.metaomgraph.model;

public class ParamsModel {

	private String name;
	private String permutations;
	private String threads;
	private String srrColumn;
	private String srpColumn;
	private String srxColumn;
	private String srsColumn;
	private String gseColumn;
	private String gsmColumn;
	
	public ParamsModel(String name, String permutations, String threads, String srrColumn, String srpColumn,
			String srxColumn, String srsColumn, String gseColumn, String gsmColumn) {
		super();
		this.name = name;
		this.permutations = permutations;
		this.threads = threads;
		this.srrColumn = srrColumn;
		this.srpColumn = srpColumn;
		this.srxColumn = srxColumn;
		this.srsColumn = srsColumn;
		this.gseColumn = gseColumn;
		this.gsmColumn = gsmColumn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPermutations() {
		return permutations;
	}

	public void setPermutations(String permutations) {
		this.permutations = permutations;
	}

	public String getThreads() {
		return threads;
	}

	public void setThreads(String threads) {
		this.threads = threads;
	}

	public String getSrrColumn() {
		return srrColumn;
	}

	public void setSrrColumn(String srrColumn) {
		this.srrColumn = srrColumn;
	}

	public String getSrpColumn() {
		return srpColumn;
	}

	public void setSrpColumn(String srpColumn) {
		this.srpColumn = srpColumn;
	}

	public String getSrxColumn() {
		return srxColumn;
	}

	public void setSrxColumn(String srxColumn) {
		this.srxColumn = srxColumn;
	}

	public String getSrsColumn() {
		return srsColumn;
	}

	public void setSrsColumn(String srsColumn) {
		this.srsColumn = srsColumn;
	}

	public String getGseColumn() {
		return gseColumn;
	}

	public void setGseColumn(String gseColumn) {
		this.gseColumn = gseColumn;
	}

	public String getGsmColumn() {
		return gsmColumn;
	}

	public void setGsmColumn(String gsmColumn) {
		this.gsmColumn = gsmColumn;
	}
	
	
	
	
}
