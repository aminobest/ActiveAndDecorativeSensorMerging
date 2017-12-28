package adsm.core.utils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.processmining.framework.plugin.PluginContext;

/**
 * 
 * @author Andrea Burattin
 */
public class Utils {

	/**
	 * 
	 * @param context
	 * @param message
	 */
	public static void l(PluginContext context, String message) {
		if (context != null) {
			context.log(message);
		}
		System.out.println(new Date().getTime() + " - " + message);
	}
	
	/**
	 * 
	 * @param message
	 */
	public static void l(String message) {
		l(null, message);
	}
	
	/**
	 * 
	 * @param lines
	 * @param separator
	 * @return
	 */
	public static boolean isValidSeparator(String[] lines, String separator) {
		int occurrences = -1;
		boolean found = false;
		for(String line : lines) {
			int count = StringUtils.countMatches(line, separator);
			if (count > 0) {
				if (occurrences == -1) {
					occurrences = count;
				} else {
					if (occurrences != count) {
						return false;
					} else {
						found = true;
					}
				}
			}
		}
		return found;
	}
	
	/**
	 * Returns the harmonic mean of the given list of values
	 * 
	 * @see org.processmining.plugins.joosbuijs.blockminer.genetic.TreeEvaluator
	 * @author jbuijs
	 * @param values the list of values
	 * @return the harmonic mean
	 */
	public static double harmonicMean(List<Double> values) {
		//A list of 1 returns 1.0
		if (values.size() == 1) {
			return values.get(0);
		}
		double sum = 0;
		double product = 1;
		for (Double dub : values) {
			sum += dub;
			product *= dub;
		}

		double numerator = values.size() * product;
		double denominator = sum;
		if (denominator == 0)
			return 0;
		return numerator / denominator;
	}

}
