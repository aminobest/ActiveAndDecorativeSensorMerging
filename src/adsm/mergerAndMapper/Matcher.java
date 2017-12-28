package adsm.mergerAndMapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import adsm.core.plugin.CSVLog;
import adsm.core.utils.DateUtil;
import adsm.datamodel.merger.DecorativeAttribute;
import adsm.datamodel.merger.DecorativeEntry;
import adsm.datamodel.merger.Labeler;
import adsm.datamodel.merger.MergableLog;
import adsm.datamodel.merger.event;
import adsm.datamodel.merger.log;
import adsm.datamodel.merger.logcase;
import adsm.mergerAndMapper.utils.Debug;
import adsm.mergerAndMapper.utils.Debuger;
import adsm.mergerAndMapper.utils.utils;

public class Matcher {

	 
	  HashMap<String, HashMap<String, String>> attributesIndexes ;  
	  String separator ;
	  ArrayList<String> newids;
	  public Debuger deb ;
	  
	  
	  public  Matcher(int maxcases, HashMap<String, HashMap<String, String>> attributesIndexes , String separator) throws IOException{
		
		  this.attributesIndexes = attributesIndexes;
		  this.separator = separator ;
		  newids = new ArrayList<String>();
		  generateNewCaseIds(maxcases);
		  deb = new Debuger("debuglog");
		  
		
		  
	  }
	
		public void generateNewCaseIds(int maxcases){
	    	 for(int i=0;i<maxcases;i++){
	 			newids.add(""+i);
	 		}
	 		Collections.shuffle(newids);
	     }
	  
	  
		public static String getMethodName(final int depth)
		{
		  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		  return ste[ste.length - 1 - depth].getMethodName(); 
		}
		
	  public HashMap<logcase, log > generateTemporalRelationsPairs(log masterlog, log slavelog) throws ParseException, IOException {

		  deb.writeTrace(new Debug(){{
			    setClassname(this.getClass().getSimpleName());
				setMethod(getMethodName(2));
				setParam1("Master Log");
    			setValue1(masterlog.getLogname());
    			setParam2("Slave Log");
    			setValue2(slavelog.getLogname());
				 }});
		  
			HashMap<logcase, log > selection = new HashMap<logcase, log > ();
			
			int counter = 0;
			Iterator it = masterlog.getCases().entrySet().iterator();
			while (it.hasNext()) {
				counter++;
				
				HashMap.Entry pair = (HashMap.Entry) it.next();
				logcase mastercase = (logcase) pair.getValue();
				String id = (String) pair.getKey();
				
			
				selection.put(mastercase, checkTemporalRelationPairsInLogs(mastercase, slavelog));

			}

			
			return selection ;
			
		}
		
		public log checkTemporalRelationPairsInLogs(logcase mastercase, log slavelog) throws ParseException, IOException{

			 log selection = new log("selection for"+mastercase.getCaseId());

		    	    deb.writeTrace(new Debug(){{
	    		    setClassname(this.getClass().getSimpleName());
	    			setMethod(getMethodName(2));
	    			setTitle("Checking temporal relations");
	    			setParam1("mastercase");
	    			setValue1(mastercase.getCaseId());
	    			setParam2("slavelog");
	    			setValue2(slavelog.getLogname());
	    	         }});
			 
			mastercase.sort();
			event master_firstevent = mastercase.getEvents().get(0);
			event master_lastevent = mastercase.getEvents().get(mastercase.getEvents().size()-1);	
	    	String master_caseStartTime = master_firstevent.getStarttime() ;
	    	String master_caseEndTime = master_lastevent.getEndtime();
			
			// iterate over the slave log
			   Iterator it = slavelog.getCases().entrySet().iterator();  
			    while (it.hasNext()) {    
			    	HashMap.Entry pair = (HashMap.Entry)it.next();
			    	
			    	logcase condidateCase = slavelog.getCases().get(pair.getKey());
			        
			    	// check the temporal rules for the condidate case
			    	
			    	condidateCase.sort();
			    	

			    	event condidate_firstevent = condidateCase.getEvents().get(0);
			    	event condidate_lastevent =  condidateCase.getEvents().get( condidateCase.getEvents().size()-1);	
			    	

			    	
			    	String condidate_caseStartTime = condidate_firstevent.getStarttime() ;
			    	String condidate_caseEndTime = condidate_lastevent.getEndtime();
			    	
			        if(CasesOverlap(master_caseStartTime, master_caseEndTime, condidate_caseStartTime, condidate_caseEndTime ) || SlaveDuringMaster(master_caseStartTime, master_caseEndTime, condidate_caseStartTime, condidate_caseEndTime ) || SlaveAfterMasterButEndAtsameTime(master_caseStartTime, master_caseEndTime, condidate_caseStartTime, condidate_caseEndTime ) ){
	  
			    	String candidateCaseid = condidateCase.getCaseId();
			    	String masterCaseid = mastercase.getCaseId();
			    	
			    	selection.AddnewCase(condidateCase.getCaseId(), condidateCase);
			    	
			    	//system.out.println("Master event, candiate event pair ("+masterCaseid+","+candidateCaseid+")");
			    	
			    	deb.writeTrace(new Debug(){{
		    		    setClassname(this.getClass().getSimpleName());
		    			setMethod(getMethodName(2));
		    			setTitle("Selected Case");
		    			setParam1("Master Case id");
		    			setValue1(masterCaseid);
		    			setParam2("Candidate Case id");
		    			setValue2(candidateCaseid);
		    			setParam3("Master Case starts");
		    			setValue3(master_caseStartTime);
		    			setParam4("Master Case ends");
		    			setValue4(master_caseEndTime);
		    			setParam5("Candidate case starts");
		    			setValue5(condidate_caseStartTime);
		    			setParam6("Candidate case ends ");
		    			setValue6(condidate_caseEndTime); }});
	
			    }
			    	
			    }
			
			return selection ;
		
		} 
	  
