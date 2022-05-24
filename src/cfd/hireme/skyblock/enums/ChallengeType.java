package cfd.hireme.skyblock.enums;

import java.util.HashMap;
import java.util.Map;

// Dummy Challengetypes are types that are handled by external plugins
// with their progress being handled manually
// such as if a plugin wanted to make a custom challenge that tracks how many blocks a player walks (derpySkyblock has not listeners that track player movement)
// progress would be set manually by the plugin

public enum ChallengeType{
	MINE, SLAY, COLLECT, ISLAND_LEVEL, DUMMY;
	private static final Map<String, ChallengeType> MAP;
	static {
		MAP = new HashMap<String, ChallengeType>();
		for(ChallengeType type:ChallengeType.values()) {
			MAP.put(type.name(), type);
		}
	}
	
	public static ChallengeType getType(String string) {
		return MAP.get(string);
	}
}
