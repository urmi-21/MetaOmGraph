package edu.iastate.metnet.metaomgraph.model;

import java.util.List;

public class ProjectFileModel {

	private String sourcePath;
	private String sourceFile;
	private String delimiter;
	private String ignoreConsecutiveDelimiters;
	private String blankValue;
	private String xLabel;
	private String yLabel;
	private String title;
	private String color1;
	private String color2;
	private String defaultColumn;
	private List<String> infoColumns;
	private List<String> columns;
	private List<DataModel> data;
	private List<SampleDataListModel> sampleDataLists;
	private List<ListModel> lists;
	private List<SortModel> sorts;
	private List<QuerySetModel> queries;
	private List<ExcludesModel> excludes;
	
	
	public String getSourcePath() {
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public String getIgnoreConsecutiveDelimiters() {
		return ignoreConsecutiveDelimiters;
	}
	public void setIgnoreConsecutiveDelimiters(String ignoreConsecutiveDelimiters) {
		this.ignoreConsecutiveDelimiters = ignoreConsecutiveDelimiters;
	}
	public String getBlankValue() {
		return blankValue;
	}
	public void setBlankValue(String blankValue) {
		this.blankValue = blankValue;
	}
	public String getxLabel() {
		return xLabel;
	}
	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
	}
	public String getyLabel() {
		return yLabel;
	}
	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getColor1() {
		return color1;
	}
	public void setColor1(String color1) {
		this.color1 = color1;
	}
	public String getColor2() {
		return color2;
	}
	public void setColor2(String color2) {
		this.color2 = color2;
	}
	public String getDefaultColumn() {
		return defaultColumn;
	}
	public void setDefaultColumn(String defaultColumn) {
		this.defaultColumn = defaultColumn;
	}
	public List<String> getInfoColumns() {
		return infoColumns;
	}
	public void setInfoColumns(List<String> infoColumns) {
		this.infoColumns = infoColumns;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	public List<DataModel> getData() {
		return data;
	}
	public void setData(List<DataModel> data) {
		this.data = data;
	}
	public List<ListModel> getLists() {
		return lists;
	}
	public void setLists(List<ListModel> lists) {
		this.lists = lists;
	}
	public List<SortModel> getSorts() {
		return sorts;
	}
	public void setSorts(List<SortModel> sorts) {
		this.sorts = sorts;
	}
	public List<QuerySetModel> getQueries() {
		return queries;
	}
	public void setQueries(List<QuerySetModel> queries) {
		this.queries = queries;
	}
	public List<ExcludesModel> getExcludes() {
		return excludes;
	}
	public void setExcludes(List<ExcludesModel> excludes) {
		this.excludes = excludes;
	}
	public List<SampleDataListModel> getSampleDataLists() {
		return sampleDataLists;
	}
	public void setSampleDataLists(List<SampleDataListModel> sampleDataLists) {
		this.sampleDataLists = sampleDataLists;
	}

	
	
}