		public  boolean CasesOverlap(String master_caseStartTime, String master_caseEndTime, String condidate_caseStartTime, String condidate_caseEndTime ) throws ParseException{
			
			if((getTimestamp(condidate_caseStartTime) > getTimestamp(master_caseStartTime) ) && (getTimestamp(condidate_caseStartTime) < getTimestamp(master_caseEndTime)) ) {
				return true ;
			}
			
			return false ;
		}
		
		public  boolean SlaveDuringMaster(String master_caseStartTime, String master_caseEndTime, String condidate_caseStartTime, String condidate_caseEndTime ) throws ParseException{
			
			if((getTimestamp(condidate_caseStartTime) > getTimestamp(master_caseStartTime) ) && (getTimestamp(condidate_caseEndTime) < getTimestamp(master_caseEndTime)) ) {
				return true ;
			}
			
			return false ;
		}
		
		public  boolean SlaveAfterMasterButEndAtsameTime(String master_caseStartTime, String master_caseEndTime, String condidate_caseStartTime, String condidate_caseEndTime ) throws ParseException{
			
			if((getTimestamp(condidate_caseStartTime) > getTimestamp(master_caseStartTime) ) && (getTimestamp(condidate_caseEndTime) == getTimestamp(master_caseEndTime)) ) {
				return true ;
			}
			
			return false ;
		}
		
		public  boolean timeInInterval(String candidateStart, String masterEvent1End, String masterEvent2Start) throws ParseException{
			if(getTimestamp(candidateStart)>=getTimestamp(masterEvent1End) && getTimestamp(candidateStart)<getTimestamp(masterEvent2Start) ){ // "<" may cause problem
				return true ;
			}
			else return false ;
		}
		
		private  long getTimestamp(String time) throws ParseException{
			
			Date date = DateUtil.parse(time);
			Calendar cal = toCalendar(date);
			long timestamp = cal.getTimeInMillis();
			return timestamp ;
		}
		
		public static Calendar toCalendar(Date date){ 
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(date);
			  return cal;
			}


	 public Labeler applyLabeling(String caseid, String startime, String endtime, ArrayList<String>header){
			
			Labeler Case = new Labeler();
			
			
			  int caseidIndex = caseid!=null ? Integer.parseInt(caseid) : -1 ;
			  int starttimeIndex = Integer.parseInt(startime);
			  int endtimeIndex =  Integer.parseInt(endtime)  ;
			
			 Case.setCaseIdIndex(caseidIndex);
			Case.setStartTimeIndex(starttimeIndex);
		   Case.setEndTimeIndex(endtimeIndex);

		
		   ArrayList<Integer> OtherAttributesIndex = new ArrayList<Integer>();
		   for(int i = 0 ; i<header.size() ; i++){
			  
			   if(i!= caseidIndex && i!=starttimeIndex && i!=endtimeIndex){
				   OtherAttributesIndex.add(i);
			   }
			   
		   }
		 

			Case.setOtherAttributes(OtherAttributesIndex);
		
			return Case ;
			
			
		}
	 
	 
	 private int eventid = 0 ;
	 
	 public int getAndIncEventId(){
		 eventid++;
			 return eventid ;
	 }
	 
