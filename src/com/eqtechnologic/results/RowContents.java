package com.eqtechnologic.results;

public class RowContents{
	public String runId;
	public String suitName;
	public String tcName;
	public String result;
	
	public RowContents(String runId, String suitName, String tcName, String result) {
		this.runId = runId;
		this.suitName = suitName;
		this.tcName = tcName;
		this.result = result;
	}
	@Override
	public String toString() {
		return runId+" "+suitName+" "+tcName+" "+result;
	}
	
}