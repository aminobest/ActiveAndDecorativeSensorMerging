package adsm.datamodel.merger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import adsm.mergerAndMapper.stringsimilarity.tfidf;

public class log {

	HashMap<String, logcase> cases ;
	String logname ;
	double averageScore ;
	Labeler label ;
	HashMap<String,Double> wordweights ;
	double selectionRatio ;
	
	ArrayList<DecorativeEntry> decorativeEntries ;
	
	private String logcontentAsString = "";
	
	

	public log(String logname) {
		this.cases = new HashMap<String, logcase>();
		this.logname = logname ;
		decorativeEntries = new ArrayList<DecorativeEntry>();
		
		
	}
	
	
	public void addDecorativeEntry(DecorativeEntry den){
		decorativeEntries.add(den);
	}
	
	
	
	
	public ArrayList<DecorativeEntry> getDecorativeEntries() {
		return decorativeEntries;
	}


	public  HashMap<String,Double> generateWeights() throws IOException{
		
		cases.forEach((caseid, trace) -> {    
			logcase events = trace ;
			for(event ee : 	events.getEvents()){
				for(String cell : ee.getAllAttributes()){
					logcontentAsString += cell+" ";
				}
			}
		});
		
		HashMap<String,Double> weights = gettf(new String[] {logcontentAsString});

		return  weights;
	}
	
	 public HashMap<String,Double> gettf(String[] data){

			tfidf cat = new tfidf(data);
			HashMap<String,Double> weights = new HashMap<String,Double>();
		
			int count = 0 ;
			for(String wd : cat.getWordVector() ){
				weights.put(wd, cat.getTF_IDFMatrix()[0][count]);
				count++;
			}
			
			/// rows represents the number of documents
			// cols represents the words in word vector
			
			
			return weights ;
		    	
		}
	
	public double getSelectionRatio() {
		return selectionRatio;
	}




	public void setSelectionRatio(double selectionRatio) {
		this.selectionRatio = selectionRatio;
	}




	public void AddnewCase(String caseid, logcase le){
		cases.put(caseid, le);
	}
	
	public void removeCase(String caseid){
		cases.remove(caseid);
	}


	public HashMap<String, logcase> getCases() {
		return cases;
	}


	public String getLogname() {
		return logname;
	}


	public double getAverageScore() {
		return averageScore;
	}


	public Labeler getLabel() {
		return label;
	}


	public void setAverageScore(double averageScore) {
		this.averageScore = averageScore;
	}


	public void setLabel(Labeler label) {
		this.label = label;
	}

	

	


	

	
	
}