	 public log processFile(CSVLog file,String type) throws IOException{

	
			log newlog = new log(file.getName());
		
			if(type.equals("decorative")){

				/// get continous attributes, and discrete attributes
				ArrayList<Integer > discreteIndexes = new ArrayList<Integer>() {{
					String discreteIndexesString[] =  attributesIndexes.get(file.getName()).get("discreteDimensions").split(",");
					for(String d : discreteIndexesString) add(Integer.valueOf(d));
				}};
				ArrayList<Integer > continiousIndexes = new ArrayList<Integer>() {{
					String continiousIndexesString[] =  attributesIndexes.get(file.getName()).get("continiousDimensions").split(",");
					for(String d : continiousIndexesString) add(Integer.valueOf(d));
				}};
				
				for(int i=0; i<file.getEntries() ; i++){
					 // parse data entry
					List<String> dataentry = file.getLine(i);
					
					// create decorative entry 
					DecorativeEntry dEntry = new DecorativeEntry();
					
					// create and add decorative attribute objects
					for(Integer attrindex : discreteIndexes){					
						String decotype = "discrete";
						String value = dataentry.get(attrindex);					
						DecorativeAttribute dAttr = new DecorativeAttribute(decotype,value);
						dEntry.addAttribute(dAttr);
					}
					// create and add continuous attribute objects
					for(Integer attrindex : continiousIndexes){					
						String decotype = "continious";
						String value = dataentry.get(attrindex);					
						DecorativeAttribute dAttr = new DecorativeAttribute(decotype,value);
						dEntry.addAttribute(dAttr);
					}
					
					newlog.addDecorativeEntry(dEntry);
				}
				

			}

			else {
				
				
			
			Labeler labeling = new Labeler();
			String endtime_Index ="";
			String caseid_Index =  attributesIndexes.get(file.getName()).get("caseid") ;
			String starttime_Index = attributesIndexes.get(file.getName()).get("starttime") ;
			if(attributesIndexes.get(file.getName()).get("type").equals("active")){
				endtime_Index = attributesIndexes.get(file.getName()).get("starttime") ;
			}
			else {
				endtime_Index = attributesIndexes.get(file.getName()).get("endtime") ;
			}

			
		 // set selectionRatio 
			
			newlog.setSelectionRatio(Double.valueOf(attributesIndexes.get(file.getName()).get("selectionRatio")));
			
			
			/*
			 *  apply labeling
			 */
			
			/// get header 
			
			ArrayList<String> Fullheader = new ArrayList<>(file.getHeaders().size());
			Fullheader.addAll(file.getHeaders());
			
			
			// apply labeling
			labeling =  applyLabeling(caseid_Index,starttime_Index,endtime_Index,Fullheader);
			
			// generate new header
			HashMap<String, ArrayList<String>> header = new HashMap<String, ArrayList<String>>();
			
			// case id
			ArrayList<String> attribute = new ArrayList<String>();
			if(labeling.getCaseIdIndex()==-1){
				attribute.add("Active Id");
			}
			else {
			
				attribute.add(Fullheader.get(labeling.getCaseIdIndex()));
			}
			header.put("caseid", attribute);
		
			// timestamps
			attribute = new ArrayList<String>();
			attribute.add(Fullheader.get(labeling.getStartTimeIndex()));
			attribute.add(Fullheader.get(labeling.getEndTimeIndex()));
			header.put("timestamps", attribute);
			// other attributes
			ArrayList<String> attribute4 = new ArrayList<String>();
			labeling.getOtherAttributes().stream().forEach(index -> attribute4.add(Fullheader.get(index)));
			header.put("otherattributes", attribute4);
			
			// save header
			labeling.setHeader(header);
			
			// prepare labeling for return
			newlog.setLabel(labeling);
			
			
			
			/*
			 * Parse data entries
			 */
			
			
			for(int i=0; i<file.getEntries() ; i++){
				
				ArrayList<String> dataentry = new ArrayList<>(file.getLine(i).size());
				dataentry.addAll((file.getLine(i)));
				
						String CurrentCaseId = "";
						if(labeling.getCaseIdIndex()==-1){
							 CurrentCaseId =  getAndIncEventId()+"";
						}
						else {
							 CurrentCaseId = dataentry.get(labeling.getCaseIdIndex());
						}
		        		
		   
		        		String eventid = getAndIncEventId()+"";
		        	    String starttime = dataentry.get(labeling.getStartTimeIndex());
		        	    String endtime = dataentry.get(labeling.getEndTimeIndex());
		        	    ArrayList<String> otherattributes = new ArrayList<String>();
		        		labeling.getOtherAttributes().stream().forEach(index -> { otherattributes.add(dataentry.get(index)); } ); 
		        

		        	    
		        		event newevent = new event(CurrentCaseId, eventid, starttime, endtime, otherattributes);
		        		
		
			        	
			        		if(!newlog.getCases().containsKey(CurrentCaseId)){
			        			
			        			logcase newcase = new logcase(CurrentCaseId);
			        			newcase.AddnewEvent(newevent);
			        			newlog.AddnewCase(newcase.getCaseId(),newcase);
			        			
			        			
			        		}
			        		else {
			        			logcase existingcase = newlog.getCases().get(CurrentCaseId);
			        			existingcase.AddnewEvent(newevent);
			   
			        		}
			}

			}
			
			
		        return newlog ;
		}

