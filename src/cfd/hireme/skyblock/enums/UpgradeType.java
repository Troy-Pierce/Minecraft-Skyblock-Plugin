package cfd.hireme.skyblock.enums;

import java.util.HashMap;
import java.util.Map;

public enum UpgradeType {
	BORDER, MEMBER, VISITOR;
	private static final Map<String, UpgradeType> MAP;
	static {
		MAP = new HashMap<String, UpgradeType>();
		for(UpgradeType type:UpgradeType.values()) {
			MAP.put(type.name(), type);
		}
	}
	
	public static UpgradeType getType(String string) {
		return MAP.get(string.toUpperCase());
	}
}
