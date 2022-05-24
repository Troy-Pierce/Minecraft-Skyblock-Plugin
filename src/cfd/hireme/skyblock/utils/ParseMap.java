package cfd.hireme.skyblock.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseMap {
	public static Map<String, Double> parse(List<String> list){
		Map<String, Double> map = new HashMap<String,Double>();
		for(String string:list) {
			String[] split = string.split(":");
			map.put(split[0], Double.parseDouble(split[1]));
		}
		return map;
	}
}