	 public double simTF(String pei, String pej, HashMap<String,Double> wpei, HashMap<String,Double> wpej){
		 double score = 0 ;
		pei = delSpaces(pei);
		pej = delSpaces(pej);
		
		ArrayList<String> alreadyChecked = new ArrayList<String>();
		
		
		 for(String wi : pei.split(" ")){
			 for(String wj : pej.split(" ")){
				 

				 
				 
				 if(wi.equals(wj) && !alreadyChecked.contains(wi)){

					
						 score = score + (wpei.get(wi) +wpei.get(wj) )/2 ;
					
					
					 alreadyChecked.add(wi);
				 }
			 }	 
		 }		 
		 return score ;
	 }
	 

	 
public  double simJA(String pei, String pej){
		
	 String[] apei = pei.split(" ");
	 String[] apej = pej.split(" ");
	 
	 
	
		 double score = 0 ;
		pei = delSpaces(pei);
		pej = delSpaces(pej);
	

	
		score = (double) intersection(apei,apej).length / (double) union(apei,apej).length ;
		
		 return score ;
	 }
	 
	 public String[] union(String[] set1, String[] set2){
		 
		  HashSet<String> unionSet = new HashSet<String>();
		 
		for(String s1 : set1 ){
			unionSet.add(s1);
		}
		for(String s2 : set2 ){
			unionSet.add(s2);
		}
		
		 return asStringH(unionSet);
	 }
	 
	 
	 public String[] intersection(String[] set1, String[] set2){
		 
		 ArrayList<String> intersectionSet = new  ArrayList<String>();
		 
		 for(String s1 : set1){
			 if(Arrays.asList(set2).contains(s1)) intersectionSet.add(s1);
		 }
		 
		 return  asStringA(intersectionSet);
	 }
	 
	 
	 public String[] asStringA(ArrayList<String> al){
		 
		 String[] ret = new String[al.size()];
		 
		 int counter = 0 ;
		 for(String val : al){
		
			 ret[counter] = val ;
			 
		counter++;
		 }
		 
		 return ret ;
		 
	 }
	 
 public String[] asStringH(HashSet<String> al){
		 
		 String[] ret = new String[al.size()];
		 
		 int counter = 0 ;
		 for(String val : al){
		
			 ret[counter] = val ;
			 
		counter++;
		 }
		 
		 return ret ;
		 
	 }
	 
	 public String removeDuplicates(String str){
		 
		 String withoutduplicates = "";
		 
		 for(String s: delSpaces(str).split(" ")){
			 if(!withoutduplicates.contains(s)){
				 withoutduplicates = withoutduplicates+" "+s;
			 }
		 }
		 
		 return withoutduplicates ;
	 }
	 
	
	 
	 public String delSpaces(String str){    //custom method to remove multiple space
	        StringBuilder sb=new StringBuilder();
	        for(String s: str.split(" ")){

	            if(!s.equals(""))        // ignore space
	             sb.append(s+" ");       // add word with 1 space

	        }
	        return new String(sb.toString());
	    }
	 


