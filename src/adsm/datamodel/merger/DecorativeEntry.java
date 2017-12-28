package adsm.datamodel.merger;

import java.util.ArrayList;

public class DecorativeEntry {

	ArrayList<DecorativeAttribute> attributes;

	public DecorativeEntry() {
		attributes = new ArrayList<DecorativeAttribute>();
	}


	public void addAttribute(DecorativeAttribute attr) {
		attributes.add(attr);
	}


	public ArrayList<DecorativeAttribute> getAttributes() {
		return attributes;
	}
	
	public String getAllattributes(){
		
		String ret = "";
		
		for(DecorativeAttribute d : attributes){
			ret += d.getValue()+" ";
		}
		
		
		
		return ret ;
		
	}
	
	
}
