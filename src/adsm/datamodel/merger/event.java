package adsm.datamodel.merger;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import adsm.core.utils.DateUtil;

public class event implements Comparable<event> {

	String caseid ;
	String eventid ;
    String starttime ;
    String endtime ;
   
    ArrayList<String> otherattributes ;
    
 
    
    public event(String caseid ,String eventid, String starttime, String endtime, ArrayList<String> otherattributes){
    	this.eventid = eventid ;
    	this.starttime = starttime ;
    	this.endtime = endtime ;
    	
    	this.otherattributes = otherattributes ;
    	
    	this.caseid = caseid ;
       
    }

    
    
    





	public void addDecorativeAttribute(String attr){
    	otherattributes.add(attr);
    }
    
    private  long getTimestamp(String time) {
    	
    	long  timestamp = 0 ;
		
    Date date;
	try {
		date = DateUtil.parse(time);
		Calendar cal = toCalendar(date);
		 timestamp = cal.getTimeInMillis();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
		return timestamp ;
	}
    
	public ArrayList<String> getRelevantAttributes() {

		ArrayList<String> all = new ArrayList<String>();
	
		all.addAll(otherattributes);

		return all;

	}
	
	public static Calendar toCalendar(Date date){ 
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
		}
	
	
	public ArrayList<String> getAllAttributes() {

		ArrayList<String> all = new ArrayList<String>();
		all.add(caseid);
		all.add(eventid) ;
		all.add(starttime) ;
		all.add(endtime) ;
		
		all.addAll(otherattributes);

		return all;

	}
    
   @Override
    public int compareTo(event e){
        return (int) (this.getTimestamp(this.starttime) - this.getTimestamp(e.getStarttime())) ;
    }

    public String getEventid() {
		
		return eventid ;
	}


	public String getStarttime() {
		return starttime;
	}


	public String getEndtime() {
		return endtime;
	}








	public ArrayList<String> getOtherattributes() {
		return otherattributes;
	}


	public void setEventid(String eventid) {
		this.eventid = eventid;
	}


	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}


	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}





	public void setOtherattributes(ArrayList<String> otherattributes) {
		this.otherattributes = otherattributes;
	}


	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}

	public String getCaseid() {
		
		return caseid;
	}



	


	
	
    
    
}