	public log FilterBySimilarityScore(HashMap<logcase, log> matchingByTime, String simfunction,
			HashMap<String, Double> weights_file1, HashMap<String, Double> weights_file2, String mainlogname, double candidateSelectionRatio) throws IOException {
		
		
		log mergedlog = new log(mainlogname+"_Merged");
		HashMap<logcase[], Double> candidateScores = new HashMap<logcase[], Double>();
		HashMap<logcase, ArrayList<Double>> scoresmap = new HashMap<logcase, ArrayList<Double>>();
		double totalscores = 0 ;
		int ncandidates = 0 ;
		
		deb.writeTrace(new Debug(){{
		    setClassname(this.getClass().getSimpleName());
			setMethod(getMethodName(2));
			setTitle("Filter by Sim score the hashmap from temporal log");
			setParam1("Similarity Function");
			setValue1(simfunction);
			setParam2("");
			setValue2("");
			setParam3("");
			setValue3("");
			setParam4("");
			setValue4("");
			setParam5("");
			setValue5("");
			setParam6("");
			setValue6(""); }});
		
		  Iterator it  = matchingByTime.entrySet().iterator();
	      while (it.hasNext()) {
	    	  
	    	  ArrayList<Double> scoresarraylist =  new  ArrayList<Double>();
	    	  
	    	HashMap.Entry pair = (HashMap.Entry)it.next();
	    	logcase mainCase = (logcase) pair.getKey();
	    	log matchinglogs = (log) pair.getValue();
	    	
            
	    	/// add events from main case to mergedcase
	       logcase mergedcase = new logcase("mergedcase"+mainCase.getCaseId());
	    	mergedcase.AddnewAllEvents(mainCase);
	        mergedlog.AddnewCase(mainCase.getCaseId(), mergedcase);
	    	/// iterate over all the candidate cases (the ones already matching temporally)
	    	
	    	
	    
	    	Iterator it2  = matchinglogs.getCases().entrySet().iterator();
	    	
		      while (it2.hasNext()) {
		    	
		    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
		    	String  matchingCase = (String) pair2.getKey();
		    	logcase matchinglogcase = (logcase) pair2.getValue();

		    	 double score = 0 ;
		    	
		    	 ncandidates++;
		    		    	 
		    	 
		    	 if(simfunction.equals("simTF")){
		    		 score = simTF(mainCase.getCaseString(), matchinglogcase.getCaseString(), weights_file1,  weights_file2); 
		    	 }
		    	 else if(simfunction.equals("JA")){
		    		 score = simJA(mainCase.getCaseString(), matchinglogcase.getCaseString());
		    	 }
		    	 
		    	 totalscores = totalscores + score ;
		    	 
		    	 // save score with its corresponding candidate for later
		    	 candidateScores.put(new logcase[] {mainCase,matchinglogcase}, score);
		    	 
		    	 
		    	 /// add scores to select top n later
		    	 scoresarraylist.add(score);
		    	 		    	 
		    	 String scoretolog = score+"";
		    	 
		    	 deb.writeTrace(new Debug(){{
		    		    setClassname(this.getClass().getSimpleName());
		    			setMethod(getMethodName(2));
		    			setTitle("Results from Scoring Function");
		    			setParam1("Main case id");
		    			setValue1(mainCase.getCaseId());
		    			setParam2("Matching case id");
		    			setValue2(matchingCase);
		    			setParam3("Score");
		    			setValue3(scoretolog);
		    		 }});
		    	 
		    	 
		      }      
		      
		      Collections.sort(scoresarraylist);
		      scoresmap.put(mainCase,  scoresarraylist);
		    
	 }
	      
	    
	      
	    
		     
		     
		     Iterator it3  =  candidateScores.entrySet().iterator();
		    	
		      while (it3.hasNext()) {
		    	HashMap.Entry pair3 = (HashMap.Entry)it3.next();
		    	logcase[]  candidateCase = (logcase[]) pair3.getKey();
		    	 Double cscore = (Double) pair3.getValue();
		    	

		    	 
		    	 
		    	   int threshold = (int)(scoresmap.get(candidateCase[0]).size() - scoresmap.get(candidateCase[0]).size()*candidateSelectionRatio) ;
		    			  
		    		      double avscore = scoresmap.get(candidateCase[0]).get(threshold) ;
		    		      
		    			    
		    			     deb.writeTrace(new Debug(){{
		    		    		    setClassname(this.getClass().getSimpleName());
		    		    			setMethod(getMethodName(2));
		    		    			setTitle("Average score");
		    		    			setParam1("Score");
		    		    			setValue1(avscore+"");
		    		    		 }});
		    	 
		    	 if(cscore>=avscore){
		    		 
		    		
		 		    

		    		logcase mergedcase =  mergedlog.getCases().get(candidateCase[0].getCaseId()) ; /// get main events
		    		 mergedcase.AddnewAllEvents(candidateCase[1]); // add matching events to new events
		    		 mergedcase.sort(); // sort
	    	
		    		 
		    		 mergedlog.removeCase(candidateCase[0].getCaseId());
		    		 mergedlog.AddnewCase(candidateCase[0].getCaseId(), mergedcase); // overwrite existing case with new case where main events and matching events are merged
		    		 
		    		 deb.writeTrace(new Debug(){{
		 	    		    setClassname(this.getClass().getSimpleName());
		 	    			setMethod(getMethodName(2));
		 	    			setTitle("Selected Candidates above threshold");
		 	    			setParam1("Main case");
		 	    			setValue1(candidateCase[0].getCaseId());
		 	    			setParam2("Matching case");
		 	    			setValue2(candidateCase[1].getCaseId());
		 	    			setParam3("Sim Score");
		 	    			setValue3(cscore+"");
		 	    			setParam4("Average score");
		 	    			setValue4(avscore+"");
		 	    		 }});
		 		     

		    		 
		    	 } 
		    	 

		    	 
		      }
		      
		      
		  
		  
		      
		  double averagescore = totalscores/ncandidates ;
		  
		     mergedlog.setAverageScore(averagescore);
		     
		 
		     
		     
		return mergedlog; 
		
	}
	
	
	public CSVLog initNewLog(CSVLog file, Labeler labels, String testfile) throws IOException{
	  	utils util = new utils();
		
		
		ArrayList<String> headerdata = new ArrayList<String>();
		
	    headerdata.add("New Case Id");
	 
		headerdata.add(labels.getHeader().get("caseid").get(0));
	  
	
	    
	    
		headerdata.add("New Event Id");
		
		labels.getHeader().get("timestamps").stream().forEach(value -> headerdata.add(value));
		labels.getHeader().get("otherattributes").stream().forEach(value -> headerdata.add(value));
		
		StringBuilder sb = new StringBuilder();
		for(String value : headerdata){
			sb.append(value);
			sb.append(',');
		}
		
		
	
		file.addLine(sb.toString());
		util.WriteToCSVfile(testfile,  headerdata);
		
		return file ;
	}
	
