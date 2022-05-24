package cfd.hireme.skyblock.objects.data.challenges;

import org.bukkit.Material;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.ChallengeType;
import cfd.hireme.skyblock.enums.RewardType;
import cfd.hireme.skyblock.utils.Console;

public class ChallengeMaterial extends ChallengeData{
	Material mat;
	public ChallengeMaterial(String name, ChallengeType type, RewardType rtype, int amount, Object reward, Material mat, Material display) {
		super(name, type, rtype, amount, reward, display, null, null);
		// TODO Auto-generated constructor stub
		this.mat=mat;
	}
	public Material getMaterial() {return this.mat;}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
