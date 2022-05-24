package cfd.hireme.skyblock.objects.data.challenges;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.enums.ChallengeType;
import cfd.hireme.skyblock.enums.RewardType;
import cfd.hireme.skyblock.utils.Console;

public class ChallengeMob extends ChallengeData{
	EntityType mob;
	public ChallengeMob(String name, ChallengeType type, RewardType rtype, int amount, Object reward, EntityType mob, Material display) {
		super(name, type, rtype, amount, reward, display, null, null);
		// TODO Auto-generated constructor stub
		this.mob=mob;
	}
	public EntityType getMob() {return this.mob;}
	@Override
	protected void finalize() throws Throwable { 
		if(Skyblock.getPlugin(Skyblock.class).getConfig().getBoolean("Primary.Garbage_Collection.notify")) {
			Console.print("Object garbage collected : " + this.toString()); 
		}
	} 
}
