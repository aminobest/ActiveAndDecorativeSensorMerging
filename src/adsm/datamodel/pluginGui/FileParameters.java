package adsm.datamodel.pluginGui;

import adsm.core.plugin.CSVLog;

public class FileParameters {
	String filename = "" ;
	String type ;
	String selectionRatio ;
	String caseIdIndex ;
	String StarttimeIndex ;
	String endttimeIndex ;
	String discreteattr ;
	String continousattr ;
	String marking ;
	CSVLog csvfile ;
	String activityIndex ;
	
    @Override
    public String toString()
    {
        return filename;
    }

    
	
	public String getActivityIndex() {
		return activityIndex;
	}



	public void setActivityIndex(String activityIndex) {
		this.activityIndex = activityIndex;
	}



	public CSVLog getCsvfile() {
		return csvfile;
	}



	public void setCsvfile(CSVLog csvfile) {
		this.csvfile = csvfile;
	}



	public String getMarking() {
		return marking;
	}



	public void setMarking(String marking) {
		this.marking = marking;
	}



	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getType() {
		return type;
	}
	public String getSelectionRatio() {
		return selectionRatio;
	}
	public String getCaseIdIndex() {
		return caseIdIndex;
	}
	public String getStarttimeIndex() {
		return StarttimeIndex;
	}
	public String getEndttimeIndex() {
		return endttimeIndex;
	}
	public String getDiscreteattr() {
		return discreteattr;
	}
	public String getContinousattr() {
		return continousattr;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSelectionRatio(String selectionRatio) {
		this.selectionRatio = selectionRatio;
	}
	public void setCaseIdIndex(String caseIdIndex) {
		this.caseIdIndex = caseIdIndex;
	}
	public void setStarttimeIndex(String starttimeIndex) {
		StarttimeIndex = starttimeIndex;
	}
	public void setEndttimeIndex(String endttimeIndex) {
		this.endttimeIndex = endttimeIndex;
	}
	public void setDiscreteattr(String discreteattr) {
		this.discreteattr = discreteattr;
	}
	public void setContinousattr(String continousattr) {
		this.continousattr = continousattr;
	}
	
	
}
  
