package cfd.hireme.skyblock.enums;

import java.util.HashMap;
import java.util.Map;

public enum RewardType {
	MONEY, MATERIAL;
	private static final Map<String, RewardType> MAP;
	static {
		MAP = new HashMap<String, RewardType>();
		for(RewardType type:RewardType.values()) {
			MAP.put(type.name(), type);
		}
	}
	
	public static RewardType getType(String string) {
		return MAP.get(string);
	}
}
