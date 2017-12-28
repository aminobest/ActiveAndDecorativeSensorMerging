package adsm.core.tester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import adsm.core.plugin.CSVLog;
import adsm.core.utils.UnicodeBOMInputStream;
import adsm.datamodel.merger.event;
import adsm.datamodel.merger.log;
import adsm.datamodel.merger.logcase;
import adsm.mergerAndMapper.Matcher;

public class TestAccuracy {

   
	
	public static void main(String[] args) throws Exception {
		String testfile = args[0];
		String groundtruthfile = args[1]+"groudTruth.csv";
		 HashMap<String, HashMap<String, String>> attributesIndexes = new HashMap<String, HashMap<String, String>>(){{
		    	put(testfile,new HashMap<String, String>(){{
		    		put("type","eventlog");
		    		put("selectionRatio","0.2");
		    		put("caseid","0");
		    		put("starttime","1");
		    		put("endtime","2");
		    	}});
		    }};
		    
		    
		    
		    
		    Matcher Merger = new Matcher(1000000,attributesIndexes,"JA");
		    log  mainlog = Merger.processFile(read(testfile),"event");
		    
		    ArrayList<String[]>  groundTfile = readfile(groundtruthfile);
		    ArrayList<String[]>  eventlogfile =  getCasesEvents(mainlog);
		    
		    int totalcases = groundTfile.size();
		    
		    int cassesnotfound = 0 ;
		    
		    for(String[] gCase : groundTfile ){
		    	  boolean found = false ;
		    	  
		    	 if(gCase.length>1){
		    		 
		    	 
		    	 for(String[] eCase : eventlogfile ){
		    		 if(compareArrays(gCase, eCase)){
		    			 found = true ;
		    			 break ;
		    		 }
		    	 }
		    	 
		    	 if(found) {
		    	 }
		    	 
		    		 else {
		    			 cassesnotfound++;
		    	 }
		    		 
		    	 }
		    	 
		    }
		    
		    
		    double accuracy = (totalcases - cassesnotfound)/(double)totalcases;
		    System.out.println("Cases correctly merged "+ (totalcases - cassesnotfound));
		    System.out.println("Cases Wrongly merged "+ (cassesnotfound));
		    System.out.println("Accuracy: "+ accuracy);
		 
		    write(args[1]+"result", (totalcases - cassesnotfound)+"", (cassesnotfound)+"", accuracy+"",args[2]);
		    
	}
	
	
	public static String printArray(String[] arr){
		
		String arraydata = "";

		for (String a : arr)
			arraydata = arraydata + " " + a;

		return arraydata;
		
	}
	
	
public static void write(String filename, String correct, String wrong, String accuracy,String sim) throws IOException{
		
		FileWriter fw = new FileWriter(filename+".csv", true);
		
		StringBuilder sb = new StringBuilder();
	
			sb.append("sim");
			sb.append(',');
			sb.append(sim);
			sb.append(',');
			sb.append("correct");
			sb.append(',');
			sb.append(correct);
			sb.append(',');
			sb.append("wrong");
			sb.append(',');
			sb.append(wrong);
			sb.append(',');
			sb.append("accuracy");
			sb.append(',');
			sb.append(accuracy);
		

			
	        sb.append('\n');

	        fw.write(sb.toString());
	        fw.close();
	
	}
	
	public static boolean compareArrays(String[] arr1, String[] arr2) {
	    HashSet<String> set1 = new HashSet<String>(Arrays.asList(arr1));
	    HashSet<String> set2 = new HashSet<String>(Arrays.asList(arr2));
	    return set1.equals(set2);
	}
	
	public static ArrayList<String[]> getCasesEvents(log mainlog){
		ArrayList<String[]> lines = new ArrayList<String[]>();
		Iterator it =mainlog.getCases().entrySet().iterator();
		while (it.hasNext()) {
			HashMap.Entry pair = (HashMap.Entry) it.next();
			logcase Case = (logcase) pair.getValue();
			String id = (String) pair.getKey();	
			ArrayList<String> line = new ArrayList<String>();
			
			for(event ee : Case.getEvents()){
				line.add(ee.getOtherattributes().get(ee.getOtherattributes().size()-2));
			}
			
			String[] Arr = new String[line.size()];
			Arr = line.toArray(Arr);
			lines.add(Arr);
			
		}
		return lines ;
	}
	
	public static ArrayList<String[]> readfile(String filename) throws IOException{
	
		ArrayList<String[]> lines = new ArrayList<String[]>();
		
		 File file = new File(filename);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line.split(","));
				
			}
		 
		return lines ;
	}
	
	
	
	public static CSVLog read(String file) throws Exception {
		CSVLog log = new CSVLog(file);
		UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(new FileInputStream(file));
		ubis.skipBOM();
		InputStreamReader isr = new InputStreamReader(ubis);
		BufferedReader reader = new BufferedReader(isr);
		String readLine;
		while ((readLine = reader.readLine()) != null) {
			log.addLine(readLine);
		}
		reader.close();
		isr.close();
		ubis.close();
		log.prepare();
		return log;
	}

}
