package edu.iastate.metnet.metaomgraph.model;

public class MetadataModel {

	private String FILEPATH;
	private String DELIMITER;
	private String DATACOL;
	private String[] COLORDER;
	
	public MetadataModel( String fILEPATH, String dELIMITER, String dATACOL, String[] ColOrder) {
		super();
		FILEPATH = fILEPATH;
		DELIMITER = dELIMITER;
		DATACOL = dATACOL;
		COLORDER = ColOrder;
	}

	public String getFILEPATH() {
		return FILEPATH;
	}
	public void setFILEPATH(String fILEPATH) {
		FILEPATH = fILEPATH;
	}
	public String getDELIMITER() {
		return DELIMITER;
	}
	public void setDELIMITER(String dELIMITER) {
		DELIMITER = dELIMITER;
	}
	public String getDATACOL() {
		return DATACOL;
	}
	public void setDATACOL(String dATACOL) {
		DATACOL = dATACOL;
	}

	public String[] getCOLORDER() {
		return COLORDER;
	}

	public void setCOLORDER(String[] cOLORDER) {
		COLORDER = cOLORDER;
	}
	
	
	
}
