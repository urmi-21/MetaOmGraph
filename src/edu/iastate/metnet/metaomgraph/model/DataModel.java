package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class DataModel {

	private List<InfoModel> info;
	private String location;
	
	public List<InfoModel> getInfo() {
		return info;
	}
	public void setInfo(List<InfoModel> info) {
		this.info = info;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	
}
