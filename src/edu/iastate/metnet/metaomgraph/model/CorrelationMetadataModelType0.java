package edu.iastate.metnet.metaomgraph.model;

public class CorrelationMetadataModelType0 extends CorrelationMetadataModel{

	private String name;
	private String value;
	private String pvalue;
	private String zval;
	private String qval;
	private String pooledzr;
	private String stderr;
	
	public CorrelationMetadataModelType0(String name, String value, String pvalue, String zval, String qval,
			String pooledzr, String stderr) {
		super(name, value, pvalue);
		
		this.name = name;
		this.value = value;
		this.pvalue = pvalue;
		this.zval = zval;
		this.qval = qval;
		this.pooledzr = pooledzr;
		this.stderr = stderr;
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

	public String getZval() {
		return zval;
	}

	public void setZval(String zval) {
		this.zval = zval;
	}

	public String getQval() {
		return qval;
	}

	public void setQval(String qval) {
		this.qval = qval;
	}

	public String getPooledzr() {
		return pooledzr;
	}

	public void setPooledzr(String pooledzr) {
		this.pooledzr = pooledzr;
	}

	public String getStderr() {
		return stderr;
	}

	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	
}
