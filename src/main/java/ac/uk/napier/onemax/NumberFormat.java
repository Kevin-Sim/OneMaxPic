package ac.uk.napier.onemax;

import java.text.DecimalFormat;

public class NumberFormat {

	public static String formatNumber(double d) {
		String result = "";		
		DecimalFormat df = new DecimalFormat("##.#####");
		result = df.format(d);
		return result;
	}
	
	public static String formatNumber(int val, int places) {
		String result = "";
		String placesString = "";
		for(int i = 0; i < places; i++){
			placesString += "0";
		}
		DecimalFormat df = new DecimalFormat(placesString);
		result = df.format(val);
		return result;
	}
	
}