	public  void printNewLog(log MergedLog1andLog2,CSVLog file, Labeler labels, String testfile) throws IOException{
		
		int counter = 0 ;
		
			   Iterator it = MergedLog1andLog2.getCases().entrySet().iterator();
			    while (it.hasNext()) {
			    	HashMap.Entry pair = (HashMap.Entry)it.next();
			    	String id = (String) pair.getKey();
			    	String newid = newids.get(counter);
					printCase(MergedLog1andLog2.getCases().get(id),file,newid,labels,MergedLog1andLog2, testfile);
					counter++;
			    }

	}
	
	public int findPadding(String attr, log eventlog, event event ){
		
		LinkedList<Integer> positions = new  LinkedList<Integer>();
		
		Iterator it2  = eventlog.getCases().entrySet().iterator();
	      while (it2.hasNext()) {
	    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
	    	String  caseid = (String) pair2.getKey();
	    	logcase Case = (logcase) pair2.getValue();
	    		// for each case, iterate over the events in that case
	    	for(event ee: Case.getEvents()){
	    	
	    			if(ee.getOtherattributes().contains(attr) && !event.getEventid().equals(ee.getEventid())){
	    				positions.add(ee.getOtherattributes().indexOf(attr));
	    				
	    			}
	    			    		
	    	}
	      }
	      
	     
	      int bestIndex = positions.size()==0 ? 0 :   getPopularElement(positions);
	      
	    
	

	      if(bestIndex==0 && isInteger(attr) ) {
	    	positions = new LinkedList<Integer>();
	    	  Iterator it3  = eventlog.getCases().entrySet().iterator();
		      while (it3.hasNext()) {
		    	HashMap.Entry pair3 = (HashMap.Entry)it3.next();
		    	String  caseid = (String) pair3.getKey();
		    	logcase Case = (logcase) pair3.getValue();
		    		// for each case, iterate over the events in that case
		    	for(event ee: Case.getEvents()){
		    		for(String col : ee.getOtherattributes()){
		    			if(isInteger(col)){
		    				positions.add(ee.getOtherattributes().indexOf(col));
		    			}
		    		}
		    		}
		      }
		      
			  
		      bestIndex = positions.size()==0 ? 0 :   getPopularElement(positions);
		 
		      
	      }
	   
		return bestIndex ;
	}
	
	public boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public  boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public ArrayList<ArrayList<String>> printCase(logcase Case, CSVLog file,String newid,Labeler labels,log eventlog, String testfile) throws IOException{
	 int count = 0 ;
	 	ArrayList<ArrayList<String>> printedcaseForJunitTestPurpuse = new ArrayList<ArrayList<String>>();
		for(event event : Case.getEvents()){
			count++;
		
			 
			
					utils util = new utils();
				
					ArrayList<String> data = new ArrayList<String>();
					data.add(newid);
					data.add(event.getCaseid());
					data.add(event.getEventid());
					
					// to be changed while deadling with events
					data.add(event.getStarttime());
					data.add(event.getEndtime());
					
					
					// add otherattributes 
					
					int actualpos = 0 ;
					
					for(int i=0; i<labels.getHeader().get("otherattributes").size();i++){
						
						
						
						if(i<event.getOtherattributes().size()){
							

							int kmemory = 0 ;
							int remaining = labels.getHeader().get("otherattributes").size() - i ; // bug here
							for(int k=0 ; k<findPadding(event.getOtherattributes().get(i),eventlog,event)-(actualpos) &&  k<remaining ; k++){
							data.add(" ");
							kmemory = k+1 ;
								
						}
						
							
							data.add(event.getOtherattributes().get(i));
							actualpos +=  kmemory +1;
						}
						
						
						
					}
					

					
					printedcaseForJunitTestPurpuse.add(data);
					
					
					
					
					StringBuilder sb = new StringBuilder();
					for(String value : data){
						sb.append(value);
						sb.append(',');
					}
					file.addLine(sb.toString());
					
					
				util.WriteToCSVfile(testfile, data);
					
					
				
				
		
		}
		return printedcaseForJunitTestPurpuse;
	}
	
