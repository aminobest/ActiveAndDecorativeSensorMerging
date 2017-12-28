package adsm.datamodel.merger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class logcase {

	ArrayList<event> events ;
	String caseId ;

	public logcase(String caseId) {
		this.events = new ArrayList<event>();
		this.caseId = caseId ;
	}
	
	
	public void AddnewEvent(event e){
		
	     events.add(e);
	}

	
	public void AddnewAllEvents(logcase l){
		for(event e : l.getEvents()){
		
			
			
			events.add(e);
		}
	}
	

	public ArrayList<event> getEvents() {
		return events;
	}


	public String getCaseId() {
		return caseId;
	}

	public void sort(){
		Collections.sort(events);
	}
	
	public String getCaseString() {
		String stringval = "";
		
		for(event e : events){
			for(String s : e.getRelevantAttributes() ){
				stringval += s+" ";
			}
		}
		
		return stringval ;
	}
	
	
	
}
