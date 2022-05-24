package cfd.hireme.skyblock.objects.data.challenges;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.ChallengeType;
import cfd.hireme.skyblock.enums.RewardType;
import cfd.hireme.skyblock.utils.Console;

public class ChallengeData {
	static FileConfiguration config = Skyblock.getPlugin(Skyblock.class).getConfig();
	String name;
	ChallengeType challengeType;
	RewardType rewardType;
	int amount;
	Object reward;
	int amounttocomplete;
	Material display;
	String prefix;
	String suffix;
	// reward is either double or String
	public ChallengeData(String name, ChallengeType type, RewardType rtype, int amount, Object reward, Material display, @Nullable String prefix, @Nullable String suffix) {
		this.name=name;
		this.challengeType=type;
		this.rewardType=rtype;
		this.amounttocomplete=amount;
		this.reward=reward;
		this.display=display;
		this.prefix=prefix;
		this.suffix=suffix;
	}
	public String getName() {return this.name;}
	public ChallengeType getChallengeType() {return this.challengeType;}
	public RewardType getRewardType() {return this.rewardType;}
	public int getRequirement() {return this.amounttocomplete;}
	public Object getReward() {return this.reward;}
	public Material getDisplay() {return this.display;}
	public String getPrefix() {return this.prefix;}
	public String getSuffix() {return this.suffix;}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
