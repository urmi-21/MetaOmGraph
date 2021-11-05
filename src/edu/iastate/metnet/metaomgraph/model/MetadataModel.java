package edu.iastate.metnet.metaomgraph.model;

public class MetadataModel {

	private String FILEPATH;
	private String DELIMITER;
	private String DATACOL;
	
	public MetadataModel( String fILEPATH, String dELIMITER, String dATACOL) {
		super();
		FILEPATH = fILEPATH;
		DELIMITER = dELIMITER;
		DATACOL = dATACOL;
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
	
	
	
}
