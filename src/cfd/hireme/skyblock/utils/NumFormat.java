package cfd.hireme.skyblock.utils;

public class NumFormat {
	public static String format(double i) {
		String string;
		if(i>=1000000) {
			string = Double.toString(i/1000000);
			if(string.substring(string.indexOf('.')+1, string.indexOf('.')+2).equals("0")) {
				string=string.substring(0, string.indexOf('.'))+"m";
				return string;
			}else {
				string=string.substring(0, string.indexOf('.')+2)+"m";
				return string;
			}
		}else if(i>=1000) {
			string = Double.toString(i/1000);
			if(string.substring(string.indexOf('.')+1, string.indexOf('.')+2).equals("0")) {
				string=string.substring(0, string.indexOf('.'))+"k";
				return string;
			}else {
				string=string.substring(0, string.indexOf('.')+2)+"k";
				return string;
			}
		}else {
			return Double.toString(i);
		}
	}
}
