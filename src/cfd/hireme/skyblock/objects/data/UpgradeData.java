package cfd.hireme.skyblock.objects.data;

import org.bukkit.configuration.file.FileConfiguration;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.UpgradeType;
import cfd.hireme.skyblock.objects.main.Island;
import cfd.hireme.skyblock.utils.Console;

public class UpgradeData {
	private Island island;
	private UpgradeType type;
	private FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	public static int maxLevel=4;
	public UpgradeData(Island island, UpgradeType type) {
		this.island=island;
		this.type=type;
	}
	public Island getIsland() {
		return this.island;
	}
	public UpgradeType getType() {
		return this.type;
	}
	public int getCurrentLevel() {
		switch(this.type) {
		case BORDER:
			return this.island.getBorderLevel();
		case MEMBER:
			return this.island.getMemberLevel();
		case VISITOR:
			return this.island.getVisitorLevel();
		default:
			return 0;
		
		}
	}
	public double getCost() {
		switch(this.type) {
		case BORDER:
			return this.island.getBorderLevel()*config.getDouble("island_settings.upgrades.border.cost");
		case MEMBER:
			return this.island.getMemberLevel()*config.getDouble("island_settings.upgrades.member.cost");
		case VISITOR:
			return this.island.getVisitorLevel()*config.getDouble("island_settings.upgrades.visitor.cost");
		default:
			return 0d;
		}
	}
	public int getLevelRequirement() {
		switch(this.type) {
		case BORDER:
			return this.island.getBorderLevel()*config.getInt("island_settings.upgrades.border.level_requirement");
		case MEMBER:
			return this.island.getMemberLevel()*config.getInt("island_settings.upgrades.member.level_requirement");
		case VISITOR:
			return this.island.getVisitorLevel()*config.getInt("island_settings.upgrades.visitor.level_requirement");
		default:
			return 0;
		}
	}
	public boolean isMaxLevel() {
		return getCurrentLevel()>=maxLevel;
	}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
