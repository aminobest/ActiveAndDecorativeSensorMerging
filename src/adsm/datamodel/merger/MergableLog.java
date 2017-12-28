package adsm.datamodel.merger;

public class MergableLog {

	
	  log mergablelog ;
	  log[] parentfiles ;
	  
	  
	public MergableLog(log mergablelog, log[] parentfiles) {
		this.mergablelog = mergablelog;
		this.parentfiles = parentfiles;
	}


	public log getMergablelog() {
		return mergablelog;
	}


	public log[] getParentfiles() {
		return parentfiles;
	}


	public void setMergablelog(log mergablelog) {
		this.mergablelog = mergablelog;
	}


	public void setParentfiles(log[] parentfiles) {
		this.parentfiles = parentfiles;
	}
	  
	  
	  
	
}
