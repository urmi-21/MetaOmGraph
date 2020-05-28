package edu.iastate.metnet.metaomgraph.logging;

public class Parameters {
	private String parameterName;
	private String parameterValue;
	
	public Parameters(String parameterName, String parameterValue) {
		super();
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	
	@Override
	public String toString() {
		return "{\""+parameterName+"\":\""+parameterValue+"\"}";
	}
}