	public log mergeFiles(log mainlog, log otherlog1, String simfunc) throws IOException, ParseException{

				
		HashMap<logcase, log > matchingByTime = generateTemporalRelationsPairs(mainlog,otherlog1); // to change if you change log
		
	    log mergedlog = FilterBySimilarityScore(matchingByTime, simfunc, mainlog.generateWeights(), otherlog1.generateWeights(),mainlog.getLogname()+"_"+otherlog1.getLogname(),otherlog1.getSelectionRatio()); // weights_file1, weights_file2 only in case of simTF
			      
				
				  return mergedlog;
		
	}
	
	
	
	public log mapDecorativeData(log eventlog, log decorativelog,String marking, Labeler labels) throws ParseException {
		
		/*
		 *  update log labels with marking as extra col 
		 */
		
		ArrayList<String> otherattributesheader =  labels.getHeader().get("otherattributes");
		otherattributesheader.add(marking);
		labels.getHeader().put("otherattributes", otherattributesheader);
		eventlog.setLabel(labels);
		
		
		
	  // iterate over the decorative log entries
		
		   ArrayList<event> eventsMatchingWithDiscreteDimension = new ArrayList<event>(); // declaration 
	        ArrayList<event> eventsMatchingWithContinuousDimension = new ArrayList<event>(); // declaration 
		
		for(DecorativeEntry de : decorativelog.getDecorativeEntries()) {
			
			// for each decorative attribute
			for(DecorativeAttribute da : de.getAttributes()){
				
				String value = da.getValue();
				String type = da.getType();
		       
		         
		        /// check decorative attribute type
		        if(type.equals("discrete")){
		        	/// if the decorative attribute is discrete
		        	/// find all events in eventlog that contains the same attribute
		        			// iterate over the cases in the event log
		        	Iterator it2  = eventlog.getCases().entrySet().iterator();
				      while (it2.hasNext()) {
				    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
				    	String  caseid = (String) pair2.getKey();
				    	logcase Case = (logcase) pair2.getValue();
				    		// for each case, iterate over the events in that case
				    	for(event ee: Case.getEvents()){
				    		// if the event contains the discrete decorative value, add it to eventsMatchingWithDiscreteDimension

//				    		ee.getAllAttributes().stream().forEach(e -> System.out.print(e+" "));
				    		
				    		if(ee.getAllAttributes().toString().contains(value)){
				    			eventsMatchingWithDiscreteDimension.add(ee);
				    		}
				    	}
				      }
		        	
		        	
		        }
		       
		        else  if(type.equals("continious")){
		        	// if the decorative attribute is continious
		        	
		        	/// find all events in eventlog that intersect with the decorative continous attribute
		        	
		        	// iterate over the cases in the event log
		        	Iterator it2  = eventlog.getCases().entrySet().iterator();
				      while (it2.hasNext()) {
				    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
				    	String  caseid = (String) pair2.getKey();
				    	logcase Case = (logcase) pair2.getValue();
				    		// for each case, iterate over the events in that case
				    	for(event ee: Case.getEvents()){
		    		
				    		String startInterval = ee.getStarttime();
				    		String endInterval = ee.getEndtime();
				    		
				    		// if candidate attribute continous value is in the event attribute interval
				    		if(timeInInterval(value, startInterval, endInterval)){
				    			eventsMatchingWithContinuousDimension.add(ee);
				    		}
				    	
				    	}
				      }
		        	
		        }
		        else {
		        	System.err.println("unkown type");
		        }
		        
		        /// events matching with dimensions are obtained ! 
		         
		        
			}

		}
		
		System.out.println("events matching in discrete dimension");
        for(event ee: eventsMatchingWithDiscreteDimension){
        	System.out.println("event id: "+ee.getEventid());
        }      
        System.out.println("events matching in continous dimension");
        for(event ee: eventsMatchingWithContinuousDimension){
        	System.out.println("event id: "+ee.getEventid());
        }
		 
		// the intersection of eventsMatchingWithDiscreteDimension and eventsMatchingWithContinuousDimension are the events that can be represented into the framework
		
		ArrayList<event> Matchingevents = new ArrayList<event>();
		
		for(event ed : eventsMatchingWithDiscreteDimension){
			for(event ec :eventsMatchingWithContinuousDimension ){
				if(ed.equals(ec)){
					Matchingevents.add(ed);
				}
			}
		}
		
		System.out.println("intersecting events");
		 for(event ee: Matchingevents){
	        	System.out.println("event id: "+ee.getEventid());
	        }
		
		// find intersections between decorative events and log events on the framework
		 log decoratedlog = findDecorativeIntersections(eventlog,decorativelog, Matchingevents,marking);
		 
		 return decoratedlog ;
		  
	}
	
