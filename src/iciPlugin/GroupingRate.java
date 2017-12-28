package iciPlugin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import adsm.core.plugin.CSVLog;

public class GroupingRate {


		
	
	public static BigDecimal GetGroupingScore(CSVLog file, int col){
		
		BigDecimal groupingScore = new BigDecimal(0);
		ArrayList<String> distinctValues = new ArrayList<String>();
		BigDecimal nDistinctValues = new BigDecimal(0);
		
		for(int i=0 ; i<file.getEntries(); i++) {
			
			List<String> event = file.getLine(i);
		
			if(!distinctValues.contains(event.get(col))) {
				distinctValues.add(event.get(col));
				nDistinctValues = nDistinctValues.add(new BigDecimal(1));
			}
		}
		
		BigDecimal one = new BigDecimal(1);
		groupingScore = one.subtract(nDistinctValues.divide(new BigDecimal(file.getEntries()), 10, RoundingMode.HALF_UP));
		
		return  groupingScore ;
	}
	
	

	
}