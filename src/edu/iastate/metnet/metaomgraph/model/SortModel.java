package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class SortModel {

	private String name;
	private String order;
	private List<RangeMarkerModel> rangeMarkers;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public List<RangeMarkerModel> getRangeMarkers() {
		return rangeMarkers;
	}
	public void setRangeMarkers(List<RangeMarkerModel> rangeMarkers) {
		this.rangeMarkers = rangeMarkers;
	}
	
	
}