	public log findDecorativeIntersections(log eventlog, log decorativelog,  ArrayList<event> Matchingevents,String marking) throws ParseException{
		   // find intersections
		
		
		
 	for(DecorativeEntry de : decorativelog.getDecorativeEntries()) {
		
 		   /// for each event in Matching events 
 		   for(event ee: Matchingevents) {
 			   
 			  Boolean eventIntersectingInAllAttributes = true ;
 			   
 			 // for each decorative attribute
 				for(DecorativeAttribute da : de.getAttributes()){
 					
 					String value = da.getValue();
 					String type = da.getType();
 					
 					if((type.equals("discrete") && ee.getAllAttributes().toString().contains(value)) || (type.equals("continious") &&  timeInInterval(value, ee.getStarttime(), ee.getEndtime()))){
 	 					eventIntersectingInAllAttributes =  eventIntersectingInAllAttributes && true ;
 						}
 					else {
 						eventIntersectingInAllAttributes = eventIntersectingInAllAttributes && false ;
 					}
 						      
 				}
 			   	
 				
 				if(eventIntersectingInAllAttributes) {
 				    
 					
 					event eOld = ee ;
 					
 					event eNew = new event(ee.getCaseid() ,ee.getEventid(),ee.getStarttime(),ee.getEndtime(),ee.getOtherattributes());
 					eNew.addDecorativeAttribute(marking);
 					
 					
 					eventlog = replace(eventlog, eOld, eNew);
 					
 						
 					System.out.println("match found between log event "+ee.getEventid()+" and the following decorative entry "+de.getAllattributes());
 				
 				
 				}
 				
 				
 		   }
 		
     
	}
 	
 	return  eventlog;
	}
	
	
	public log replace(log eventlog, event eOld, event eNew){
	
		
		Iterator it2  = eventlog.getCases().entrySet().iterator();
	      while (it2.hasNext()) {
	    	HashMap.Entry pair2 = (HashMap.Entry)it2.next();
	    	String  caseid = (String) pair2.getKey();
	    	logcase Case = (logcase) pair2.getValue();
	    		// for each case, iterate over the events in that case
	    	
	    	int eventindex = -1 ;
	    	
	    	for(event ee: Case.getEvents()){
	    		if(ee.equals(eOld)){
	    		   eventindex = Case.getEvents().indexOf(ee);
	    		}
	    	}
	    	
	    	if(eventindex!=-1){
	    		System.out.println("decorated");
	    		Case.getEvents().remove(eventindex);
				Case.AddnewEvent(eNew);
//				eNew.getAllAttributes().stream().forEach(attr -> System.out.println(attr) );
				Case.sort();
	    	}

	      }
	    	

		return eventlog ;
	}
	
	public int getPopularElement(LinkedList<Integer> a)
	{

		
		int popular = 0 ;
		
		for(int k : a){
			if(k>popular) popular = k ;
		}
		
	  return popular;
	}
	
    public  log Hmerger(log mainlog,ArrayList<log> logs, String simfunction) throws IOException, ParseException{

	while (logs.size() > 0) {

		double highestScore = 0;
		MergableLog highcandidateLog = null;

		for (log candidatelog : logs) {

			if (!mainlog.equals(candidatelog)) {

				log mergedlog = mergeFiles(mainlog, candidatelog, simfunction);

				double averageScore = mergedlog.getAverageScore();
				
		
				if (averageScore > highestScore) {
					highestScore = averageScore;
					highcandidateLog = new MergableLog(mergedlog, new log[] { mainlog, candidatelog });
				}
			}
		}

		
		for (log parent : highcandidateLog.getParentfiles()) {
			logs.remove(parent);
		}

		mainlog = highcandidateLog.getMergablelog();

	}

	return mainlog;
    }
	
	
}
