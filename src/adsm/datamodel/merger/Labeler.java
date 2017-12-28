package adsm.datamodel.merger;

import java.util.ArrayList;
import java.util.HashMap;

public class Labeler {
    int caseIdIndex ;
    int startTimeIndex ;
    int endTimeIndex ;
  
    ArrayList<Integer> OtherAttributes ;
    HashMap<String, ArrayList<String>> header ;
    
    
    
    public ArrayList<Integer> getAllIndexes(){
    	
    	ArrayList<Integer> all = new ArrayList<Integer>();
    	all.add(caseIdIndex);
    	all.add(startTimeIndex);
    	all.add(endTimeIndex);
    
    	all.addAll(OtherAttributes); /// otherattributes added in this version
  
    	
    	return all ;
    	
    }



	public int getCaseIdIndex() {
		return caseIdIndex;
	}



	public int getStartTimeIndex() {
		return startTimeIndex;
	}



	public int getEndTimeIndex() {
		return endTimeIndex;
	}



	



	public ArrayList<Integer> getOtherAttributes() {
		return OtherAttributes;
	}



	public HashMap<String, ArrayList<String>> getHeader() {
		return header;
	}



	public void setCaseIdIndex(int caseIdIndex) {
		this.caseIdIndex = caseIdIndex;
	}



	public void setStartTimeIndex(int startTimeIndex) {
		this.startTimeIndex = startTimeIndex;
	}



	public void setEndTimeIndex(int endTimeIndex) {
		this.endTimeIndex = endTimeIndex;
	}







	public void setOtherAttributes(ArrayList<Integer> otherAttributes) {
		OtherAttributes = otherAttributes;
	}



	public void setHeader(HashMap<String, ArrayList<String>> header) {
		this.header = header;
	}
    
    
    
    
}
