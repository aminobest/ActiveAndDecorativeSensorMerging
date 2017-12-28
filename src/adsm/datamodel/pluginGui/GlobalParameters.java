package adsm.datamodel.pluginGui;

import java.util.ArrayList;

public class GlobalParameters {

	
	String similarityfunction ;
	String mainlogfile ;
	ArrayList<FileParameters> files ;
	
	
	public GlobalParameters(ArrayList<FileParameters> files) {
		super();
		this.files = files;
	}
	
	
	public ArrayList<FileParameters> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<FileParameters> files) {
		this.files = files;
	}

	
	public String getSimilarityfunction() {
		return similarityfunction;
	}
	public String getMainlogfile() {
		return mainlogfile;
	}
	
	public void setSimilarityfunction(String Similarityfunction) {
		this.similarityfunction = Similarityfunction;
	}
	public void setMainlogfile(String mainlogfile) {
		this.mainlogfile = mainlogfile;
	}
	
	
	
	
	
}

