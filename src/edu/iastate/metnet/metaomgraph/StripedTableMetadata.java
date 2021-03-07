package edu.iastate.metnet.metaomgraph;

import java.util.Map;

import javax.swing.table.TableColumn;

public class StripedTableMetadata {

	private Map<TableColumn,Boolean> columnVisibilityMap;

	public Map<TableColumn, Boolean> getColumnVisibilityMap() {
		return columnVisibilityMap;
	}

	public void setColumnVisibilityMap(Map<TableColumn, Boolean> columnVisibilityMap) {
		this.columnVisibilityMap = columnVisibilityMap;
	}
	
}
